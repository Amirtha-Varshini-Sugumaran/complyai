import { useEffect, useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Alert, Button, Card, CardContent, MenuItem, Stack, TextField, Typography } from "@mui/material";
import { resourceApi } from "../../api/services";
import { ResourceTable } from "../../components/ResourceTable";
import type { ConsentRecord } from "../../types/domain";

export function ConsentPage() {
  const queryClient = useQueryClient();
  const [subjectIdentifier, setSubjectIdentifier] = useState("lead-999");
  const [consentType, setConsentType] = useState("MARKETING_EMAIL");
  const [source, setSource] = useState("website_form");
  const [proofReference, setProofReference] = useState("");
  const [expiryDate, setExpiryDate] = useState("");
  const [status, setStatus] = useState("ACTIVE");
  const [selectedConsentId, setSelectedConsentId] = useState("");

  const consentQuery = useQuery({
    queryKey: ["consents"],
    queryFn: () => resourceApi.list<ConsentRecord>("/consents")
  });
  const summaryQuery = useQuery({
    queryKey: ["consent-summary"],
    queryFn: () => resourceApi.get<Record<string, number>>("/consents/summary")
  });

  const createMutation = useMutation({
    mutationFn: async () =>
      resourceApi.create<ConsentRecord>("/consents", {
        subjectIdentifier,
        consentType,
        dateGranted: new Date().toISOString().slice(0, 10),
        source,
        expiryDate: expiryDate || null,
        proofReference: proofReference || null,
        status
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["consents"] });
      queryClient.invalidateQueries({ queryKey: ["consent-summary"] });
    }
  });

  const selectedConsent = useMemo(
    () => (consentQuery.data?.items ?? []).find((item) => item.id === Number(selectedConsentId)),
    [consentQuery.data, selectedConsentId]
  );

  useEffect(() => {
    if (!selectedConsent) return;
    setSubjectIdentifier(selectedConsent.subjectIdentifier);
    setConsentType(selectedConsent.consentType);
    setSource(selectedConsent.source);
    setProofReference(selectedConsent.proofReference ?? "");
    setExpiryDate(selectedConsent.expiryDate ?? "");
    setStatus(selectedConsent.status);
  }, [selectedConsent]);

  const updateMutation = useMutation({
    mutationFn: async () =>
      resourceApi.update<ConsentRecord>(`/consents/${selectedConsentId}`, {
        id: Number(selectedConsentId),
        subjectIdentifier,
        consentType,
        dateGranted: selectedConsent?.dateGranted ?? new Date().toISOString().slice(0, 10),
        source,
        expiryDate: expiryDate || null,
        proofReference: proofReference || null,
        status
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["consents"] });
      queryClient.invalidateQueries({ queryKey: ["consent-summary"] });
    }
  });

  const hasMissingProof = (consentQuery.data?.items ?? []).some((item) => item.missingProof);

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Consent Records</Typography>
      <Card>
        <CardContent>
          <Stack direction={{ xs: "column", md: "row" }} spacing={2} useFlexGap flexWrap="wrap">
            <TextField label="Subject Identifier" value={subjectIdentifier} onChange={(e) => setSubjectIdentifier(e.target.value)} />
            <TextField label="Consent Type" value={consentType} onChange={(e) => setConsentType(e.target.value)} />
            <TextField label="Source" value={source} onChange={(e) => setSource(e.target.value)} />
            <TextField label="Expiry Date" type="date" InputLabelProps={{ shrink: true }} value={expiryDate} onChange={(e) => setExpiryDate(e.target.value)} />
            <TextField label="Proof Reference" value={proofReference} onChange={(e) => setProofReference(e.target.value)} />
            <TextField select label="Status" value={status} onChange={(e) => setStatus(e.target.value)}>
              <MenuItem value="ACTIVE">ACTIVE</MenuItem>
              <MenuItem value="EXPIRED">EXPIRED</MenuItem>
              <MenuItem value="PENDING_RENEWAL">PENDING_RENEWAL</MenuItem>
              <MenuItem value="REVOKED">REVOKED</MenuItem>
            </TextField>
            <TextField
              select
              label="Select Existing"
              value={selectedConsentId}
              onChange={(e) => setSelectedConsentId(e.target.value)}
              sx={{ minWidth: 220 }}
            >
              {(consentQuery.data?.items ?? []).map((item) => (
                <MenuItem key={item.id} value={item.id}>{`${item.subjectIdentifier} (${item.status})`}</MenuItem>
              ))}
            </TextField>
            <Button variant="contained" onClick={() => createMutation.mutate()}>
              Create consent
            </Button>
            <Button variant="outlined" disabled={!selectedConsentId} onClick={() => updateMutation.mutate()}>
              Update consent
            </Button>
          </Stack>
        </CardContent>
      </Card>
      {hasMissingProof ? <Alert severity="warning">Some consent records are missing proof references.</Alert> : null}
      {summaryQuery.data ? (
        <Alert severity="info">
          Missing proof: {summaryQuery.data.missingProof ?? 0} | Expired: {summaryQuery.data.expired ?? 0}
        </Alert>
      ) : null}
      {selectedConsent?.expired ? <Alert severity="error">The selected consent record is expired.</Alert> : null}
      <ResourceTable
        columns={["Subject", "Consent Type", "Source", "Expiry", "Status", "Missing Proof", "Expired"]}
        rows={(consentQuery.data?.items ?? []).map((item) => [
          item.subjectIdentifier,
          item.consentType,
          item.source,
          item.expiryDate ?? "-",
          item.status,
          item.missingProof ? "Yes" : "No",
          item.expired ? "Yes" : "No"
        ])}
      />
    </Stack>
  );
}
