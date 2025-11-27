package com.example.gym.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    private final SecretKey key;
    private final int expDays;

    public JwtUtils(
            @Value("${app.jwt-secret}") String secret,
            @Value("${app.jwt-exp-days:30}") int expDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expDays = expDays;
    }

    public String sign(Long userId, String openid) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .addClaims(Map.of("id", userId, "openid", openid))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expDays, ChronoUnit.DAYS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> parse(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return Map.of(
                "id", Long.parseLong(claims.get("id").toString()),
                "openid", claims.get("openid")
        );
    }
}