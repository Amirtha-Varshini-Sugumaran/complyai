import { useState } from "react";
import { Alert, Box, Button, Card, CardContent, Stack, TextField, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export function LoginPage() {
  const [email, setEmail] = useState("tenantadmin@northwind.dev");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { signIn } = useAuth();

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    try {
      setSubmitting(true);
      setError(null);
      await signIn(email, password);
      navigate("/dashboard");
    } catch {
      setError("Unable to sign in with those credentials.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Box sx={{ minHeight: "100vh", display: "grid", placeItems: "center", px: 2 }}>
      <Card sx={{ width: "100%", maxWidth: 420 }}>
        <CardContent>
          <Stack spacing={2} component="form" onSubmit={handleSubmit}>
            <Typography variant="h4">ComplyAI</Typography>
            <Typography color="text.secondary">
              EU privacy operations for multi-tenant teams.
            </Typography>
            {error ? <Alert severity="error">{error}</Alert> : null}
            <TextField label="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <TextField
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? "Signing in..." : "Sign in"}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
