import { useQuery } from "@tanstack/react-query";
import { Stack, Typography } from "@mui/material";
import { resourceApi } from "../../api/services";
import { ResourceTable } from "../../components/ResourceTable";

export function AuditPage() {
  const { data } = useQuery({
    queryKey: ["audit-logs"],
    queryFn: () => resourceApi.list<any>("/audit-logs")
  });

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Audit Logs</Typography>
      <ResourceTable
        columns={["Action", "Entity", "Metadata", "Created"]}
        rows={(data?.items ?? []).map((item) => [
          item.action,
          item.entityType,
          item.metadataSummary,
          item.createdAt ? new Date(item.createdAt).toLocaleString() : "-"
        ])}
      />
    </Stack>
  );
}
