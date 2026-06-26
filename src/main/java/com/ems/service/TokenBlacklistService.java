package com.ems.service;

public interface TokenBlacklistService {

    void invalidateRefreshToken(String token, long expirationTime);

    void invalidateAccessToken(String token, long expirationTime);

    boolean isRefreshTokenBlacklisted(String token);

    boolean isAccessTokenBlacklisted(String token);

    void invalidateUserTokens(Long userId, int revokedVersion);

    Integer getRevokedUserVersion(Long userId);

    int getNextUserVersion(Long userId);
}