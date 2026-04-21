package com.complyai.tenant.service;

import com.complyai.common.exception.AccessDeniedDomainException;
import com.complyai.security.AppUserPrincipal;
import org.springframework.stereotype.Component;

@Component("tenantAccessEvaluator")
public class TenantAccessEvaluator {

    public boolean canAccessTenant(AppUserPrincipal principal, Long tenantId) {
        return isSuperAdmin(principal) || (principal.getTenantId() != null && principal.getTenantId().equals(tenantId));
    }

    public void assertTenantAccess(AppUserPrincipal principal, Long tenantId) {
        if (!canAccessTenant(principal, tenantId)) {
            throw new AccessDeniedDomainException("Cross-tenant access is not allowed");
        }
    }

    public boolean isSuperAdmin(AppUserPrincipal principal) {
        return principal.getRoles().contains("ROLE_SUPER_ADMIN");
    }
}
