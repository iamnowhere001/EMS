package com.ems.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.Result;
import com.ems.dto.WorkflowRequestDTO;
import com.ems.entity.EmployeeWorkflowChange;
import com.ems.service.EmployeeWorkflowChangeService;
import com.ems.service.EmployeeWorkflowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class EmployeeWorkflowController {

    private final EmployeeWorkflowService workflowService;
    private final EmployeeWorkflowChangeService changeService;

    public EmployeeWorkflowController(EmployeeWorkflowService workflowService,
                                       EmployeeWorkflowChangeService changeService) {
        this.workflowService = workflowService;
        this.changeService = changeService;
    }

    @PostMapping("/submit")
    public Result<EmployeeWorkflowChange> submit(@RequestBody WorkflowRequestDTO req) {
        return Result.success(workflowService.submit(req));
    }

    @PostMapping("/revoke/{id}")
    public Result<Boolean> revoke(@PathVariable Long id) {
        return Result.success(workflowService.revoke(id));
    }

    @GetMapping("/page")
    public Result<IPage<EmployeeWorkflowChange>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String keyword) {
        return Result.success(workflowService.page(page, size, changeType, keyword));
    }

    @GetMapping("/list-by-employee/{employeeId}")
    public Result<List<EmployeeWorkflowChange>> listByEmployee(@PathVariable Long employeeId) {
        return Result.success(changeService.list(new LambdaQueryWrapper<EmployeeWorkflowChange>()
                .eq(EmployeeWorkflowChange::getEmployeeId, employeeId)
                .eq(EmployeeWorkflowChange::getStatus, 1)
                .orderByDesc(EmployeeWorkflowChange::getEffectiveDate)));
    }
}
