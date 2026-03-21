package com.stocklens.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final Key signingKey;
    private final Duration accessTokenTtl;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes
    ) {
        this.signingKey = buildKey(secret);
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public Duration accessTokenTtl() {
        return accessTokenTtl;
    }

    public String issueAccessToken(Long userId, String email) {
        var now = Instant.now();
        var expiresAt = now.plus(accessTokenTtl);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .claim("email", email)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key buildKey(String secret) {
        if (secret.startsWith("base64:")) {
            var decoded = Decoders.BASE64.decode(secret.substring("base64:".length()));
            return Keys.hmacShaKeyFor(decoded);
        }
        var raw = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(raw);
    }
}
