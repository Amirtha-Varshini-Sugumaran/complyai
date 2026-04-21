package com.complyai.security;

import com.complyai.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final ApplicationProperties properties;

    public JwtService(ApplicationProperties properties) {
        this.properties = properties;
    }

    public String generateToken(AppUserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.security().jwt().accessTokenExpirationMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(principal.getUsername())
                .claims(Map.of(
                        "userId", principal.getUserId(),
                        "tenantId", principal.getTenantId(),
                        "roles", principal.getRoles()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey())
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(properties.security().jwt().secret().getBytes(StandardCharsets.UTF_8));
    }
}
