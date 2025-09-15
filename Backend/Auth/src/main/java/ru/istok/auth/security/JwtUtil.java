package ru.istok.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final String jwtSecret = "your-very-strong-secret-key-that-should-be-at-least-256-bits-long";
    private final long jwtExpirationMs = 3600000; // 1 час

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Важное изменение — в subject кладём username, а не userId!
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)  // <-- сюда username
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            logger.info("JWT token валиден");
            return true;
        } catch (JwtException ex) {
            logger.warn("JWT token невалиден: {}", ex.getMessage());
        }
        return false;
    }

    // Переименовано из getUserIdFromToken -> getUsernameFromToken
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
}
