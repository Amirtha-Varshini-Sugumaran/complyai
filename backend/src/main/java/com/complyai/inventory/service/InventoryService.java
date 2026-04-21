package com.complyai.inventory.service;

import com.complyai.common.dto.PagedResponse;
import com.complyai.common.enums.InventoryStatus;
import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.common.util.PageMapper;
import com.complyai.audit.service.AuditLogService;
import com.complyai.inventory.dto.InventoryRecordDto;
import com.complyai.inventory.entity.DataInventoryRecord;
import com.complyai.inventory.mapper.InventoryMapper;
import com.complyai.inventory.repository.DataInventoryRepository;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenantmanagement.service.TenantService;
import com.complyai.user.entity.User;
import com.complyai.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final DataInventoryRepository repository;
    private final InventoryMapper mapper;
    private final TenantService tenantService;
    private final PageMapper pageMapper;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public InventoryService(DataInventoryRepository repository,
                            InventoryMapper mapper,
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

    public PagedResponse<InventoryRecordDto> list(AppUserPrincipal principal,
                                                  int page,
                                                  int size,
                                                  String sortBy,
                                                  String direction,
                                                  InventoryStatus status,
                                                  String sourceSystem,
                                                  Boolean sensitivityFlag,
                                                  String lawfulBasis,
                                                  String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        if (status == null && sourceSystem == null && sensitivityFlag == null && lawfulBasis == null && search == null) {
            return pageMapper.toResponse(repository.findAllByTenantId(principal.getTenantId(), pageable), mapper::toDto);
        }
        return pageMapper.toResponse(repository.searchByTenant(
                principal.getTenantId(),
                status,
                normalize(sourceSystem),
                sensitivityFlag,
                normalize(lawfulBasis),
                normalize(search),
                pageable
        ), mapper::toDto);
    }

    public InventoryRecordDto get(Long id, AppUserPrincipal principal) {
        return mapper.toDto(getEntity(id, principal));
    }

    @Transactional
    public InventoryRecordDto create(InventoryRecordDto dto, AppUserPrincipal principal) {
        DataInventoryRecord entity = new DataInventoryRecord();
        entity.setTenant(tenantService.getEntity(principal.getTenantId()));
        mapper.updateEntity(entity, dto);
        DataInventoryRecord saved = repository.save(entity);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "INVENTORY_CREATED", "DATA_INVENTORY", saved.getId(), "Inventory record created");
        return mapper.toDto(saved);
    }

    @Transactional
    public InventoryRecordDto update(Long id, InventoryRecordDto dto, AppUserPrincipal principal) {
        DataInventoryRecord entity = getEntity(id, principal);
        mapper.updateEntity(entity, dto);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "INVENTORY_UPDATED", "DATA_INVENTORY", entity.getId(), "Inventory record updated");
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id, AppUserPrincipal principal) {
        DataInventoryRecord entity = getEntity(id, principal);
        repository.delete(entity);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        auditLogService.log(actor, "INVENTORY_DELETED", "DATA_INVENTORY", entity.getId(), "Inventory record deleted");
    }

    public DataInventoryRecord getEntity(Long id, AppUserPrincipal principal) {
        return repository.findByIdAndTenantId(id, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found"));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.toLowerCase();
    }
}
