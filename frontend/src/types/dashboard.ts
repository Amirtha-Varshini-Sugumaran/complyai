export interface DashboardSummary {
  totalInventoryRecords: number;
  recordsMissingConsentEvidence: number;
  overdueRequests: number;
  retentionViolations: number;
  latestAuditEvents: Array<{
    id: number;
    action: string;
    entityType: string;
    metadataSummary: string;
    createdAt: string;
  }>;
  aiRiskSummary: Record<string, number>;
}
