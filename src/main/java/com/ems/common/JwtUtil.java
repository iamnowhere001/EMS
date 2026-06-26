package com.ems.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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

    /** 已注销的 refreshToken（token -> 过期时间戳），命中即拒绝。 */
    private final ConcurrentHashMap<String, Long> refreshTokenBlacklist = new ConcurrentHashMap<>();

    /** 已注销的 accessToken（用于退出登录后立即失效）。 */
    private final ConcurrentHashMap<String, Long> accessTokenBlacklist = new ConcurrentHashMap<>();

    /** 用户级 token 版本：每次登出后递增，旧 accessToken 一律失效（可作为强制下线方案）。 */
    private final ConcurrentHashMap<Long, Integer> userTokenVersion = new ConcurrentHashMap<>();

    /** 已被强制下线过的旧 token 版本号：userId -> 失效版本号（>= 该版本号的 token 视为失效）。 */
    private final ConcurrentHashMap<Long, Integer> revokedUserTokenVersion = new ConcurrentHashMap<>();

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

    public String generateToken(Long userId, String username, String role) {
        int version = currentUserVersion(userId);
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

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            Claims claims = parseToken(token);
            if (claims.getExpiration().before(new Date())) return false;

            String type = stringClaim(claims, CLAIM_TYPE);
            if (TYPE_REFRESH.equals(type)) {
                return !refreshTokenBlacklist.containsKey(token);
            }
            if (TYPE_ACCESS.equals(type)) {
                if (accessTokenBlacklist.containsKey(token)) return false;
                Long userId = Long.valueOf(claims.getSubject());
                Integer revoked = revokedUserTokenVersion.get(userId);
                if (revoked != null) {
                    Integer tokenVer = claims.get("ver", Integer.class);
                    if (tokenVer == null || tokenVer <= revoked) {
                        return false;
                    }
                }
                return true;
            }
            // 兼容老 token：默认视为 access
            return !accessTokenBlacklist.containsKey(token);
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

    /** 兼容老 token：默认为 access 类型。 */
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

    public void invalidateRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            refreshTokenBlacklist.put(token, claims.getExpiration().getTime());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("无法使刷新令牌无效: {}", e.getMessage());
        }
    }

    /**
     * 退出登录时调用：使当前 accessToken 立即失效，并把用户 token 版本递增，
     * 达到强制下线该用户所有未过期 accessToken 的效果。
     *
     * <p>新生成的 token 版本号 = currentVer + 1（即 userTokenVersion 递增后的值），
     * 已被拉黑的版本号 = currentVer（旧的、刚被废止的版本）。
     * 这样新登录的 token 一定 &gt; revokedVersion，可通过校验。</p>
     */
    public void revokeAccessTokenAndUser(String accessToken, Long userId) {
        if (accessToken != null) {
            try {
                Claims claims = parseToken(accessToken);
                accessTokenBlacklist.put(accessToken, claims.getExpiration().getTime());
            } catch (JwtException | IllegalArgumentException ignored) {
            }
        }
        if (userId != null) {
            int currentVer = currentUserVersion(userId);
            // 已下线的最大版本号 = currentVer，下次发新 token 时会从 currentVer+1 开始
            revokedUserTokenVersion.put(userId, currentVer);
            // 下一次发 token 时使用的版本号
            userTokenVersion.put(userId, currentVer + 1);
        }
    }

    private int currentUserVersion(Long userId) {
        return userTokenVersion.getOrDefault(userId, 1);
    }

    public Long getUserIdFromToken(String token) {
        try {
            return Long.valueOf(parseToken(token).getSubject());
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

    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        int removed = 0;
        for (Map.Entry<String, Long> e : refreshTokenBlacklist.entrySet()) {
            if (e.getValue() < now) {
                refreshTokenBlacklist.remove(e.getKey());
                removed++;
            }
        }
        int removed2 = 0;
        for (Map.Entry<String, Long> e : accessTokenBlacklist.entrySet()) {
            if (e.getValue() < now) {
                accessTokenBlacklist.remove(e.getKey());
                removed2++;
            }
        }
        if (removed + removed2 > 0) {
            log.info("清理了 {} 个过期 refreshToken、{} 个过期 accessToken", removed, removed2);
        }
    }
}
