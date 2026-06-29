package com.ems.controller;

import com.ems.common.AuthContext;
import com.ems.common.JwtUtil;
import com.ems.common.Result;
import com.ems.dto.LoginDTO;
import com.ems.dto.RefreshTokenDTO;
import com.ems.entity.User;
import com.ems.interceptor.LoginInterceptor;
import com.ems.service.OperationLogService;
import com.ems.service.RoleService;
import com.ems.service.TokenBlacklistService;
import com.ems.service.UserService;
import com.ems.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final OperationLogService operationLogService;
    private final RoleService roleService;

    public AuthController(UserService userService, JwtUtil jwtUtil,
                          TokenBlacklistService tokenBlacklistService,
                          OperationLogService operationLogService,
                          RoleService roleService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.operationLogService = operationLogService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public Result<UserInfoVO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        String ip = LoginInterceptor.resolveClientIp(request);
        UserInfoVO userInfo = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), ip);
        operationLogService.logWithOperator(
                userInfo.getUsername(),
                ip,
                "认证管理",
                "登录成功",
                "用户 " + userInfo.getUsername() + " 登录成功"
        );
        return Result.success(userInfo);
    }

    @PostMapping("/refresh")
    public Result<UserInfoVO> refresh(@RequestBody @Valid RefreshTokenDTO dto) {
        String refreshToken = dto.getRefreshToken();

        if (!jwtUtil.validateTokenSignature(refreshToken)) {
            return Result.error(400, "刷新令牌已过期或无效");
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            return Result.error(400, "无效的刷新令牌");
        }

        if (tokenBlacklistService.isRefreshTokenBlacklisted(refreshToken)) {
            return Result.error(400, "刷新令牌已被吊销");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);

        User user = userService.getById(userId);
        if (user == null || user.getStatus() == 0) {
            return Result.error(400, "用户不存在或已被禁用");
        }

        // 从数据库读取最新角色，刷新令牌中不存储 role
        String normalizedRole = normalizeRole(user.getRole());
        List<String> permissions = roleService.getPermissionCodesByRoleCode(normalizedRole);

        Long expirationTime = claims.getExpiration().getTime();
        tokenBlacklistService.invalidateRefreshToken(refreshToken, expirationTime);

        int version = tokenBlacklistService.getNextUserVersion(userId);
        String newToken = jwtUtil.generateToken(userId, username, normalizedRole, version, permissions);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmployeeId(user.getEmployeeId());
        vo.setRole(normalizedRole);
        vo.setPermissions(permissions);
        vo.setToken(newToken);
        vo.setRefreshToken(newRefreshToken);
        return Result.success(vo);
    }

    private String normalizeRole(String role) {
        if (role == null) return "EMPLOYEE";
        if ("admin".equalsIgnoreCase(role) || "ADMIN".equals(role)) return "SUPER_ADMIN";
        if ("user".equalsIgnoreCase(role) || "USER".equals(role)) return "EMPLOYEE";
        return role;
    }

    @PostMapping("/logout")
    public Result<Map<String, Object>> logout(HttpServletRequest request,
                                              @RequestBody(required = false) Map<String, String> body) {
        Long userId = AuthContext.getUserId();
        String username = AuthContext.getUsername();
        String ip = AuthContext.getIp();
        String accessToken = extractToken(request);
        String refreshToken = body == null ? null : body.get("refreshToken");

        if (accessToken != null) {
            Long accessExpTime = jwtUtil.getExpirationDate(accessToken) != null ? jwtUtil.getExpirationDate(accessToken).getTime() : System.currentTimeMillis() + jwtUtil.getExpiration();
            tokenBlacklistService.invalidateAccessToken(accessToken, accessExpTime);

            Integer currentVersion = jwtUtil.getTokenVersion(accessToken);
            if (currentVersion != null) {
                tokenBlacklistService.invalidateUserTokens(userId, currentVersion);
            }
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            Long refreshExpTime = jwtUtil.getExpirationDate(refreshToken) != null ? jwtUtil.getExpirationDate(refreshToken).getTime() : System.currentTimeMillis() + jwtUtil.getRefreshExpiration();
            tokenBlacklistService.invalidateRefreshToken(refreshToken, refreshExpTime);
        }

        operationLogService.logWithOperator(
                username,
                ip,
                "认证管理",
                "退出登录",
                "用户 " + username + " 退出登录"
        );

        Map<String, Object> data = new HashMap<>();
        data.put("message", "退出成功");
        return Result.success(data);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
