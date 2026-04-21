package com.complyai.inventory.mapper;

import com.complyai.inventory.dto.InventoryRecordDto;
import com.complyai.inventory.entity.DataInventoryRecord;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryRecordDto toDto(DataInventoryRecord record) {
        return new InventoryRecordDto(
                record.getId(),
                record.getTitle(),
                record.getDataCategory(),
                record.getDataSubjectType(),
                record.getProcessingPurpose(),
                record.getLawfulBasis(),
                record.getStorageLocation(),
                record.getRetentionPeriodDays(),
                record.getSensitivityFlag(),
                record.getSourceSystem(),
                record.getStatus(),
                record.getJustification()
        );
    }

    public void updateEntity(DataInventoryRecord entity, InventoryRecordDto dto) {
        entity.setTitle(dto.title());
        entity.setDataCategory(dto.dataCategory());
        entity.setDataSubjectType(dto.dataSubjectType());
        entity.setProcessingPurpose(dto.processingPurpose());
        entity.setLawfulBasis(dto.lawfulBasis());
        entity.setStorageLocation(dto.storageLocation());
        entity.setRetentionPeriodDays(dto.retentionPeriodDays());
        entity.setSensitivityFlag(dto.sensitivityFlag());
        entity.setSourceSystem(dto.sourceSystem());
        entity.setStatus(dto.status());
        entity.setJustification(dto.justification());
    }
}
