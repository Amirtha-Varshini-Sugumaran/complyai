package com.complyai.audit.dto;

import java.time.Instant;

public record AuditLogDto(
        Long id,
        Long tenantId,
        Long actorUserId,
        String action,
        String entityType,
        Long entityId,
        String metadataSummary,
        Instant createdAt
) {
}
