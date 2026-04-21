package com.complyai.consent.mapper;

import com.complyai.consent.dto.ConsentRecordDto;
import com.complyai.consent.entity.ConsentRecord;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ConsentMapper {

    public ConsentRecordDto toDto(ConsentRecord record) {
        boolean expired = record.getExpiryDate() != null && record.getExpiryDate().isBefore(LocalDate.now());
        boolean missingProof = record.getProofReference() == null || record.getProofReference().isBlank();
        return new ConsentRecordDto(
                record.getId(),
                record.getSubjectIdentifier(),
                record.getConsentType(),
                record.getDateGranted(),
                record.getSource(),
                record.getExpiryDate(),
                record.getProofReference(),
                record.getStatus(),
                expired,
                missingProof
        );
    }

    public void updateEntity(ConsentRecord entity, ConsentRecordDto dto) {
        entity.setSubjectIdentifier(dto.subjectIdentifier());
        entity.setConsentType(dto.consentType());
        entity.setDateGranted(dto.dateGranted());
        entity.setSource(dto.source());
        entity.setExpiryDate(dto.expiryDate());
        entity.setProofReference(dto.proofReference());
        entity.setStatus(dto.status());
    }
}
