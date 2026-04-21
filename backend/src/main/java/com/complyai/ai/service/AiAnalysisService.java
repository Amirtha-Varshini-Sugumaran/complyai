package com.complyai.ai.service;

import com.complyai.ai.dto.AiAnalysisRequest;
import com.complyai.ai.dto.AiAnalysisResponse;
import com.complyai.ai.entity.AiAnalysisResult;
import com.complyai.ai.repository.AiAnalysisResultRepository;
import com.complyai.audit.service.AuditLogService;
import com.complyai.config.ApplicationProperties;
import com.complyai.security.AppUserPrincipal;
import com.complyai.tenantmanagement.service.TenantService;
import com.complyai.user.entity.User;
import com.complyai.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiAnalysisService {

    private final ComplianceAiClient aiClient;
    private final AiAnalysisResultRepository repository;
    private final TenantService tenantService;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;

    public AiAnalysisService(ComplianceAiClient aiClient,
                             AiAnalysisResultRepository repository,
                             TenantService tenantService,
                             UserService userService,
                             AuditLogService auditLogService,
                             ApplicationProperties properties,
                             ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.repository = repository;
        this.tenantService = tenantService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiAnalysisResponse analyze(AiAnalysisRequest request, AppUserPrincipal principal) {
        AiAnalysisResponse response = aiClient.analyze(request);
        User actor = userService.getTenantUser(principal.getUserId(), principal.getTenantId());
        AiAnalysisResult result = new AiAnalysisResult();
        result.setTenant(tenantService.getEntity(principal.getTenantId()));
        result.setTargetType(request.targetType());
        result.setTargetId(request.targetId());
        result.setInputSource(request.inputSource());
        result.setRiskLevel(response.riskLevel());
        result.setSummary(response.summary());
        result.setRecommendedActions(String.join(" | ", response.recommendedNextActions()));
        result.setFlaggedIssuesJson(toJson(response.flaggedIssues()));
        result.setModelMode(properties.ai().mode());
        result.setCreatedBy(actor);
        repository.save(result);
        auditLogService.log(actor, "AI_ANALYSIS_RUN", request.targetType(), request.targetId(),
                "AI analysis completed with risk " + response.riskLevel());
        return response;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize AI issues", exception);
        }
    }
}
