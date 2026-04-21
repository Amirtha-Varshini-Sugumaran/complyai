package com.complyai.inventory.dto;

import com.complyai.common.enums.InventoryStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryRecordDto(
        Long id,
        @NotBlank String title,
        @NotBlank String dataCategory,
        @NotBlank String dataSubjectType,
        @NotBlank String processingPurpose,
        @NotBlank String lawfulBasis,
        @NotBlank String storageLocation,
        @NotNull @Min(1) Integer retentionPeriodDays,
        @NotNull Boolean sensitivityFlag,
        @NotBlank String sourceSystem,
        @NotNull InventoryStatus status,
        String justification
) {
}
