package com.ems.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final int MIN_SECRET_LENGTH = 32;
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        validateSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private void validateSecret() {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            log.warn("JWT 密钥长度不足，建议至少 {} 字符。当前: {} 字符", MIN_SECRET_LENGTH, secret == null ? 0 : secret.length());
            log.warn("在生产环境中，请设置强密钥！可以通过环境变量 JWT_SECRET 配置");
            if (secret == null || secret.contains("change-me-in-production")) {
                log.warn("检测到默认密钥，正在生成临时随机密钥...");
                byte[] randomKey = new byte[32];
                new SecureRandom().nextBytes(randomKey);
                this.secret = Base64.getEncoder().encodeToString(randomKey);
                log.info("已生成临时密钥，请在生产环境中使用固定密钥");
            }
        }
    }

    private SecretKey key() {
        return this.key;
    }

    public String generateToken(Long userId, String username, String role, int version) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + this.expiration);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim("ver", version)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + this.refreshExpiration);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateTokenSignature(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return TYPE_REFRESH.equals(stringClaim(claims, CLAIM_TYPE));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token);
            return TYPE_ACCESS.equals(stringClaim(claims, CLAIM_TYPE));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String tokenType(String token) {
        try {
            Claims claims = parseToken(token);
            String type = stringClaim(claims, CLAIM_TYPE);
            return type == null ? TYPE_ACCESS : type;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private String stringClaim(Claims claims, String name) {
        Object v = claims.get(name);
        return v == null ? null : v.toString();
    }

    public Long getUserIdFromToken(String token) {
        try {
            return Long.valueOf(parseToken(token).getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getTokenVersion(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("ver", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public long getExpiration() {
        return expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public Date getExpirationDate(String token) {
        try {
            return parseToken(token).getExpiration();
        } catch (Exception e) {
            return null;
        }
    }
}