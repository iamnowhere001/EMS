package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.entity.SalaryRecord;
import com.ems.entity.SalaryStructure;
import com.ems.service.SalaryRecordService;
import com.ems.service.SalaryStructureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryStructureService salaryStructureService;
    private final SalaryRecordService salaryRecordService;

    public SalaryController(SalaryStructureService salaryStructureService,
                           SalaryRecordService salaryRecordService) {
        this.salaryStructureService = salaryStructureService;
        this.salaryRecordService = salaryRecordService;
    }

    // ===== 薪资结构 =====

    @GetMapping("/structure/{employeeId}")
    @RequiresPermission("salary:view")
    public Result<SalaryStructure> getStructure(@PathVariable Long employeeId) {
        return Result.success(salaryStructureService.getByEmployeeId(employeeId));
    }

    @PostMapping("/structure")
    @RequiresPermission("salary:manage")
    public Result<String> saveStructure(@RequestParam Long employeeId, @RequestBody SalaryStructure structure) {
        salaryStructureService.saveOrUpdate(employeeId, structure);
        return Result.success("保存成功");
    }

    // ===== 月度工资 =====

    @GetMapping("/record/page")
    @RequiresPermission("salary:view")
    public Result<IPage<SalaryRecord>> recordPage(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(salaryRecordService.pageQuery(employeeId, yearMonth, status, page, size));
    }

    @GetMapping("/record/list")
    @RequiresPermission("salary:view")
    public Result<List<SalaryRecord>> recordList(@RequestParam Long employeeId) {
        return Result.success(salaryRecordService.listByEmployeeId(employeeId));
    }

    @PostMapping("/generate")
    @RequiresPermission("salary:manage")
    public Result<String> generateMonthlySalary(@RequestParam String yearMonth) {
        salaryRecordService.generateMonthlySalary(yearMonth);
        return Result.success("工资生成成功");
    }

    @PostMapping("/confirm")
    @RequiresPermission("salary:confirm")
    public Result<String> confirmSalary(@RequestParam Long id) {
        salaryRecordService.confirmSalary(id);
        return Result.success("确认成功");
    }

    @PostMapping("/batchConfirm")
    @RequiresPermission("salary:confirm")
    public Result<String> batchConfirm(@RequestBody List<Long> ids) {
        salaryRecordService.batchConfirm(ids);
        return Result.success("批量确认成功");
    }

    @PostMapping("/batchPay")
    @RequiresPermission("salary:pay")
    public Result<String> batchPay(@RequestBody List<Long> ids) {
        salaryRecordService.batchPay(ids);
        return Result.success("批量发放成功");
    }

    @PutMapping("/record")
    @RequiresPermission("salary:manage")
    public Result<String> updateRecord(@RequestBody SalaryRecord record) {
        salaryRecordService.updateById(record);
        return Result.success("更新成功");
    }
}
