import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Card, CardContent, MenuItem, Stack, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { resourceApi } from "../../api/services";
import { ResourceTable } from "../../components/ResourceTable";
import type { InventoryRecord } from "../../types/domain";

export function InventoryPage() {
  const queryClient = useQueryClient();
  const [title, setTitle] = useState("EU Support Archive");
  const [dataCategory, setDataCategory] = useState("Support Data");
  const [dataSubjectType, setDataSubjectType] = useState("Customer");
  const [processingPurpose, setProcessingPurpose] = useState("Customer support follow-up");
  const [lawfulBasis, setLawfulBasis] = useState("LEGITIMATE_INTEREST");
  const [storageLocation, setStorageLocation] = useState("Azure EU / blob archive");
  const [retentionPeriodDays, setRetentionPeriodDays] = useState("365");
  const [sensitivityFlag, setSensitivityFlag] = useState("false");
  const [sourceSystem, setSourceSystem] = useState("Zendesk");
  const [status, setStatus] = useState("ACTIVE");
  const [justification, setJustification] = useState("");

  const { data } = useQuery({
    queryKey: ["inventory"],
    queryFn: () => resourceApi.list<InventoryRecord>("/inventory")
  });

  const createMutation = useMutation({
    mutationFn: async () =>
      resourceApi.create<InventoryRecord>("/inventory", {
        title,
        dataCategory,
        dataSubjectType,
        processingPurpose,
        lawfulBasis,
        storageLocation,
        retentionPeriodDays: Number(retentionPeriodDays),
        sensitivityFlag: sensitivityFlag === "true",
        sourceSystem,
        status,
        justification: justification || null
      }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["inventory"] })
  });

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Data Inventory</Typography>
      <Card>
        <CardContent>
          <Stack direction={{ xs: "column", md: "row" }} spacing={2} useFlexGap flexWrap="wrap">
            <TextField label="Title" value={title} onChange={(event) => setTitle(event.target.value)} />
            <TextField label="Category" value={dataCategory} onChange={(event) => setDataCategory(event.target.value)} />
            <TextField label="Data Subject Type" value={dataSubjectType} onChange={(event) => setDataSubjectType(event.target.value)} />
            <TextField label="Purpose" value={processingPurpose} onChange={(event) => setProcessingPurpose(event.target.value)} />
            <TextField label="Lawful Basis" value={lawfulBasis} onChange={(event) => setLawfulBasis(event.target.value)} />
            <TextField label="Storage Location" value={storageLocation} onChange={(event) => setStorageLocation(event.target.value)} />
            <TextField label="Retention Days" type="number" value={retentionPeriodDays} onChange={(event) => setRetentionPeriodDays(event.target.value)} />
            <TextField select label="Sensitive" value={sensitivityFlag} onChange={(event) => setSensitivityFlag(event.target.value)}>
              <MenuItem value="false">No</MenuItem>
              <MenuItem value="true">Yes</MenuItem>
            </TextField>
            <TextField label="Source System" value={sourceSystem} onChange={(event) => setSourceSystem(event.target.value)} />
            <TextField select label="Status" value={status} onChange={(event) => setStatus(event.target.value)}>
              <MenuItem value="ACTIVE">ACTIVE</MenuItem>
              <MenuItem value="UNDER_REVIEW">UNDER_REVIEW</MenuItem>
              <MenuItem value="ARCHIVED">ARCHIVED</MenuItem>
            </TextField>
            <TextField label="Justification" value={justification} onChange={(event) => setJustification(event.target.value)} />
            <Button variant="contained" onClick={() => createMutation.mutate()}>
              Add record
            </Button>
          </Stack>
        </CardContent>
      </Card>
      <ResourceTable
        columns={["Title", "Category", "Purpose", "Lawful Basis", "Status"]}
        rows={(data?.items ?? []).map((item) => [
          item.title,
          item.dataCategory,
          item.processingPurpose,
          item.lawfulBasis,
          item.status
        ])}
      />
    </Stack>
  );
}
