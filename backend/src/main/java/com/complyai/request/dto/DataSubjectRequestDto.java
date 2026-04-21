package com.complyai.request.dto;

import com.complyai.common.enums.RequestStatus;
import com.complyai.common.enums.RequestType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DataSubjectRequestDto(
        Long id,
        Long requesterUserId,
        @Email String requesterEmail,
        @NotNull RequestType requestType,
        @NotNull LocalDate submissionDate,
        @NotNull LocalDate dueDate,
        Long assignedUserId,
        @NotNull RequestStatus status,
        String completionNotes
) {
}
