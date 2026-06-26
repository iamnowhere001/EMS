package com.ems.controller;

import com.ems.common.RequireRole;
import com.ems.common.Result;
import com.ems.common.RoleConstants;
import com.ems.entity.Department;
import com.ems.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/tree")
    public Result<List<Department>> tree() {
        return Result.success(departmentService.buildTree());
    }

    @GetMapping
    public Result<List<Department>> list() {
        return Result.success(departmentService.getAllWithEmployeeCount());
    }

    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @GetMapping("/code/{code}")
    public Result<Department> getByCode(@PathVariable String code) {
        return Result.success(departmentService.getByCode(code));
    }

    @PostMapping
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> create(@RequestBody Department department) {
        departmentService.saveDepartment(department);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleConstants.ADMIN)
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}