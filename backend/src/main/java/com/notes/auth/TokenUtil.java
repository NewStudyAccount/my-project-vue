package com.notes.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成和解析 token
 */
@Component
public class TokenUtil {

    // 签名密钥（学习项目硬编码）
    private static final String SECRET = "vue3-learning-notes-secret-key-must-be-long-enough";
    // 过期时间：24 小时
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 token
     */
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token，返回 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 token 中获取 userId
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 token 中获取 username
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
}
