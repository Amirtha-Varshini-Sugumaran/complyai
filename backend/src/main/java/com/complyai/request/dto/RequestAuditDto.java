package com.complyai.request.dto;

import com.complyai.common.enums.RequestStatus;
import java.time.Instant;

public record RequestAuditDto(
        Long id,
        RequestStatus fromStatus,
        RequestStatus toStatus,
        Long changedBy,
        Instant changedAt,
        String note
) {
}
