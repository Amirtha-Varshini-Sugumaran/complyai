package com.complyai.ai.repository;

import com.complyai.ai.entity.AiAnalysisResult;
import com.complyai.common.enums.RiskLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AiAnalysisResultRepository extends JpaRepository<AiAnalysisResult, Long> {

    @Query("select a.riskLevel as riskLevel, count(a) as total from AiAnalysisResult a where a.tenant.id = :tenantId group by a.riskLevel")
    List<RiskLevelCountView> countByRiskLevel(Long tenantId);

    interface RiskLevelCountView {
        RiskLevel getRiskLevel();

        long getTotal();
    }
}
