package com.complyai.auth.dto;

import java.util.Set;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        AuthUserDto user,
        Set<String> roles,
        TenantSummaryDto tenant
) {
}
