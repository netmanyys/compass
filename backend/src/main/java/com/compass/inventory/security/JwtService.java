package com.compass.inventory.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final Key key;
    private final long accessSeconds;
    private final long refreshSeconds;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.accessMinutes}") long accessMinutes,
        @Value("${app.jwt.refreshDays}") long refreshDays
    ) {
        byte[] keyBytes = secret.length() < 32 ? (secret + "0".repeat(32)).substring(0, 32).getBytes() : secret.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessSeconds = accessMinutes * 60;
        this.refreshSeconds = refreshDays * 24 * 60 * 60;
    }

    public String generateAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(accessSeconds)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(refreshSeconds)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public JwtUser parseToken(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles", List.class);
        return new JwtUser(username, roles);
    }

    public long getAccessSeconds() {
        return accessSeconds;
    }

    public record JwtUser(String username, List<String> roles) {}
}
