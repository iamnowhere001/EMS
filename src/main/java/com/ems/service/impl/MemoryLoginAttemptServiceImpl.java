package com.ems.service.impl;

import com.ems.service.LoginAttemptService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnMissingBean(RedisLoginAttemptServiceImpl.class)
public class MemoryLoginAttemptServiceImpl implements LoginAttemptService {

    private final ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockedUntil = new ConcurrentHashMap<>();

    @Value("${security.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login.lock-duration:1800}")
    private int lockDurationSeconds;

    @Override
    public void loginFailed(String username, String ip) {
        String key = username.toLowerCase();
        attempts.merge(key, 1, Integer::sum);

        if (attempts.get(key) >= maxAttempts) {
            lockedUntil.put(key, System.currentTimeMillis() + lockDurationSeconds * 1000L);
        }
    }

    @Override
    public void loginSuccess(String username) {
        String key = username.toLowerCase();
        attempts.remove(key);
        lockedUntil.remove(key);
    }

    @Override
    public boolean isAccountLocked(String username) {
        String key = username.toLowerCase();
        Long locked = lockedUntil.get(key);
        if (locked == null) return false;
        if (System.currentTimeMillis() > locked) {
            lockedUntil.remove(key);
            attempts.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public int getFailedAttempts(String username) {
        return attempts.getOrDefault(username.toLowerCase(), 0);
    }

    @Override
    public void resetAttempts(String username) {
        loginSuccess(username);
    }
}