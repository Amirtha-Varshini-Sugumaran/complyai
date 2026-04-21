package com.complyai.tenantmanagement.controller;

import com.complyai.security.SecurityUtils;
import com.complyai.tenantmanagement.dto.TenantDto;
import com.complyai.tenantmanagement.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Operation(summary = "List all tenants for super admin inspection")
    @GetMapping
    public ResponseEntity<List<TenantDto>> listTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDto> getTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(tenantService.getTenant(tenantId, SecurityUtils.currentUser()));
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantDto> updateTenant(@PathVariable Long tenantId, @Valid @RequestBody TenantDto dto) {
        return ResponseEntity.ok(tenantService.updateTenant(tenantId, dto, SecurityUtils.currentUser()));
    }

    @PostMapping
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody TenantDto dto) {
        return ResponseEntity.ok(tenantService.createTenant(dto));
    }
}
