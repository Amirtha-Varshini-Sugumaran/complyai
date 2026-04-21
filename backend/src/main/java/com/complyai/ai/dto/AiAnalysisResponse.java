package com.complyai.ai.dto;

import com.complyai.common.enums.RiskLevel;
import java.util.List;

public record AiAnalysisResponse(
        RiskLevel riskLevel,
        String summary,
        List<String> recommendedNextActions,
        List<String> flaggedIssues
) {
}
