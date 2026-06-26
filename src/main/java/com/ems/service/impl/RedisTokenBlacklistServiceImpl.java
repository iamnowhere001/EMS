package com.ems.service.impl;

import com.ems.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(RedisTokenBlacklistServiceImpl.class);

    private static final String PREFIX_REFRESH_BLACKLIST = "ems:token:refresh:blacklist:";
    private static final String PREFIX_ACCESS_BLACKLIST = "ems:token:access:blacklist:";
    private static final String PREFIX_USER_VERSION = "ems:token:user:version:";
    private static final String PREFIX_USER_REVOKED_VERSION = "ems:token:user:revoked:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    public RedisTokenBlacklistServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void invalidateRefreshToken(String token, long expirationTime) {
        String key = PREFIX_REFRESH_BLACKLIST + hashToken(token);
        long ttl = Math.max(0, expirationTime - System.currentTimeMillis());
        try {
            redisTemplate.opsForValue().set(key, true, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 设置 refreshToken 黑名单失败: {}", e.getMessage());
        }
    }

    @Override
    public void invalidateAccessToken(String token, long expirationTime) {
        String key = PREFIX_ACCESS_BLACKLIST + hashToken(token);
        long ttl = Math.max(0, expirationTime - System.currentTimeMillis());
        try {
            redisTemplate.opsForValue().set(key, true, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 设置 accessToken 黑名单失败: {}", e.getMessage());
        }
    }

    @Override
    public boolean isRefreshTokenBlacklisted(String token) {
        String key = PREFIX_REFRESH_BLACKLIST + hashToken(token);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 查询 refreshToken 黑名单失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isAccessTokenBlacklisted(String token) {
        String key = PREFIX_ACCESS_BLACKLIST + hashToken(token);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 查询 accessToken 黑名单失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void invalidateUserTokens(Long userId, int revokedVersion) {
        try {
            String revokedKey = PREFIX_USER_REVOKED_VERSION + userId;
            redisTemplate.opsForValue().set(revokedKey, revokedVersion, refreshTokenExpiration, TimeUnit.MILLISECONDS);

            String versionKey = PREFIX_USER_VERSION + userId;
            redisTemplate.opsForValue().set(versionKey, revokedVersion + 1, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 设置用户版本失败 userId={}: {}", userId, e.getMessage());
        }
    }

    @Override
    public Integer getRevokedUserVersion(Long userId) {
        String key = PREFIX_USER_REVOKED_VERSION + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Integer.parseInt(value.toString()) : null;
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 获取用户废止版本失败 userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public int getNextUserVersion(Long userId) {
        String key = PREFIX_USER_VERSION + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Integer.parseInt(value.toString()) : 1;
        } catch (Exception e) {
            log.warn("[TokenBlacklist] Redis 获取用户版本失败 userId={}: {}", userId, e.getMessage());
            return 1;
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(token.hashCode());
        }
    }
}