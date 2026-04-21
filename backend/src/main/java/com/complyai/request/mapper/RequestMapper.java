package com.complyai.request.mapper;

import com.complyai.request.dto.DataSubjectRequestDto;
import com.complyai.request.dto.RequestAuditDto;
import com.complyai.request.entity.DataSubjectRequest;
import com.complyai.request.entity.DataSubjectRequestAudit;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public DataSubjectRequestDto toDto(DataSubjectRequest request) {
        return new DataSubjectRequestDto(
                request.getId(),
                request.getRequesterUser() == null ? null : request.getRequesterUser().getId(),
                request.getRequesterEmail(),
                request.getRequestType(),
                request.getSubmissionDate(),
                request.getDueDate(),
                request.getAssignedUser() == null ? null : request.getAssignedUser().getId(),
                request.getStatus(),
                request.getCompletionNotes()
        );
    }

    public RequestAuditDto toAuditDto(DataSubjectRequestAudit audit) {
        return new RequestAuditDto(
                audit.getId(),
                audit.getFromStatus(),
                audit.getToStatus(),
                audit.getChangedBy().getId(),
                audit.getChangedAt(),
                audit.getNote()
        );
    }
}
