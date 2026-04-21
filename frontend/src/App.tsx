import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "./layouts/AppShell";
import { LoginPage } from "./features/auth/LoginPage";
import { RequireAuth } from "./routes/RequireAuth";
import { DashboardPage } from "./features/dashboard/DashboardPage";
import { InventoryPage } from "./features/inventory/InventoryPage";
import { ConsentPage } from "./features/consent/ConsentPage";
import { RequestsPage } from "./features/requests/RequestsPage";
import { AuditPage } from "./features/audit/AuditPage";
import { AiScanPage } from "./features/ai-scan/AiScanPage";
import { UsersPage } from "./features/users/UsersPage";
import { TenantSettingsPage } from "./features/tenant-settings/TenantSettingsPage";
import { RequireRole } from "./routes/RequireRole";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <AppShell />
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]}><DashboardPage /></RequireRole>} />
        <Route path="inventory" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]}><InventoryPage /></RequireRole>} />
        <Route path="consents" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]}><ConsentPage /></RequireRole>} />
        <Route path="requests" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER", "ROLE_USER"]}><RequestsPage /></RequireRole>} />
        <Route path="audit-logs" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]}><AuditPage /></RequireRole>} />
        <Route path="ai-scan" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]}><AiScanPage /></RequireRole>} />
        <Route path="users" element={<RequireRole roles={["ROLE_TENANT_ADMIN"]}><UsersPage /></RequireRole>} />
        <Route path="tenant-settings" element={<RequireRole roles={["ROLE_TENANT_ADMIN", "ROLE_SUPER_ADMIN"]}><TenantSettingsPage /></RequireRole>} />
      </Route>
    </Routes>
  );
}
