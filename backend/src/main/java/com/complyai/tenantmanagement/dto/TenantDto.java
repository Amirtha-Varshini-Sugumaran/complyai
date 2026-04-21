package com.complyai.tenantmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantDto(
        Long id,
        @NotBlank String name,
        @NotBlank String slug,
        @NotBlank String status,
        @Email @NotBlank String contactEmail,
        @NotBlank String region
) {
}
