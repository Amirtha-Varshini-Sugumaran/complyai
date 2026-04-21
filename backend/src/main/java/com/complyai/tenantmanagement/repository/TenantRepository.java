package com.complyai.tenantmanagement.repository;

import com.complyai.tenantmanagement.entity.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySlug(String slug);
}
