package com.example.board.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails user) {
        var now = new Date();
        var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3));
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails user, String sessionId) {
        var now = new Date();
        var exp = new Date(now.getTime() + ( 1000 * 60 * 60 * 24 * 7));

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("sid", sessionId)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    public String getSessionId(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().get("sid", String.class);
    }

    private String getSubject(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            logger.error("JwtException", e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
