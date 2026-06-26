package com.ems.service.impl;

import com.ems.service.TokenBlacklistService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnMissingBean(RedisTokenBlacklistServiceImpl.class)
public class MemoryTokenBlacklistServiceImpl implements TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> refreshTokenBlacklist = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> accessTokenBlacklist = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> userTokenVersion = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> revokedUserTokenVersion = new ConcurrentHashMap<>();

    @Override
    public void invalidateRefreshToken(String token, long expirationTime) {
        refreshTokenBlacklist.put(token, expirationTime);
    }

    @Override
    public void invalidateAccessToken(String token, long expirationTime) {
        accessTokenBlacklist.put(token, expirationTime);
    }

    @Override
    public boolean isRefreshTokenBlacklisted(String token) {
        return refreshTokenBlacklist.containsKey(token);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String token) {
        return accessTokenBlacklist.containsKey(token);
    }

    @Override
    public void invalidateUserTokens(Long userId, int revokedVersion) {
        revokedUserTokenVersion.put(userId, revokedVersion);
        userTokenVersion.put(userId, revokedVersion + 1);
    }

    @Override
    public Integer getRevokedUserVersion(Long userId) {
        return revokedUserTokenVersion.get(userId);
    }

    @Override
    public int getNextUserVersion(Long userId) {
        return userTokenVersion.getOrDefault(userId, 1);
    }
}