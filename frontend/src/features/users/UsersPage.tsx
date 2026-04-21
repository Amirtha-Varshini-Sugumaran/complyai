import { useQuery } from "@tanstack/react-query";
import { Stack, Typography } from "@mui/material";
import { ResourceTable } from "../../components/ResourceTable";
import api from "../../api/client";

export function UsersPage() {
  const { data } = useQuery({
    queryKey: ["users"],
    queryFn: () => apiListUsers()
  });

  return (
    <Stack spacing={2}>
      <Typography variant="h4">User Management</Typography>
      <ResourceTable
        columns={["Name", "Email", "Status", "Roles"]}
        rows={(data ?? []).map((item: any) => [
          `${item.firstName} ${item.lastName}`,
          item.email,
          item.status,
          item.roles.join(", ")
        ])}
      />
    </Stack>
  );
}

async function apiListUsers() {
  const { data } = await api.get("/users");
  return data;
}
