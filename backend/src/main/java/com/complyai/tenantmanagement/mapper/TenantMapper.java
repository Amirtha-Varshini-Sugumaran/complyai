package com.complyai.tenantmanagement.mapper;

import com.complyai.tenantmanagement.dto.TenantDto;
import com.complyai.tenantmanagement.entity.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantDto toDto(Tenant tenant) {
        return new TenantDto(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getStatus(),
                tenant.getContactEmail(),
                tenant.getRegion()
        );
    }

    public void updateEntity(Tenant tenant, TenantDto dto) {
        tenant.setName(dto.name());
        tenant.setSlug(dto.slug());
        tenant.setStatus(dto.status());
        tenant.setContactEmail(dto.contactEmail());
        tenant.setRegion(dto.region());
    }
}
