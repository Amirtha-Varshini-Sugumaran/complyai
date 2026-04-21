package com.complyai.request.repository;

import com.complyai.common.enums.RequestStatus;
import com.complyai.request.entity.DataSubjectRequest;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSubjectRequestRepository extends JpaRepository<DataSubjectRequest, Long> {

    Page<DataSubjectRequest> findAllByTenantId(Long tenantId, Pageable pageable);

    java.util.Optional<DataSubjectRequest> findByIdAndTenantId(Long id, Long tenantId);

    long countByTenantIdAndDueDateBeforeAndStatusNot(Long tenantId, LocalDate dueDate, RequestStatus status);

    long countByTenantIdAndStatusIn(Long tenantId, Collection<RequestStatus> statuses);
}
