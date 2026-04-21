package com.complyai.consent.dto;

import com.complyai.common.enums.ConsentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ConsentRecordDto(
        Long id,
        @NotBlank String subjectIdentifier,
        @NotBlank String consentType,
        @NotNull LocalDate dateGranted,
        @NotBlank String source,
        LocalDate expiryDate,
        String proofReference,
        @NotNull ConsentStatus status,
        boolean expired,
        boolean missingProof
) {
}
