package com.complyai.audit.service;

import com.complyai.audit.entity.AuditLog;
import com.complyai.audit.repository.AuditLogRepository;
import com.complyai.tenantmanagement.entity.Tenant;
import com.complyai.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog log(User actor, String action, String entityType, Long entityId, String metadataSummary) {
        AuditLog log = new AuditLog();
        Tenant tenant = actor == null ? null : actor.getTenant();
        log.setTenant(tenant);
        log.setActorUser(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMetadataSummary(metadataSummary);
        return auditLogRepository.save(log);
    }
}
