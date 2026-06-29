package com.ems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ems.common.AuthContext;
import com.ems.common.BusinessException;
import com.ems.common.RequiresPermission;
import com.ems.common.Result;
import com.ems.common.RoleConstants;
import com.ems.entity.User;
import com.ems.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    @RequiresPermission("system:manage")
    public Result<IPage<User>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        IPage<User> result = userService.pageQuery(page, size, username, role, status);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @RequiresPermission("system:manage")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    @RequiresPermission("system:manage")
    public Result<Void> create(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (userService.getByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        String pwd = user.getPassword() == null || user.getPassword().isEmpty() ? "123456" : user.getPassword();
        if (pwd.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于 6 位");
        }
        user.setPassword(passwordEncoder.encode(pwd));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole(RoleConstants.USER);
        }
        if (user.getStatus() == null) user.setStatus(1);
        userService.save(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequiresPermission("system:manage")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        User exist = userService.getById(id);
        if (exist == null) {
            throw new BusinessException(400, "用户不存在");
        }
        if (exist.getUsername().equals("admin") && user.getRole() != null
                && !RoleConstants.isAdmin(user.getRole())) {
            throw new BusinessException(400, "不能修改超级管理员角色");
        }
        if (exist.getUsername().equals("admin") && user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(400, "不能禁用超级管理员");
        }
        user.setId(id);
        user.setPassword(null);
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("system:manage")
    public Result<Void> delete(@PathVariable Long id) {
        User exist = userService.getById(id);
        if (exist == null) {
            throw new BusinessException(400, "用户不存在");
        }
        if ("admin".equals(exist.getUsername())) {
            throw new BusinessException(400, "不能删除超级管理员");
        }
        userService.removeById(id);
        return Result.success();
    }

    @PostMapping("/reset-password/{id}")
    @RequiresPermission("system:manage")
    public Result<Void> resetPassword(@PathVariable Long id) {
        User exist = userService.getById(id);
        if (exist == null) {
            throw new BusinessException(400, "用户不存在");
        }
        exist.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(exist);
        return Result.success();
    }

    @PostMapping("/change-password")
    public Result<Map<String, String>> changePassword(@RequestBody Map<String, String> body) {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new BusinessException(400, "参数不完整");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        Map<String, String> data = new HashMap<>();
        data.put("message", "密码修改成功，请重新登录");
        return Result.success(data);
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        User user = userService.getById(userId);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }
}
