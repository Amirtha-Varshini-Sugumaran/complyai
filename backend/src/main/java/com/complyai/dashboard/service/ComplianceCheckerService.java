package com.complyai.dashboard.service;

import com.complyai.audit.service.AuditLogService;
import com.complyai.consent.repository.ConsentRecordRepository;
import com.complyai.inventory.repository.DataInventoryRepository;
import com.complyai.request.repository.DataSubjectRequestRepository;
import com.complyai.tenantmanagement.repository.TenantRepository;
import com.complyai.user.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ComplianceCheckerService {

    private final TenantRepository tenantRepository;
    private final DataInventoryRepository inventoryRepository;
    private final ConsentRecordRepository consentRepository;
    private final DataSubjectRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ComplianceCheckerService(TenantRepository tenantRepository,
                                    DataInventoryRepository inventoryRepository,
                                    ConsentRecordRepository consentRepository,
                                    DataSubjectRequestRepository requestRepository,
                                    UserRepository userRepository,
                                    AuditLogService auditLogService) {
        this.tenantRepository = tenantRepository;
        this.inventoryRepository = inventoryRepository;
        this.consentRepository = consentRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Scheduled(cron = "0 0 */6 * * *")
    public void runScheduledChecks() {
        tenantRepository.findAll().forEach(tenant -> {
            long missingConsentEvidence = consentRepository.countMissingProofByTenantId(tenant.getId());
            long expiredConsent = consentRepository.countExpired(tenant.getId(), LocalDate.now());
            long overdueRequests = requestRepository.countByTenantIdAndDueDateBeforeAndStatusNot(tenant.getId(), LocalDate.now(), com.complyai.common.enums.RequestStatus.COMPLETED);
            long sensitiveWithoutJustification = inventoryRepository.searchByTenant(tenant.getId(), null, null, true, null, null, org.springframework.data.domain.PageRequest.of(0, 1000))
                    .stream().filter(record -> record.getJustification() == null || record.getJustification().isBlank()).count();
            userRepository.findAllByTenantId(tenant.getId()).stream().findFirst().ifPresent(actor ->
                    auditLogService.log(actor, "COMPLIANCE_CHECK_RUN", "TENANT", tenant.getId(),
                            "expiredConsent=%d, missingConsentEvidence=%d, overdueRequests=%d, sensitiveWithoutJustification=%d"
                                    .formatted(expiredConsent, missingConsentEvidence, overdueRequests, sensitiveWithoutJustification))
            );
        });
    }
}
