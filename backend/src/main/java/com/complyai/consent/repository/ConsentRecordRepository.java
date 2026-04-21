package com.complyai.consent.repository;

import com.complyai.consent.entity.ConsentRecord;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {

    Page<ConsentRecord> findAllByTenantId(Long tenantId, Pageable pageable);

    java.util.Optional<ConsentRecord> findByIdAndTenantId(Long id, Long tenantId);

    @Query("""
            select count(c)
            from ConsentRecord c
            where c.tenant.id = :tenantId
              and (c.proofReference is null or trim(c.proofReference) = '')
            """)
    long countMissingProofByTenantId(Long tenantId);

    @Query("select count(c) from ConsentRecord c where c.tenant.id = :tenantId and c.expiryDate is not null and c.expiryDate < :today")
    long countExpired(Long tenantId, LocalDate today);
}
