package com.rental.saas.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${app.jwt.secret:rental-saas-jwt-secret-key-2024}")
    private String secret;

    /**
     * 访问令牌过期时间（毫秒）
     */
    @Value("${app.jwt.access-token-expiration:7200000}")
    private Long accessTokenExpiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    @Value("${app.jwt.refresh-token-expiration:2592000000}")
    private Long refreshTokenExpiration;

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username, Long tenantId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("tenantId", tenantId)
                .claim("type", "access")
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("解析JWT令牌失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return Long.valueOf(claims.getSubject());
        }
        return null;
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 从令牌中获取租户ID
     */
    public Long getTenantIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("tenantId", Long.class);
        }
        return null;
    }

    /**
     * 检查是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return "access".equals(claims.get("type", String.class));
        }
        return false;
    }

    /**
     * 检查是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return "refresh".equals(claims.get("type", String.class));
        }
        return false;
    }
}