package com.complyai.ai.entity;

import com.complyai.common.entity.BaseEntity;
import com.complyai.common.enums.RiskLevel;
import com.complyai.tenantmanagement.entity.Tenant;
import com.complyai.user.entity.User;
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
@Table(name = "ai_analysis_results")
public class AiAnalysisResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String targetType;

    private Long targetId;

    @Column(nullable = false)
    private String inputSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false, length = 2000)
    private String summary;

    @Column(nullable = false, length = 2000)
    private String recommendedActions;

    @Column(nullable = false, length = 4000)
    private String flaggedIssuesJson;

    @Column(nullable = false)
    private String modelMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
