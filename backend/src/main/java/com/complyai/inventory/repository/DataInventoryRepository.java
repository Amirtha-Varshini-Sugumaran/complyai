package com.complyai.inventory.repository;

import com.complyai.common.enums.InventoryStatus;
import com.complyai.inventory.entity.DataInventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DataInventoryRepository extends JpaRepository<DataInventoryRecord, Long> {

    java.util.Optional<DataInventoryRecord> findByIdAndTenantId(Long id, Long tenantId);

    org.springframework.data.domain.Page<DataInventoryRecord> findAllByTenantId(Long tenantId, org.springframework.data.domain.Pageable pageable);

    long countByTenantId(Long tenantId);

    @Query("""
            select r from DataInventoryRecord r
            where r.tenant.id = :tenantId
              and (:status is null or r.status = :status)
              and (:sourceSystem is null or lower(r.sourceSystem) = :sourceSystem)
              and (:sensitivityFlag is null or r.sensitivityFlag = :sensitivityFlag)
              and (:lawfulBasis is null or lower(r.lawfulBasis) = :lawfulBasis)
              and (:search is null or lower(r.title) like concat('%', :search, '%')
                or lower(r.processingPurpose) like concat('%', :search, '%')
                or lower(r.storageLocation) like concat('%', :search, '%')
                or lower(r.sourceSystem) like concat('%', :search, '%'))
            """)
    Page<DataInventoryRecord> searchByTenant(@Param("tenantId") Long tenantId,
                                             @Param("status") InventoryStatus status,
                                             @Param("sourceSystem") String sourceSystem,
                                             @Param("sensitivityFlag") Boolean sensitivityFlag,
                                             @Param("lawfulBasis") String lawfulBasis,
                                             @Param("search") String search,
                                             Pageable pageable);
}
