package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.RequireRole;
import com.ems.common.Result;
import com.ems.common.RoleConstants;
import com.ems.dto.BatchOperationDTO;
import com.ems.dto.EmployeeFormDTO;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import com.ems.service.OperationLogService;
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

    public EmployeeController(EmployeeService employeeService, OperationLogService operationLogService) {
        this.employeeService = employeeService;
        this.operationLogService = operationLogService;
    }

    @PostMapping
    public Result<Void> save(@RequestBody @Valid EmployeeFormDTO form) {
        Employee employee = form.getEmployee();
        boolean success = employeeService.addEmployee(form);
        if (success) {
            operationLogService.log("员工管理", "新增", "新增员工：" + (employee != null ? employee.getName() : ""));
        }
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping("/{id}")
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
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> delete(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        boolean success = employeeService.deleteEmployee(id);
        if (success) {
            operationLogService.log("员工管理", "删除", "删除员工：" + (employee != null ? employee.getName() : id));
        }
        return success ? Result.success() : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> deleteBatch(@RequestParam List<Long> ids) {
        boolean success = employeeService.deleteEmployees(ids);
        if (success) {
            operationLogService.log("员工管理", "批量删除", "批量删除员工，数量：" + ids.size());
        }
        return success ? Result.success() : Result.error("批量删除失败");
    }

    @PostMapping("/batch/transfer")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Integer> batchTransfer(@RequestBody @Valid BatchOperationDTO.Transfer dto) {
        int count = employeeService.batchTransfer(dto.getIds(), dto.getDepartment(), dto.getPosition());
        operationLogService.log("员工管理", "批量调岗",
                "批量调岗至 [" + dto.getDepartment() + " · " + dto.getPosition() + "]，共 " + count + " 人");
        return Result.success(count);
    }

    @PostMapping("/batch/adjust-salary")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Integer> batchAdjustSalary(@RequestBody @Valid BatchOperationDTO.AdjustSalary dto) {
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
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        employeeService.decryptSensitiveData(employee);
        return Result.success(employee);
    }

    @GetMapping("/{id}/detail")
    public Result<EmployeeDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(employeeService.getDetail(id));
    }

    @GetMapping("/list")
    public Result<List<Employee>> list() {
        List<Employee> list = employeeService.list();
        employeeService.decryptSensitiveData(list);
        return Result.success(list);
    }

    @GetMapping("/page")
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
    public Result<Void> sort(@RequestBody List<Long> ids) {
        employeeService.updateSortOrder(ids);
        operationLogService.log("员工管理", "排序", "调整员工排序顺序，数量：" + ids.size());
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<EmployeeStatisticsVO> statistics() {
        return Result.success(employeeService.statistics());
    }

    @GetMapping("/reminders")
    public Result<com.ems.vo.ReminderVO> reminders() {
        return Result.success(employeeService.reminders());
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        employeeService.exportExcel(response);
        operationLogService.log("员工管理", "导出", "导出员工信息");
    }

    @PostMapping("/import")
    public Result<Void> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        employeeService.importExcel(file);
        operationLogService.log("员工管理", "导入", "导入员工信息");
        return Result.success();
    }
}
