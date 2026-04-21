import { useQuery } from "@tanstack/react-query";
import { Card, CardContent, Stack, Typography } from "@mui/material";
import api from "../../api/client";
import { useAuth } from "../auth/AuthContext";

export function TenantSettingsPage() {
  const { session } = useAuth();
  const tenantId = session?.tenant?.id;
  const { data } = useQuery({
    queryKey: ["tenant-settings", tenantId],
    queryFn: async () => {
      if (!tenantId) return null;
      const response = await api.get(`/tenants/${tenantId}`);
      return response.data;
    },
    enabled: !!tenantId
  });

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Tenant Settings</Typography>
      <Card>
        <CardContent>
          <Stack spacing={1}>
            {!tenantId ? (
              <>
                <Typography variant="body2" color="text.secondary">Scope</Typography>
                <Typography>Super admin inspection view. Provisioning and tenant-level edits belong in the backend tenant APIs for this MVP.</Typography>
              </>
            ) : null}
            <Typography variant="body2" color="text.secondary">Tenant Name</Typography>
            <Typography variant="h6">{data?.name ?? session?.tenant?.name}</Typography>
            <Typography variant="body2" color="text.secondary">Region</Typography>
            <Typography>{data?.region ?? "EU"}</Typography>
            <Typography variant="body2" color="text.secondary">Contact Email</Typography>
            <Typography>{data?.contactEmail ?? "-"}</Typography>
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}
