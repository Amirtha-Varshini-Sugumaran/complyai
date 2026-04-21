package com.complyai.inventory.entity;

import com.complyai.common.entity.BaseEntity;
import com.complyai.common.enums.InventoryStatus;
import com.complyai.tenantmanagement.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "data_inventory")
public class DataInventoryRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String dataCategory;

    @Column(nullable = false)
    private String dataSubjectType;

    @Column(nullable = false)
    private String processingPurpose;

    @Column(nullable = false)
    private String lawfulBasis;

    @Column(nullable = false)
    private String storageLocation;

    @Column(nullable = false)
    private Integer retentionPeriodDays;

    @Column(nullable = false)
    private Boolean sensitivityFlag;

    @Column(nullable = false)
    private String sourceSystem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    private String justification;
}
