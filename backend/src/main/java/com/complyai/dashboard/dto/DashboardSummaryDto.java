package com.complyai.dashboard.dto;

import com.complyai.audit.dto.AuditLogDto;
import java.util.List;
import java.util.Map;

public record DashboardSummaryDto(
        long totalInventoryRecords,
        long recordsMissingConsentEvidence,
        long overdueRequests,
        long retentionViolations,
        List<AuditLogDto> latestAuditEvents,
        Map<String, Long> aiRiskSummary
) {
}
