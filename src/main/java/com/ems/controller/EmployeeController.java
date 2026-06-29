package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.AuthContext;
import com.ems.common.BusinessException;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.dto.BatchOperationDTO.AdjustSalary;
import com.ems.dto.BatchOperationDTO.Transfer;
import com.ems.dto.EmployeeFormDTO;
import com.ems.entity.Employee;
import com.ems.entity.User;
import com.ems.service.EmployeeService;
import com.ems.service.OperationLogService;
import com.ems.service.UserService;
import com.ems.util.EmployeeChangeLogUtil;
import com.ems.vo.EmployeeDetailVO;
import com.ems.vo.EmployeeStatisticsVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final OperationLogService operationLogService;
    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, OperationLogService operationLogService, UserService userService) {
        this.employeeService = employeeService;
        this.operationLogService = operationLogService;
        this.userService = userService;
    }

    @PostMapping
    @RequiresPermission("employee:create")
    public Result<Void> save(@RequestBody @Valid EmployeeFormDTO form) {
        Employee employee = form.getEmployee();
        boolean success = employeeService.addEmployee(form);
        if (success) {
            operationLogService.log("员工管理", "新增", "新增员工：" + (employee != null ? employee.getName() : ""));
        }
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    @RequiresPermission("employee:edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid EmployeeFormDTO form) {
        Employee oldEmployee = employeeService.getById(id);
        Employee employee = form.getEmployee();
        if (employee == null) {
            return Result.error("参数错误");
        }
        employee.setId(id);
        employee.setEmployeeNo(null);
        boolean success = employeeService.updateEmployee(form);
        if (success) {
            String changedFields = EmployeeChangeLogUtil.buildChangedFields(oldEmployee, employee);
            String content = "更新员工：" + employee.getName();
            if (!changedFields.isEmpty()) {
                content += "（" + changedFields + "）";
            }
            operationLogService.log("员工管理", "更新", content);
        }
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("employee:delete")
    public Result<Void> delete(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        boolean success = employeeService.deleteEmployee(id);
        if (success) {
            operationLogService.log("员工管理", "删除", "删除员工：" + (employee != null ? employee.getName() : id));
        }
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @RequiresPermission("employee:delete")
    public Result<Void> deleteBatch(@RequestParam List<Long> ids) {
        boolean success = employeeService.deleteEmployees(ids);
        if (success) {
            operationLogService.log("员工管理", "批量删除", "批量删除员工，数量：" + ids.size());
        }
        return success ? Result.success() : Result.error("批量删除失败");
    }

    @PostMapping("/batch/transfer")
    @RequiresPermission("employee:edit")
    public Result<Integer> batchTransfer(@RequestBody @Valid Transfer dto) {
        int count = employeeService.batchTransfer(dto.getIds(), dto.getDepartment(), dto.getPosition());
        operationLogService.log("员工管理", "批量调岗",
                "批量调岗至 [" + dto.getDepartment() + " · " + dto.getPosition() + "]，共 " + count + " 人");
        return Result.success(count);
    }

    @PostMapping("/batch/adjust-salary")
    @RequiresPermission("salary:manage")
    public Result<Integer> batchAdjustSalary(@RequestBody @Valid AdjustSalary dto) {
        int count = employeeService.batchAdjustSalary(dto.getIds(), dto.getMode(), dto.getAmount());
        String modeDesc;
        switch (dto.getMode()) {
            case "fixed": modeDesc = "固定金额 " + dto.getAmount(); break;
            case "percent": modeDesc = "百分比 " + dto.getAmount() + "%"; break;
            case "set": modeDesc = "设置为 " + dto.getAmount(); break;
            default: modeDesc = dto.getMode();
        }
        operationLogService.log("员工管理", "批量调薪",
                "批量调薪（" + modeDesc + "），共 " + count + " 人");
        return Result.success(count);
    }

    @GetMapping("/{id}")
    @RequiresPermission("employee:detail")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        employeeService.decryptSensitiveData(employee);
        return Result.success(employee);
    }

    @GetMapping("/me")
    @RequiresPermission("personal:view")
    public Result<Employee> getMyProfile() {
        Employee employee = employeeService.getById(resolveCurrentEmployeeId());
        employeeService.decryptSensitiveData(employee);
        return Result.success(employee);
    }

    @PutMapping("/me")
    @RequiresPermission("personal:view")
    public Result<Void> updateMyProfile(@RequestBody EmployeeFormDTO form) {
        Long employeeId = resolveCurrentEmployeeId();
        Employee existing = employeeService.getById(employeeId);
        Employee employee = form.getEmployee();
        if (existing == null || employee == null) {
            return Result.error("参数错误");
        }
        existing.setPhone(employee.getPhone());
        existing.setEmail(employee.getEmail());
        existing.setEmergencyContact(employee.getEmergencyContact());
        existing.setEmergencyPhone(employee.getEmergencyPhone());
        existing.setCurrentAddress(employee.getCurrentAddress());
        boolean success = employeeService.updateById(existing);
        if (success) {
            operationLogService.log("个人中心", "更新资料", "员工更新个人联系资料：" + existing.getName());
        }
        return success ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/{id}/detail")
    @RequiresPermission("employee:detail")
    public Result<EmployeeDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(employeeService.getDetail(id));
    }

    @GetMapping("/list")
    @RequiresPermission("employee:view")
    public Result<List<Employee>> list() {
        List<Employee> list = employeeService.list();
        employeeService.decryptSensitiveData(list);
        return Result.success(list);
    }

    @GetMapping("/page")
    @RequiresPermission("employee:view")
    public Result<IPage<Employee>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeNo,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        IPage<Employee> result = employeeService.pageQuery(name, employeeNo, department, position, status, page, size, sortField, sortOrder);
        return Result.success(result);
    }

    @PostMapping("/sort")
    @RequiresPermission("employee:edit")
    public Result<Void> sort(@RequestBody List<Long> ids) {
        employeeService.updateSortOrder(ids);
        operationLogService.log("员工管理", "排序", "调整员工排序顺序，数量：" + ids.size());
        return Result.success();
    }

    @GetMapping("/statistics")
    @RequiresPermission("employee:view")
    public Result<EmployeeStatisticsVO> statistics() {
        return Result.success(employeeService.statistics());
    }

    @GetMapping("/reminders")
    @RequiresPermission("employee:view")
    public Result<com.ems.vo.ReminderVO> reminders() {
        return Result.success(employeeService.reminders());
    }

    @GetMapping("/export")
    @RequiresPermission("employee:export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        employeeService.exportExcel(response);
        operationLogService.log("员工管理", "导出", "导出员工信息");
    }

    @PostMapping("/import")
    @RequiresPermission("employee:import")
    public Result<Void> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        employeeService.importExcel(file);
        operationLogService.log("员工管理", "导入", "导入员工信息");
        return Result.success();
    }

    private Long resolveCurrentEmployeeId() {
        User user = userService.getById(AuthContext.getUserId());
        if (user == null || user.getEmployeeId() == null) {
            throw new BusinessException(400, "当前账号未绑定员工档案");
        }
        return user.getEmployeeId();
    }
}
