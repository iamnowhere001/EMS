package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.dto.WorkflowRequestDTO;
import com.ems.entity.Employee;
import com.ems.entity.EmployeeLeave;
import com.ems.entity.EmployeeWorkflowChange;
import com.ems.mapper.EmployeeWorkflowChangeMapper;
import com.ems.service.EmployeeLeaveService;
import com.ems.service.EmployeeService;
import com.ems.service.EmployeeWorkflowChangeService;
import com.ems.service.EmployeeWorkflowService;
import com.ems.util.EmployeeChangeLogUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeWorkflowServiceImpl
        extends ServiceImpl<EmployeeWorkflowChangeMapper, EmployeeWorkflowChange>
        implements EmployeeWorkflowService {

    private static final String TYPE_TRANSFER = "TRANSFER";
    private static final String TYPE_ADJUST_SALARY = "ADJUST_SALARY";
    private static final String TYPE_CONFIRM = "CONFIRM";
    private static final String TYPE_LEAVE = "LEAVE";

    private final EmployeeService employeeService;
    private final EmployeeWorkflowChangeService changeService;
    private final EmployeeLeaveService leaveService;
    private final ObjectMapper objectMapper;

    public EmployeeWorkflowServiceImpl(EmployeeService employeeService,
                                        EmployeeWorkflowChangeService changeService,
                                        EmployeeLeaveService leaveService,
                                        ObjectMapper objectMapper) {
        this.employeeService = employeeService;
        this.changeService = changeService;
        this.leaveService = leaveService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public EmployeeWorkflowChange submit(WorkflowRequestDTO req) {
        if (req == null) return null;
        Employee emp = employeeService.getById(req.getEmployeeId());
        if (emp == null) throw new IllegalArgumentException("员工不存在");

        Employee before = clone(emp);
        EmployeeWorkflowChange change = req.toEntity();

        switch (req.getChangeType()) {
            case TYPE_TRANSFER: applyTransfer(emp, req, change); break;
            case TYPE_ADJUST_SALARY: applyAdjustSalary(emp, req, change); break;
            case TYPE_CONFIRM: applyConfirm(emp, change); break;
            case TYPE_LEAVE: applyLeave(emp, req, change); break;
            default: throw new IllegalArgumentException("未知变更类型: " + req.getChangeType());
        }

        // 持久化主表与变更台账
        employeeService.updateById(emp);
        change.setBeforeValue(toJson(snapshot(before)));
        change.setAfterValue(toJson(snapshot(emp)));
        changeService.save(change);
        return change;
    }

    @Override
    @Transactional
    public boolean revoke(Long changeId) {
        EmployeeWorkflowChange change = changeService.getById(changeId);
        if (change == null || change.getStatus() == 0) return false;
        change.setStatus(0);
        changeService.updateById(change);
        // 注意：当前不逆向回滚主表（避免数据不一致），如需完整撤销建议人工处理
        return true;
    }

    @Override
    public IPage<EmployeeWorkflowChange> page(Integer page, Integer size, String changeType, String keyword) {
        LambdaQueryWrapper<EmployeeWorkflowChange> q = new LambdaQueryWrapper<>();
        if (changeType != null && !changeType.isEmpty()) {
            q.eq(EmployeeWorkflowChange::getChangeType, changeType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            // 简易关键字过滤：员工姓名/工号/变更摘要
            q.and(w -> w.like(EmployeeWorkflowChange::getChangeSummary, keyword)
                    .or().like(EmployeeWorkflowChange::getApplicant, keyword));
        }
        q.orderByDesc(EmployeeWorkflowChange::getEffectiveDate);
        IPage<EmployeeWorkflowChange> result = changeService.page(new Page<>(page, size), q);
        // 填充员工姓名/工号，便于前端展示
        if (result != null && result.getRecords() != null) {
            for (EmployeeWorkflowChange c : result.getRecords()) {
                if (c.getEmployeeId() != null) {
                    Employee e = employeeService.getById(c.getEmployeeId());
                    if (e != null) {
                        c.setEmployeeName(e.getName());
                        c.setEmployeeNo(e.getEmployeeNo());
                    }
                }
            }
        }
        return result;
    }

    // ============== 各类型处理逻辑 ==============

    private void applyTransfer(Employee emp, WorkflowRequestDTO req, EmployeeWorkflowChange change) {
        Map<String, String> before = new HashMap<>();
        Map<String, String> after = new HashMap<>();
        StringBuilder summary = new StringBuilder();

        if (req.getToDepartment() != null && !req.getToDepartment().equals(emp.getDepartment())) {
            before.put("部门", emp.getDepartment());
            after.put("部门", req.getToDepartment());
            emp.setDepartment(req.getToDepartment());
        }
        if (req.getToPosition() != null && !req.getToPosition().equals(emp.getPosition())) {
            before.put("职位", emp.getPosition());
            after.put("职位", req.getToPosition());
            emp.setPosition(req.getToPosition());
        }
        if (req.getToRank() != null && !req.getToRank().equals(emp.getRank())) {
            before.put("职级", emp.getRank());
            after.put("职级", req.getToRank());
            emp.setRank(req.getToRank());
        }
        appendSummary(summary, before, after);
        change.setChangeSummary(summary.length() == 0 ? "调岗（无字段变化）" : summary.toString());
        // change 关联 employee 同步工资位置 (使用工位/工作地)
        if (req.getWorkLocation() != null) {
            emp.setCurrentAddress(emp.getCurrentAddress()); // 不直接覆盖地址
        }
    }

    private void applyAdjustSalary(Employee emp, WorkflowRequestDTO req, EmployeeWorkflowChange change) {
        BigDecimal newSal = req.getToSalary() == null ? emp.getSalary() : req.getToSalary();
        BigDecimal oldSal = emp.getSalary();
        emp.setSalary(newSal);
        String summary = "薪资: " + formatSalary(oldSal) + " → " + formatSalary(newSal);
        if (oldSal != null && oldSal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = newSal.subtract(oldSal)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(oldSal, 2, RoundingMode.HALF_UP);
            summary += "（" + (pct.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + pct + "%）";
        }
        change.setChangeSummary(summary);
    }

    private void applyConfirm(Employee emp, EmployeeWorkflowChange change) {
        // 转正 = 状态保持在职 + 不变其他字段
        change.setChangeSummary("试用期转正：员工由试用期转为正式员工（" + emp.getName() + "）");
    }

    private void applyLeave(Employee emp, WorkflowRequestDTO req, EmployeeWorkflowChange change) {
        emp.setStatus(0);
        EmployeeLeave leave = req.getLeave();
        if (leave == null) {
            throw new IllegalArgumentException("离职申请必须填写离职信息");
        }
        leave.setEmployeeId(emp.getId());
        leave.setApprover(req.getApprover());
        leave.setApproveDate(req.getApproveDate() == null ? java.time.LocalDate.now() : req.getApproveDate());
        leave.setApplicant(req.getApplicant());
        leaveService.save(leave);
        change.setChangeSummary("离职: " + emp.getName() + "（" + (leave.getLeaveType() == null ? "主动离职" : leave.getLeaveType()) + "），最后工作日 " + leave.getLastWorkDate());
    }

    // ============== helpers ==============

    private Employee clone(Employee src) {
        Employee c = new Employee();
        c.setId(src.getId());
        c.setName(src.getName());
        c.setDepartment(src.getDepartment());
        c.setPosition(src.getPosition());
        c.setRank(src.getRank());
        c.setSalary(src.getSalary());
        c.setStatus(src.getStatus());
        c.setPhone(src.getPhone());
        c.setEmail(src.getEmail());
        c.setHireDate(src.getHireDate());
        return c;
    }

    private Map<String, Object> snapshot(Employee e) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", e.getName());
        m.put("department", e.getDepartment());
        m.put("position", e.getPosition());
        m.put("rank", e.getRank());
        m.put("salary", e.getSalary());
        m.put("status", e.getStatus());
        return m;
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private void appendSummary(StringBuilder sb, Map<String, String> before, Map<String, String> after) {
        boolean first = true;
        for (Map.Entry<String, String> en : after.entrySet()) {
            String k = en.getKey();
            String b = before.get(k);
            String a = en.getValue();
            if (a == null) continue;
            if (!first) sb.append("、");
            sb.append(k).append(": ").append(b == null ? "空" : b).append(" → ").append(a);
            first = false;
        }
    }

    private String formatSalary(BigDecimal v) {
        if (v == null) return "空";
        return "¥" + String.format("%,.2f", v);
    }
}
