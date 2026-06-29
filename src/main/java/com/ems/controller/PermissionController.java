package com.ems.controller;

import com.ems.common.Result;
import com.ems.common.RequiresPermission;
import com.ems.entity.Permission;
import com.ems.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @RequiresPermission("system:role")
    @GetMapping("/list")
    public Result<List<Permission>> listAll() {
        return Result.success(permissionService.listAll());
    }

    @RequiresPermission("system:role")
    @GetMapping("/menus")
    public Result<List<Permission>> listMenus() {
        return Result.success(permissionService.listMenus());
    }

    @RequiresPermission("system:role")
    @GetMapping("/tree")
    public Result<List<Permission>> getTree() {
        return Result.success(permissionService.getTree());
    }

    @RequiresPermission("system:role")
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @RequiresPermission("system:role")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success("保存成功");
    }

    @RequiresPermission("system:role")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Permission permission) {
        permissionService.update(permission);
        return Result.success("更新成功");
    }

    @RequiresPermission("system:role")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success("删除成功");
    }
}
