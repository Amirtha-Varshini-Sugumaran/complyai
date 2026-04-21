package com.complyai.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record AiAnalysisRequest(
        @NotBlank String targetType,
        Long targetId,
        @NotBlank String inputSource,
        @NotBlank String content
) {
}
