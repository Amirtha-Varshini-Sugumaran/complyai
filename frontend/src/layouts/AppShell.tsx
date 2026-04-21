import DashboardOutlinedIcon from "@mui/icons-material/DashboardOutlined";
import FactCheckOutlinedIcon from "@mui/icons-material/FactCheckOutlined";
import Inventory2OutlinedIcon from "@mui/icons-material/Inventory2Outlined";
import ManageAccountsOutlinedIcon from "@mui/icons-material/ManageAccountsOutlined";
import PolicyOutlinedIcon from "@mui/icons-material/PolicyOutlined";
import PsychologyOutlinedIcon from "@mui/icons-material/PsychologyOutlined";
import ReceiptLongOutlinedIcon from "@mui/icons-material/ReceiptLongOutlined";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import {
  AppBar,
  Box,
  Button,
  Divider,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Stack,
  Toolbar,
  Typography
} from "@mui/material";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/AuthContext";

const navItems = [
  { label: "Dashboard", icon: <DashboardOutlinedIcon />, path: "/dashboard" },
  { label: "Data Inventory", icon: <Inventory2OutlinedIcon />, path: "/inventory" },
  { label: "Consent Records", icon: <PolicyOutlinedIcon />, path: "/consents" },
  { label: "Requests", icon: <FactCheckOutlinedIcon />, path: "/requests" },
  { label: "Audit Logs", icon: <ReceiptLongOutlinedIcon />, path: "/audit-logs" },
  { label: "AI Scan", icon: <PsychologyOutlinedIcon />, path: "/ai-scan" },
  { label: "Users", icon: <ManageAccountsOutlinedIcon />, path: "/users" },
  { label: "Tenant Settings", icon: <SettingsOutlinedIcon />, path: "/tenant-settings" }
];

export function AppShell() {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const { session, signOut, hasRole } = useAuth();
  const isSuperAdmin = hasRole(["ROLE_SUPER_ADMIN"]);
  const visibleItems = navItems.filter((item) => {
    if (isSuperAdmin) {
      return item.path === "/tenant-settings";
    }
    if (item.path === "/users") return hasRole(["ROLE_TENANT_ADMIN"]);
    if (item.path === "/tenant-settings") return hasRole(["ROLE_TENANT_ADMIN", "ROLE_SUPER_ADMIN"]);
    if (item.path === "/requests") return hasRole(["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER", "ROLE_USER"]);
    return hasRole(["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]);
  });

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <Drawer variant="permanent" sx={{ width: 260, [`& .MuiDrawer-paper`]: { width: 260, boxSizing: "border-box" } }}>
        <Toolbar>
          <Stack>
            <Typography variant="h6">ComplyAI</Typography>
            <Typography variant="body2" color="text.secondary">
              {session?.tenant?.name ?? "Platform"}
            </Typography>
          </Stack>
        </Toolbar>
        <Divider />
        <List>
          {visibleItems.map((item) => (
            <ListItemButton
              key={item.path}
              selected={pathname === item.path}
              onClick={() => navigate(item.path)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1 }}>
        <AppBar position="static" color="transparent" elevation={0}>
          <Toolbar sx={{ justifyContent: "space-between" }}>
            <Typography variant="h6">Compliance Workspace</Typography>
            <Stack direction="row" spacing={2} alignItems="center">
              <Typography variant="body2" color="text.secondary">
                {session?.user.firstName} {session?.user.lastName}
              </Typography>
              <Button onClick={signOut}>Sign out</Button>
            </Stack>
          </Toolbar>
        </AppBar>
        <Box sx={{ p: 3 }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
