package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.common.BusinessException;
import com.ems.common.JwtUtil;
import com.ems.common.RoleConstants;
import com.ems.entity.Employee;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import com.ems.service.LoginAttemptService;
import com.ems.service.OperationLogService;
import com.ems.service.RoleService;
import com.ems.service.TokenBlacklistService;
import com.ems.service.UserService;
import com.ems.vo.UserInfoVO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private OperationLogService operationLogService;

    public UserServiceImpl(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService,
                           LoginAttemptService loginAttemptService, RoleService roleService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.loginAttemptService = loginAttemptService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void initDefaultUser() {
        User admin = getByUsername("admin");
        if (admin == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname("管理员");
            user.setRole(RoleConstants.ADMIN);
            user.setStatus(1);
            this.save(user);
        }
    }

    @Override
    public UserInfoVO login(String username, String password, String ip) {
        if (loginAttemptService.isAccountLocked(username)) {
            int attempts = loginAttemptService.getFailedAttempts(username);
            throw new BusinessException(423, "账户已被锁定，请稍后再试（已失败 " + attempts + " 次）");
        }

        User user = getByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username, ip);
            int attempts = loginAttemptService.getFailedAttempts(username);
            
            operationLogService.logWithOperator(
                    username,
                    ip,
                    "认证管理",
                    "登录失败",
                    "登录失败：用户名或密码错误（" + username + "），失败次数：" + attempts
            );
            
            String message;
            if (loginAttemptService.isAccountLocked(username)) {
                message = "账户已被锁定，请稍后再试";
            } else {
                message = "用户名或密码错误，还剩 " + (5 - attempts) + " 次尝试机会";
            }
            throw new BusinessException(400, message);
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            operationLogService.logWithOperator(
                    username,
                    ip,
                    "认证管理",
                    "登录失败",
                    "登录失败：账号已被禁用（" + username + "）"
            );
            throw new BusinessException(400, "账号已被禁用");
        }

        loginAttemptService.loginSuccess(username);

        // 规范化角色编码（兼容旧数据）
        String normalizedRole = normalizeRole(user.getRole());

        // 加载权限列表
        java.util.List<String> permissions = roleService.getPermissionCodesByRoleCode(normalizedRole);
        if (permissions == null) permissions = new java.util.ArrayList<>();

        int version = tokenBlacklistService.getNextUserVersion(user.getId());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), normalizedRole, version, permissions);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmployeeId(user.getEmployeeId());
        vo.setRole(normalizedRole);
        vo.setPermissions(permissions);
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        return vo;
    }

    /**
     * 规范化角色编码（兼容数据库中存储的旧值）
     */
    private String normalizeRole(String role) {
        if (role == null) return RoleConstants.EMPLOYEE;
        if ("admin".equalsIgnoreCase(role) || "ADMIN".equals(role)) return RoleConstants.SUPER_ADMIN;
        if ("user".equalsIgnoreCase(role) || "USER".equals(role)) return RoleConstants.EMPLOYEE;
        return role;
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public User ensureDefaultEmployeeAccount(Employee employee) {
        if (employee == null || employee.getId() == null || !StringUtils.hasText(employee.getName())) {
            return null;
        }

        User existing = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmployeeId, employee.getId())
                .last("LIMIT 1"));
        if (existing != null) {
            existing.setNickname(employee.getName());
            if (!StringUtils.hasText(existing.getRole())) {
                existing.setRole(RoleConstants.EMPLOYEE);
            }
            if (existing.getStatus() == null) {
                existing.setStatus(1);
            }
            this.updateById(existing);
            return existing;
        }

        User user = new User();
        user.setUsername(resolveDefaultEmployeeUsername(employee));
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNickname(employee.getName());
        user.setEmployeeId(employee.getId());
        user.setDeptDataScope(0);
        user.setRole(RoleConstants.EMPLOYEE);
        user.setStatus(1);
        user.setRemark("员工档案创建时自动生成，默认密码：123456");
        this.save(user);
        return user;
    }

    private String resolveDefaultEmployeeUsername(Employee employee) {
        String base = employee.getName().trim();
        User sameNameUser = getByUsername(base);
        if (sameNameUser == null || employee.getId().equals(sameNameUser.getEmployeeId())) {
            return base;
        }

        String suffix = StringUtils.hasText(employee.getEmployeeNo())
                ? employee.getEmployeeNo()
                : String.valueOf(employee.getId());
        String candidate = base + "_" + suffix;
        int seq = 1;
        while (true) {
            User user = getByUsername(candidate);
            if (user == null || employee.getId().equals(user.getEmployeeId())) {
                return candidate;
            }
            candidate = base + "_" + suffix + "_" + seq++;
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException(400, "新密码长度不能少于 6 位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public IPage<User> pageQuery(Integer page, Integer size, String username, String role, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public void logout(Long userId, String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            Long expirationTime = jwtUtil.getExpirationDate(refreshToken) != null ? jwtUtil.getExpirationDate(refreshToken).getTime() : System.currentTimeMillis() + jwtUtil.getRefreshExpiration();
            tokenBlacklistService.invalidateRefreshToken(refreshToken, expirationTime);
        }
    }
}
