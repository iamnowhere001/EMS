package com.ems.controller;

import com.ems.common.Result;
import com.ems.common.RequiresPermission;
import com.ems.entity.Permission;
import com.ems.entity.Role;
import com.ems.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @RequiresPermission("system:role")
    @GetMapping("/list")
    public Result<List<Role>> listAll() {
        return Result.success(roleService.listAll());
    }

    @RequiresPermission("system:role")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @RequiresPermission("system:role")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("保存成功");
    }

    @RequiresPermission("system:role")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success("更新成功");
    }

    @RequiresPermission("system:role")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功");
    }

    @RequiresPermission("system:role")
    @GetMapping("/permissions/{roleId}")
    public Result<List<Permission>> getPermissions(@PathVariable Long roleId) {
        return Result.success(roleService.getRolePermissions(roleId));
    }

    @RequiresPermission("system:role")
    @PostMapping("/assign-permissions")
    public Result<String> assignPermissions(@RequestParam Long roleId,
                                           @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success("权限分配成功");
    }
}
