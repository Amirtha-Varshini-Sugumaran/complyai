package com.complyai.inventory.controller;

import com.complyai.common.dto.PagedResponse;
import com.complyai.common.enums.InventoryStatus;
import com.complyai.inventory.dto.InventoryRecordDto;
import com.complyai.inventory.service.InventoryService;
import com.complyai.security.SecurityUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping
    public ResponseEntity<PagedResponse<InventoryRecordDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) InventoryStatus status,
            @RequestParam(required = false) String sourceSystem,
            @RequestParam(required = false) Boolean sensitivityFlag,
            @RequestParam(required = false) String lawfulBasis,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(inventoryService.list(SecurityUtils.currentUser(), page, size, sortBy, direction,
                status, sourceSystem, sensitivityFlag, lawfulBasis, search));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryRecordDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.get(id, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PostMapping
    public ResponseEntity<InventoryRecordDto> create(@Valid @RequestBody InventoryRecordDto dto) {
        return ResponseEntity.ok(inventoryService.create(dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<InventoryRecordDto> update(@PathVariable Long id, @Valid @RequestBody InventoryRecordDto dto) {
        return ResponseEntity.ok(inventoryService.update(id, dto, SecurityUtils.currentUser()));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id, SecurityUtils.currentUser());
        return ResponseEntity.noContent().build();
    }
}
