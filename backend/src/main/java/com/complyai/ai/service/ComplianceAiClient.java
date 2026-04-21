package com.complyai.ai.service;

import com.complyai.ai.dto.AiAnalysisRequest;
import com.complyai.ai.dto.AiAnalysisResponse;

public interface ComplianceAiClient {

    AiAnalysisResponse analyze(AiAnalysisRequest request);
}
