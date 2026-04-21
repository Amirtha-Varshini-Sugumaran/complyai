import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { describe, expect, it, vi } from "vitest";
import { DashboardPage } from "../DashboardPage";

vi.mock("../../../api/services", () => ({
  dashboardApi: {
    summary: async () => ({
      totalInventoryRecords: 10,
      recordsMissingConsentEvidence: 2,
      overdueRequests: 1,
      retentionViolations: 3,
      latestAuditEvents: [],
      aiRiskSummary: { HIGH: 2 }
    })
  }
}));

describe("DashboardPage", () => {
  it("renders key dashboard labels", async () => {
    render(
      <QueryClientProvider client={new QueryClient()}>
        <DashboardPage />
      </QueryClientProvider>
    );

    expect(await screen.findByText("Dashboard")).toBeInTheDocument();
    expect(await screen.findByText("Inventory Records")).toBeInTheDocument();
  });
});
