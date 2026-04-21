package com.complyai.consent.controller;

import com.complyai.common.dto.PagedResponse;
import com.complyai.consent.dto.ConsentRecordDto;
import com.complyai.consent.service.ConsentService;
import com.complyai.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consents")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping
    public ResponseEntity<PagedResponse<ConsentRecordDto>> list(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(consentService.list(SecurityUtils.currentUser(), page, size));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> summary() {
        return ResponseEntity.ok(consentService.getSummaryCounts(SecurityUtils.currentUser().getTenantId()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ConsentRecordDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(consentService.get(id, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PostMapping
    public ResponseEntity<ConsentRecordDto> create(@Valid @RequestBody ConsentRecordDto dto) {
        return ResponseEntity.ok(consentService.create(dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ConsentRecordDto> update(@PathVariable Long id, @Valid @RequestBody ConsentRecordDto dto) {
        return ResponseEntity.ok(consentService.update(id, dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consentService.delete(id, SecurityUtils.currentUser());
        return ResponseEntity.noContent().build();
    }
}
