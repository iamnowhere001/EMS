package com.ems.service.impl;

import com.ems.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLoginAttemptServiceImpl implements LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(RedisLoginAttemptServiceImpl.class);

    private static final String PREFIX_ATTEMPTS = "ems:login:attempts:";
    private static final String PREFIX_LOCKED = "ems:login:locked:";
    private static final String PREFIX_IP = "ems:login:ip:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${security.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login.lock-duration:1800}")
    private int lockDurationSeconds;

    public RedisLoginAttemptServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void loginFailed(String username, String ip) {
        try {
            String attemptsKey = PREFIX_ATTEMPTS + username.toLowerCase();
            String lockedKey = PREFIX_LOCKED + username.toLowerCase();
            String ipKey = PREFIX_IP + ip;

            Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
            attempts = attempts != null ? attempts + 1 : 1;

            redisTemplate.opsForValue().set(attemptsKey, attempts, lockDurationSeconds, TimeUnit.SECONDS);

            if (ip != null && !ip.isEmpty()) {
                Integer ipAttempts = (Integer) redisTemplate.opsForValue().get(ipKey);
                ipAttempts = ipAttempts != null ? ipAttempts + 1 : 1;
                redisTemplate.opsForValue().set(ipKey, ipAttempts, lockDurationSeconds, TimeUnit.SECONDS);
            }

            if (attempts >= maxAttempts) {
                redisTemplate.opsForValue().set(lockedKey, true, lockDurationSeconds, TimeUnit.SECONDS);
                log.warn("账户已锁定: username={}, ip={}, attempts={}", username, ip, attempts);
            }
        } catch (Exception e) {
            log.warn("Redis 登录尝试记录失败: {}", e.getMessage());
        }
    }

    @Override
    public void loginSuccess(String username) {
        try {
            String attemptsKey = PREFIX_ATTEMPTS + username.toLowerCase();
            String lockedKey = PREFIX_LOCKED + username.toLowerCase();
            redisTemplate.delete(attemptsKey);
            redisTemplate.delete(lockedKey);
        } catch (Exception e) {
            log.warn("Redis 登录成功清理失败: {}", e.getMessage());
        }
    }

    @Override
    public boolean isAccountLocked(String username) {
        try {
            String lockedKey = PREFIX_LOCKED + username.toLowerCase();
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockedKey));
        } catch (Exception e) {
            log.warn("Redis 查询账户锁定状态失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getFailedAttempts(String username) {
        try {
            String attemptsKey = PREFIX_ATTEMPTS + username.toLowerCase();
            Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
            return attempts != null ? attempts : 0;
        } catch (Exception e) {
            log.warn("Redis 查询失败次数失败: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public void resetAttempts(String username) {
        loginSuccess(username);
    }
}