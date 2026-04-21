package com.complyai.request.dto;

import jakarta.validation.constraints.NotNull;

public record RequestAssignmentDto(@NotNull Long assignedUserId, String note) {
}
