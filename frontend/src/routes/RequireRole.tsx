import type { PropsWithChildren } from "react";
import { Alert } from "@mui/material";
import { useAuth } from "../features/auth/AuthContext";
import type { Role } from "../types/auth";

export function RequireRole({ roles, children }: PropsWithChildren<{ roles: Role[] }>) {
  const { hasRole } = useAuth();
  if (!hasRole(roles)) {
    return <Alert severity="warning">You do not have permission to view this workspace.</Alert>;
  }
  return <>{children}</>;
}
