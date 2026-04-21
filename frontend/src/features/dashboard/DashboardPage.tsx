import { useQuery } from "@tanstack/react-query";
import { Card, CardContent, Grid2, Stack, Typography } from "@mui/material";
import { dashboardApi } from "../../api/services";
import { MetricCard } from "../../components/MetricCard";
import { ResourceTable } from "../../components/ResourceTable";

export function DashboardPage() {
  const { data } = useQuery({
    queryKey: ["dashboard-summary"],
    queryFn: dashboardApi.summary
  });

  return (
    <Stack spacing={3}>
      <Typography variant="h4">Dashboard</Typography>
      <Grid2 container spacing={2}>
        <Grid2 size={{ xs: 12, md: 3 }}>
          <MetricCard label="Inventory Records" value={data?.totalInventoryRecords ?? 0} />
        </Grid2>
        <Grid2 size={{ xs: 12, md: 3 }}>
          <MetricCard label="Missing Consent Evidence" value={data?.recordsMissingConsentEvidence ?? 0} />
        </Grid2>
        <Grid2 size={{ xs: 12, md: 3 }}>
          <MetricCard label="Overdue Requests" value={data?.overdueRequests ?? 0} />
        </Grid2>
        <Grid2 size={{ xs: 12, md: 3 }}>
          <MetricCard label="Retention Violations" value={data?.retentionViolations ?? 0} />
        </Grid2>
      </Grid2>
      <ResourceTable
        columns={["Action", "Entity", "Summary", "Created"]}
        rows={(data?.latestAuditEvents ?? []).map((event) => [
          event.action,
          event.entityType,
          event.metadataSummary,
          new Date(event.createdAt).toLocaleString()
        ])}
      />
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>AI Risk Counts</Typography>
          {Object.keys(data?.aiRiskSummary ?? {}).length ? (
            <Stack direction="row" spacing={3}>
              {Object.entries(data?.aiRiskSummary ?? {}).map(([risk, count]) => (
                <Typography key={risk}>{risk}: {count}</Typography>
              ))}
            </Stack>
          ) : (
            <Typography color="text.secondary">No AI analysis results yet.</Typography>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}
