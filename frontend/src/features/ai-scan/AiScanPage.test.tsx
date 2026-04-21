import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { describe, expect, it, vi } from "vitest";
import { AiScanPage } from "./AiScanPage";

vi.mock("../../api/client", () => ({
  default: {
    post: vi.fn().mockResolvedValue({
      data: {
        riskLevel: "HIGH",
        summary: "Mock compliance analysis completed for PASTED_TEXT with 2 flagged issue(s)."
      }
    })
  }
}));

describe("AiScanPage", () => {
  it("runs an AI analysis and renders the returned summary", async () => {
    render(
      <QueryClientProvider client={new QueryClient()}>
        <AiScanPage />
      </QueryClientProvider>
    );

    await userEvent.click(screen.getByRole("button", { name: /run analysis/i }));
    expect(await screen.findByText(/Mock compliance analysis completed/)).toBeInTheDocument();
  });
});
