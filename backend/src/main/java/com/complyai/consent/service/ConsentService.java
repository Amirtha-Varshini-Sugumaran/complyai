package com.complyai.consent.service;

import com.complyai.common.dto.PagedResponse;
import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.common.util.PageMapper;
import com.complyai.audit.service.AuditLogService;
import com.complyai.consent.dto.ConsentRecordDto;
import com.complyai.consent.entity.ConsentRecord;
import com.complyai.consent.mapper.ConsentMapper;
import com.complyai.consent.repository.ConsentRecordRepository;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenantmanagement.service.TenantService;
import com.complyai.user.entity.User;
import com.complyai.user.service.UserService;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsentService {

    private final ConsentRecordRepository repository;
    private final ConsentMapper mapper;
    private final TenantService tenantService;
    private final PageMapper pageMapper;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public ConsentService(ConsentRecordRepository repository,
                          ConsentMapper mapper,
                          TenantService tenantService,
                          PageMapper pageMapper,
                          AuditLogService auditLogService,
                          UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenantService = tenantService;
        this.pageMapper = pageMapper;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    public PagedResponse<ConsentRecordDto> list(AppUserPrincipal principal, int page, int size) {
        return pageMapper.toResponse(repository.findAllByTenantId(principal.getTenantId(), PageRequest.of(page, size)), mapper::toDto);
    }

    public ConsentRecordDto get(Long id, AppUserPrincipal principal) {
        return mapper.toDto(getEntity(id, principal));
    }

    @Transactional
    public ConsentRecordDto create(ConsentRecordDto dto, AppUserPrincipal principal) {
        ConsentRecord entity = new ConsentRecord();
        entity.setTenant(tenantService.getEntity(principal.getTenantId()));
        mapper.updateEntity(entity, dto);
        ConsentRecord saved = repository.save(entity);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "CONSENT_CREATED", "CONSENT_RECORD", saved.getId(), "Consent record created");
        return mapper.toDto(saved);
    }

    @Transactional
    public ConsentRecordDto update(Long id, ConsentRecordDto dto, AppUserPrincipal principal) {
        ConsentRecord entity = getEntity(id, principal);
        mapper.updateEntity(entity, dto);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "CONSENT_UPDATED", "CONSENT_RECORD", entity.getId(), "Consent record updated");
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id, AppUserPrincipal principal) {
        ConsentRecord entity = getEntity(id, principal);
        repository.delete(entity);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "CONSENT_DELETED", "CONSENT_RECORD", entity.getId(), "Consent record deleted");
    }

    public Map<String, Long> getSummaryCounts(Long tenantId) {
        return Map.of(
                "missingProof", repository.countMissingProofByTenantId(tenantId),
                "expired", repository.countExpired(tenantId, LocalDate.now())
        );
    }

    private ConsentRecord getEntity(Long id, AppUserPrincipal principal) {
        return repository.findByIdAndTenantId(id, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Consent record not found"));
    }
}
