package com.complyai.dashboard.controller;

import com.complyai.dashboard.dto.DashboardSummaryDto;
import com.complyai.dashboard.service.DashboardService;
import com.complyai.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('TENANT_ADMIN','COMPLIANCE_MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> summary() {
        return ResponseEntity.ok(dashboardService.getSummary(SecurityUtils.currentUser().getTenantId()));
    }
}
