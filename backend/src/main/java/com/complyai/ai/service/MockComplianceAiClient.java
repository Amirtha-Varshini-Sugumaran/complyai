package com.complyai.ai.service;

import com.complyai.ai.dto.AiAnalysisRequest;
import com.complyai.ai.dto.AiAnalysisResponse;
import com.complyai.common.enums.RiskLevel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MockComplianceAiClient implements ComplianceAiClient {

    @Override
    public AiAnalysisResponse analyze(AiAnalysisRequest request) {
        String content = request.content().toLowerCase();
        List<String> issues = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        RiskLevel level = RiskLevel.LOW;

        boolean missingJustification = content.contains("without justification")
                || content.contains("no justification")
                || !content.contains("justification");
        if (content.contains("sensitive") && missingJustification) {
            issues.add("Sensitive data without justification");
            actions.add("Document the necessity and legal basis for sensitive processing.");
            level = RiskLevel.HIGH;
        }
        if (content.contains("consent") && !content.contains("proof")) {
            issues.add("Consent evidence appears incomplete");
            actions.add("Add a traceable proof reference for the consent event.");
            level = level == RiskLevel.HIGH ? level : RiskLevel.MEDIUM;
        }
        if (content.contains("lawful basis") || content.contains("lawful_basis")) {
            if (content.contains("lawful basis:") && content.contains("lawful basis: ")) {
                // keep current level
            }
        } else {
            issues.add("Lawful basis is missing or unclear");
            actions.add("Record the lawful basis and map it to the processing purpose.");
            level = RiskLevel.HIGH;
        }
        if (content.contains("overdue") || content.contains("past due")) {
            issues.add("Request timing indicates possible SLA breach");
            actions.add("Escalate the request and capture remediation steps.");
            level = RiskLevel.HIGH;
        }
        if (issues.isEmpty()) {
            issues.add("No critical issues detected from the submitted content");
            actions.add("Keep evidence and review the record during the next control cycle.");
        }

        return new AiAnalysisResponse(
                level,
                "Mock compliance analysis completed for %s with %d flagged issue(s).".formatted(request.targetType(), issues.size()),
                actions,
                issues
        );
    }
}
