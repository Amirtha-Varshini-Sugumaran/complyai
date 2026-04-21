package com.complyai.ai.controller;

import com.complyai.ai.dto.AiAnalysisRequest;
import com.complyai.ai.dto.AiAnalysisResponse;
import com.complyai.ai.service.AiAnalysisService;
import com.complyai.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @PostMapping("/analyze")
    public ResponseEntity<AiAnalysisResponse> analyze(@Valid @RequestBody AiAnalysisRequest request) {
        return ResponseEntity.ok(aiAnalysisService.analyze(request, SecurityUtils.currentUser()));
    }
}
