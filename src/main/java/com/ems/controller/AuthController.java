package com.ems.controller;

import com.ems.common.AuthContext;
import com.ems.common.JwtUtil;
import com.ems.common.Result;
import com.ems.dto.LoginDTO;
import com.ems.dto.RefreshTokenDTO;
import com.ems.entity.User;
import com.ems.interceptor.LoginInterceptor;
import com.ems.service.OperationLogService;
import com.ems.service.UserService;
import com.ems.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;

    public AuthController(UserService userService, JwtUtil jwtUtil, OperationLogService operationLogService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.operationLogService = operationLogService;
    }

    @PostMapping("/login")
    public Result<UserInfoVO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        String ip = LoginInterceptor.resolveClientIp(request);
        UserInfoVO userInfo = userService.login(loginDTO.getUsername(), loginDTO.getPassword(), ip);
        // 登录成功日志（AuthContext 尚未设置，单独写入）
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

        if (!jwtUtil.validateToken(refreshToken)) {
            return Result.error(400, "刷新令牌已过期或无效");
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            return Result.error(400, "无效的刷新令牌");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);

        User user = userService.getById(userId);
        if (user == null || user.getStatus() == 0) {
            return Result.error(400, "用户不存在或已被禁用");
        }

        jwtUtil.invalidateRefreshToken(refreshToken);

        String newToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        vo.setToken(newToken);
        vo.setRefreshToken(newRefreshToken);
        return Result.success(vo);
    }

    /**
     * 退出登录：使当前 accessToken 立即失效并刷新该用户的 token 版本，
     * 同时拉黑 refreshToken。该接口需要登录态，因此会经过 LoginInterceptor。
     */
    @PostMapping("/logout")
    public Result<Map<String, Object>> logout(HttpServletRequest request,
                                              @RequestBody(required = false) Map<String, String> body) {
        Long userId = AuthContext.getUserId();
        String username = AuthContext.getUsername();
        String ip = AuthContext.getIp();
        String accessToken = extractToken(request);
        String refreshToken = body == null ? null : body.get("refreshToken");

        // 1. 把当前 accessToken 加入黑名单，并把该用户的 token 版本+1
        if (accessToken != null) {
            jwtUtil.revokeAccessTokenAndUser(accessToken, userId);
        }
        // 2. 拉黑 refreshToken
        if (refreshToken != null && !refreshToken.isEmpty()) {
            jwtUtil.invalidateRefreshToken(refreshToken);
        }

        // 3. 写退出日志
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
