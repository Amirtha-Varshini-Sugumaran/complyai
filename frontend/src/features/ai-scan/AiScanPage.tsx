import { useMutation } from "@tanstack/react-query";
import { Alert, Button, Paper, Stack, TextField, Typography } from "@mui/material";
import { useState } from "react";
import api from "../../api/client";

export function AiScanPage() {
  const [content, setContent] = useState("Sensitive customer support data. Missing lawful basis. No proof of consent.");
  const mutation = useMutation({
    mutationFn: async () => {
      const { data } = await api.post("/ai/analyze", {
        targetType: "PASTED_TEXT",
        inputSource: "manual_entry",
        content
      });
      return data;
    }
  });

  async function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) return;
    const text = await file.text();
    setContent(text);
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4">AI Compliance Scan</Typography>
      <Paper variant="outlined" sx={{ p: 2 }}>
        <Stack spacing={2}>
          <TextField
            label="Paste text or CSV excerpt"
            multiline
            minRows={8}
            value={content}
            onChange={(event) => setContent(event.target.value)}
          />
          <Button variant="outlined" component="label">
            Upload txt/csv
            <input hidden type="file" accept=".txt,.csv,text/plain,text/csv" onChange={handleFileChange} />
          </Button>
          <Button variant="contained" onClick={() => mutation.mutate()}>
            Run analysis
          </Button>
        </Stack>
      </Paper>
      {mutation.data ? (
        <Paper variant="outlined" sx={{ p: 2 }}>
          <Stack spacing={1}>
            <Alert severity={mutation.data.riskLevel === "HIGH" ? "error" : mutation.data.riskLevel === "MEDIUM" ? "warning" : "info"}>
              {mutation.data.riskLevel}: {mutation.data.summary}
            </Alert>
            <Typography variant="subtitle1">Flagged Issues</Typography>
            {(mutation.data.flaggedIssues ?? []).map((issue: string) => (
              <Typography key={issue} variant="body2">- {issue}</Typography>
            ))}
            <Typography variant="subtitle1" sx={{ pt: 1 }}>Recommended Actions</Typography>
            {(mutation.data.recommendedNextActions ?? []).map((action: string) => (
              <Typography key={action} variant="body2">- {action}</Typography>
            ))}
          </Stack>
        </Paper>
      ) : null}
    </Stack>
  );
}
