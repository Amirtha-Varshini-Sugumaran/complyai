package com.complyai.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.complyai.ai.dto.AiAnalysisRequest;
import com.complyai.ai.service.MockComplianceAiClient;
import com.complyai.common.enums.RiskLevel;
import org.junit.jupiter.api.Test;

class MockComplianceAiClientTest {

    private final MockComplianceAiClient client = new MockComplianceAiClient();

    @Test
    void shouldReturnDeterministicHighRiskForSensitiveDataWithoutJustification() {
        var response = client.analyze(new AiAnalysisRequest(
                "PASTED_TEXT",
                null,
                "manual_entry",
                "Sensitive customer data with no lawful basis and no justification"
        ));

        assertThat(response.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(response.flaggedIssues()).contains("Sensitive data without justification");
    }
}
