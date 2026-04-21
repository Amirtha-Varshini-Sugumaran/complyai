package com.complyai.audit.mapper;

import com.complyai.audit.dto.AuditLogDto;
import com.complyai.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogDto toDto(AuditLog entity) {
        return new AuditLogDto(
                entity.getId(),
                entity.getTenant() == null ? null : entity.getTenant().getId(),
                entity.getActorUser() == null ? null : entity.getActorUser().getId(),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getMetadataSummary(),
                entity.getCreatedAt()
        );
    }
}
