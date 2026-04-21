package com.complyai.request.service;

import com.complyai.audit.service.AuditLogService;
import com.complyai.common.dto.PagedResponse;
import com.complyai.common.enums.RequestStatus;
import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.common.util.PageMapper;
import com.complyai.request.dto.DataSubjectRequestDto;
import com.complyai.request.dto.RequestAuditDto;
import com.complyai.request.dto.RequestAssignmentDto;
import com.complyai.request.dto.RequestTransitionDto;
import com.complyai.request.entity.DataSubjectRequest;
import com.complyai.request.entity.DataSubjectRequestAudit;
import com.complyai.request.mapper.RequestMapper;
import com.complyai.request.repository.DataSubjectRequestAuditRepository;
import com.complyai.request.repository.DataSubjectRequestRepository;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenantmanagement.service.TenantService;
import com.complyai.user.entity.User;
import com.complyai.user.service.UserService;
import java.time.Instant;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataSubjectRequestService {

    private final DataSubjectRequestRepository repository;
    private final DataSubjectRequestAuditRepository auditRepository;
    private final RequestMapper mapper;
    private final PageMapper pageMapper;
    private final TenantService tenantService;
    private final UserService userService;
    private final RequestWorkflowRules workflowRules;
    private final AuditLogService auditLogService;

    public DataSubjectRequestService(DataSubjectRequestRepository repository,
                                     DataSubjectRequestAuditRepository auditRepository,
                                     RequestMapper mapper,
                                     PageMapper pageMapper,
                                     TenantService tenantService,
                                     UserService userService,
                                     RequestWorkflowRules workflowRules,
                                     AuditLogService auditLogService) {
        this.repository = repository;
        this.auditRepository = auditRepository;
        this.mapper = mapper;
        this.pageMapper = pageMapper;
        this.tenantService = tenantService;
        this.userService = userService;
        this.workflowRules = workflowRules;
        this.auditLogService = auditLogService;
    }

    public PagedResponse<DataSubjectRequestDto> list(AppUserPrincipal principal, int page, int size) {
        return pageMapper.toResponse(repository.findAllByTenantId(principal.getTenantId(), PageRequest.of(page, size)), mapper::toDto);
    }

    public DataSubjectRequestDto get(Long id, AppUserPrincipal principal) {
        return mapper.toDto(getEntity(id, principal));
    }

    @Transactional
    public DataSubjectRequestDto create(DataSubjectRequestDto dto, AppUserPrincipal principal) {
        DataSubjectRequest entity = new DataSubjectRequest();
        entity.setTenant(tenantService.getEntity(principal.getTenantId()));
        entity.setRequesterUser(dto.requesterUserId() == null ? null : userService.getTenantUser(dto.requesterUserId(), principal.getTenantId()));
        entity.setRequesterEmail(dto.requesterEmail());
        entity.setRequestType(dto.requestType());
        entity.setSubmissionDate(dto.submissionDate());
        entity.setDueDate(dto.dueDate());
        entity.setAssignedUser(dto.assignedUserId() == null ? null : userService.getTenantUser(dto.assignedUserId(), principal.getTenantId()));
        entity.setStatus(dto.status() == null ? RequestStatus.SUBMITTED : dto.status());
        entity.setCompletionNotes(dto.completionNotes());
        DataSubjectRequest saved = repository.save(entity);
        auditLogService.log(userService.getTenantUser(principal.getUserId(), principal.getTenantId()),
                "REQUEST_CREATED", "DATA_SUBJECT_REQUEST", saved.getId(), "Data subject request created");
        return mapper.toDto(saved);
    }

    @Transactional
    public DataSubjectRequestDto assign(Long id, RequestAssignmentDto dto, AppUserPrincipal principal) {
        DataSubjectRequest request = getEntity(id, principal);
        request.setAssignedUser(userService.getTenantUser(dto.assignedUserId(), principal.getTenantId()));
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "REQUEST_ASSIGNED", "DATA_SUBJECT_REQUEST", request.getId(),
                "Request assigned to user " + dto.assignedUserId() + (dto.note() == null ? "" : " - " + dto.note()));
        return mapper.toDto(request);
    }

    @Transactional
    public DataSubjectRequestDto transition(Long id, RequestTransitionDto dto, AppUserPrincipal principal) {
        DataSubjectRequest request = getEntity(id, principal);
        workflowRules.validateTransition(request.getStatus(), dto.status());
        RequestStatus previous = request.getStatus();
        request.setStatus(dto.status());
        if (dto.note() != null) {
            request.setCompletionNotes(dto.note());
        }
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        DataSubjectRequestAudit audit = new DataSubjectRequestAudit();
        audit.setRequest(request);
        audit.setFromStatus(previous);
        audit.setToStatus(dto.status());
        audit.setChangedBy(actor);
        audit.setChangedAt(Instant.now());
        audit.setNote(dto.note());
        auditRepository.save(audit);
        auditLogService.log(actor, "REQUEST_STATUS_CHANGED", "DATA_SUBJECT_REQUEST", request.getId(),
                "Request moved from %s to %s".formatted(previous, dto.status()));
        return mapper.toDto(request);
    }

    public java.util.List<RequestAuditDto> history(Long id, AppUserPrincipal principal) {
        getEntity(id, principal);
        return auditRepository.findAllByRequestIdOrderByChangedAtDesc(id).stream().map(mapper::toAuditDto).toList();
    }

    public DataSubjectRequest getEntity(Long id, AppUserPrincipal principal) {
        return repository.findByIdAndTenantId(id, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Data subject request not found"));
    }
}
