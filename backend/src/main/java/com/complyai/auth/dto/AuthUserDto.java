package com.complyai.auth.dto;

public record AuthUserDto(
        Long id,
        Long tenantId,
        String firstName,
        String lastName,
        String email,
        String status
) {
}
