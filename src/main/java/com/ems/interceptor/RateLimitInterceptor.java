package com.ems.interceptor;

import com.ems.common.AuthContext;
import com.ems.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private static final String PREFIX_RATE_LIMIT = "ems:ratelimit:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${security.rate-limit.max-requests-per-minute:60}")
    private int maxRequestsPerMinute;

    public RateLimitInterceptor(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = getRateLimitKey(request);
        try {
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            if (count == null) {
                redisTemplate.opsForValue().set(key, 1, 60, TimeUnit.SECONDS);
            } else if (count >= maxRequestsPerMinute) {
                handleRateLimit(response);
                return false;
            } else {
                redisTemplate.opsForValue().increment(key);
            }
        } catch (Exception e) {
            log.warn("Redis限流检查失败，允许请求: {}", e.getMessage());
        }
        return true;
    }

    private String getRateLimitKey(HttpServletRequest request) {
        Long userId = AuthContext.getUserId();
        String ip = AuthContext.getIp();
        if (userId != null) {
            return PREFIX_RATE_LIMIT + "user:" + userId;
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return PREFIX_RATE_LIMIT + "ip:" + ip;
    }

    private void handleRateLimit(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(429);
        Result<Void> result = Result.error(429, "请求过于频繁，请稍后再试");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}