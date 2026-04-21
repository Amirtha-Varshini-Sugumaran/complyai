package com.complyai.audit.controller;

import com.complyai.audit.dto.AuditLogDto;
import com.complyai.audit.mapper.AuditLogMapper;
import com.complyai.audit.repository.AuditLogRepository;
import com.complyai.common.dto.PagedResponse;
import com.complyai.common.util.PageMapper;
import com.complyai.security.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogRepository repository;
    private final AuditLogMapper mapper;
    private final PageMapper pageMapper;

    public AuditLogController(AuditLogRepository repository, AuditLogMapper mapper, PageMapper pageMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageMapper = pageMapper;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping
    public ResponseEntity<PagedResponse<AuditLogDto>> list(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(pageMapper.toResponse(
                repository.findAllByTenantIdOrderByCreatedAtDesc(SecurityUtils.currentUser().getTenantId(), PageRequest.of(page, size)),
                mapper::toDto
        ));
    }
}
