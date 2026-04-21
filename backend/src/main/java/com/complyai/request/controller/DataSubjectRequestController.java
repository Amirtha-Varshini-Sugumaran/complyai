package com.complyai.request.controller;

import com.complyai.common.dto.PagedResponse;
import com.complyai.request.dto.DataSubjectRequestDto;
import com.complyai.request.dto.RequestAuditDto;
import com.complyai.request.dto.RequestAssignmentDto;
import com.complyai.request.dto.RequestTransitionDto;
import com.complyai.request.service.DataSubjectRequestService;
import com.complyai.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requests")
public class DataSubjectRequestController {

    private final DataSubjectRequestService requestService;

    public DataSubjectRequestController(DataSubjectRequestService requestService) {
        this.requestService = requestService;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER','USER')")
    @GetMapping
    public ResponseEntity<PagedResponse<DataSubjectRequestDto>> list(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(requestService.list(SecurityUtils.currentUser(), page, size));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<DataSubjectRequestDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.get(id, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER','USER')")
    @PostMapping
    public ResponseEntity<DataSubjectRequestDto> create(@Valid @RequestBody DataSubjectRequestDto dto) {
        return ResponseEntity.ok(requestService.create(dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PostMapping("/{id}/assign")
    public ResponseEntity<DataSubjectRequestDto> assign(@PathVariable Long id,
                                                        @Valid @RequestBody RequestAssignmentDto dto) {
        return ResponseEntity.ok(requestService.assign(id, dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PostMapping("/{id}/transition")
    public ResponseEntity<DataSubjectRequestDto> transition(@PathVariable Long id,
                                                            @Valid @RequestBody RequestTransitionDto dto) {
        return ResponseEntity.ok(requestService.transition(id, dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<RequestAuditDto>> history(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.history(id, SecurityUtils.currentUser()));
    }
}
