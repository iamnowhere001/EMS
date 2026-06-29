package com.ems.controller;

import com.ems.common.RequiresPermission;
import com.ems.common.Result;
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
    @RequiresPermission("org:view")
    public Result<List<Department>> tree() {
        return Result.success(departmentService.buildTree());
    }

    @GetMapping
    @RequiresPermission("org:view")
    public Result<List<Department>> list() {
        return Result.success(departmentService.getAllWithEmployeeCount());
    }

    @GetMapping("/{id}")
    @RequiresPermission("org:view")
    public Result<Department> getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    @GetMapping("/code/{code}")
    @RequiresPermission("org:view")
    public Result<Department> getByCode(@PathVariable String code) {
        return Result.success(departmentService.getByCode(code));
    }

    @PostMapping
    @RequiresPermission("org:manage")
    public Result<Void> create(@RequestBody Department department) {
        departmentService.saveDepartment(department);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequiresPermission("org:manage")
    public Result<Void> update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("org:manage")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}
