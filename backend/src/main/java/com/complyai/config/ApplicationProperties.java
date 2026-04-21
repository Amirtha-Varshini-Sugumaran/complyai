package com.complyai.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "complyai")
public record ApplicationProperties(
        SecurityProperties security,
        CorsProperties cors,
        AiProperties ai,
        ComplianceProperties compliance,
        LoggingProperties logging
) {

    public record SecurityProperties(JwtProperties jwt) {
    }

    public record JwtProperties(String secret, long accessTokenExpirationMinutes) {
    }

    public record CorsProperties(List<String> allowedOrigins) {
    }

    public record AiProperties(String mode) {
    }

    public record ComplianceProperties(CheckerProperties checker) {
    }

    public record CheckerProperties(boolean enabled) {
    }

    public record LoggingProperties(boolean maskSensitiveValues) {
    }
}
