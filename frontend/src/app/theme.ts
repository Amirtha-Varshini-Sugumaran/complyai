import { createTheme } from "@mui/material";

export const appTheme = createTheme({
  palette: {
    mode: "light",
    primary: { main: "#1d4ed8" },
    secondary: { main: "#0f766e" },
    background: {
      default: "#f3f6fb",
      paper: "#ffffff"
    }
  },
  shape: {
    borderRadius: 8
  },
  typography: {
    fontFamily: "'Inter', system-ui, sans-serif"
  }
});
