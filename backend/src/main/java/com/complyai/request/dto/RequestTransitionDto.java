package com.complyai.request.dto;

import com.complyai.common.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record RequestTransitionDto(@NotNull RequestStatus status, String note) {
}
