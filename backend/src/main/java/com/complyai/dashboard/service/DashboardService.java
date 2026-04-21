package com.complyai.dashboard.service;

import com.complyai.ai.repository.AiAnalysisResultRepository;
import com.complyai.audit.dto.AuditLogDto;
import com.complyai.audit.mapper.AuditLogMapper;
import com.complyai.audit.repository.AuditLogRepository;
import com.complyai.common.enums.RequestStatus;
import com.complyai.consent.repository.ConsentRecordRepository;
import com.complyai.dashboard.dto.DashboardSummaryDto;
import com.complyai.inventory.repository.DataInventoryRepository;
import com.complyai.request.repository.DataSubjectRequestRepository;
import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DataInventoryRepository inventoryRepository;
    private final ConsentRecordRepository consentRepository;
    private final DataSubjectRequestRepository requestRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final AiAnalysisResultRepository aiRepository;

    public DashboardService(DataInventoryRepository inventoryRepository,
                            ConsentRecordRepository consentRepository,
                            DataSubjectRequestRepository requestRepository,
                            AuditLogRepository auditLogRepository,
                            AuditLogMapper auditLogMapper,
                            AiAnalysisResultRepository aiRepository) {
        this.inventoryRepository = inventoryRepository;
        this.consentRepository = consentRepository;
        this.requestRepository = requestRepository;
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
        this.aiRepository = aiRepository;
    }

    public DashboardSummaryDto getSummary(Long tenantId) {
        long totalInventory = inventoryRepository.countByTenantId(tenantId);
        long missingConsentEvidence = consentRepository.countMissingProofByTenantId(tenantId);
        long overdueRequests = requestRepository.countByTenantIdAndDueDateBeforeAndStatusNot(tenantId, LocalDate.now(), RequestStatus.COMPLETED);
        long retentionViolations = inventoryRepository.findAllByTenantId(tenantId, PageRequest.of(0, 1000))
                .stream()
                .filter(record -> record.getCreatedAt() != null)
                .filter(record -> record.getCreatedAt().plus(record.getRetentionPeriodDays(), ChronoUnit.DAYS).isBefore(Instant.now()))
                .count();
        List<AuditLogDto> latestAudits = auditLogRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId, PageRequest.of(0, 5))
                .stream().map(auditLogMapper::toDto).toList();
        Map<String, Long> aiRiskCounts = aiRepository.countByRiskLevel(tenantId).stream()
                .collect(Collectors.toMap(row -> row.getRiskLevel().name(), AiAnalysisResultRepository.RiskLevelCountView::getTotal));
        return new DashboardSummaryDto(totalInventory, missingConsentEvidence, overdueRequests, retentionViolations, latestAudits, aiRiskCounts);
    }
}
