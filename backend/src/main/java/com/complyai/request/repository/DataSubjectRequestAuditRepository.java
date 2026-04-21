package com.complyai.request.repository;

import com.complyai.request.entity.DataSubjectRequestAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSubjectRequestAuditRepository extends JpaRepository<DataSubjectRequestAudit, Long> {

    List<DataSubjectRequestAudit> findAllByRequestIdOrderByChangedAtDesc(Long requestId);
}
