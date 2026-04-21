package com.complyai.audit.repository;

import com.complyai.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId, Pageable pageable);
}
