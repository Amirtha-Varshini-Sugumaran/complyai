package com.complyai.tenantmanagement.service;

import com.complyai.common.exception.ResourceNotFoundException;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenant.service.TenantAccessEvaluator;
import com.complyai.tenantmanagement.dto.TenantDto;
import com.complyai.tenantmanagement.entity.Tenant;
import com.complyai.tenantmanagement.mapper.TenantMapper;
import com.complyai.tenantmanagement.repository.TenantRepository;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final TenantAccessEvaluator tenantAccessEvaluator;

    public TenantService(TenantRepository tenantRepository,
                         TenantMapper tenantMapper,
                         TenantAccessEvaluator tenantAccessEvaluator) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
        this.tenantAccessEvaluator = tenantAccessEvaluator;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAll().stream().map(tenantMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TenantDto getTenant(Long id, AppUserPrincipal principal) {
        tenantAccessEvaluator.assertTenantAccess(principal, id);
        return tenantMapper.toDto(getEntity(id));
    }

    @Transactional
    public TenantDto updateTenant(Long id, TenantDto dto, AppUserPrincipal principal) {
        tenantAccessEvaluator.assertTenantAccess(principal, id);
        Tenant tenant = getEntity(id);
        tenantMapper.updateEntity(tenant, dto);
        return tenantMapper.toDto(tenant);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public TenantDto createTenant(TenantDto dto) {
        Tenant tenant = new Tenant();
        tenantMapper.updateEntity(tenant, dto);
        return tenantMapper.toDto(tenantRepository.save(tenant));
    }

    public Tenant getEntity(Long id) {
        return tenantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }
}
