package com.complyai.consent.entity;

import com.complyai.common.entity.BaseEntity;
import com.complyai.common.enums.ConsentStatus;
import com.complyai.tenantmanagement.entity.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "consent_records")
public class ConsentRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String subjectIdentifier;

    @Column(nullable = false)
    private String consentType;

    @Column(nullable = false)
    private LocalDate dateGranted;

    @Column(nullable = false)
    private String source;

    private LocalDate expiryDate;

    private String proofReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;
}
