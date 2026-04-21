import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Alert,
  Button,
  Card,
  CardContent,
  MenuItem,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { resourceApi } from "../../api/services";
import { ResourceTable } from "../../components/ResourceTable";
import { useAuth } from "../auth/AuthContext";
import type { DataSubjectRequest, UserRecord } from "../../types/domain";

const transitionMap: Record<string, string[]> = {
  SUBMITTED: ["IN_REVIEW"],
  IN_REVIEW: ["APPROVED", "REJECTED"],
  APPROVED: ["COMPLETED"]
};

export function RequestsPage() {
  const { hasRole } = useAuth();
  const canManageWorkflow = hasRole(["ROLE_TENANT_ADMIN", "ROLE_COMPLIANCE_MANAGER"]);
  const queryClient = useQueryClient();
  const [requesterEmail, setRequesterEmail] = useState("external.subject@example.eu");
  const [requestType, setRequestType] = useState("ACCESS");
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [assignedUserId, setAssignedUserId] = useState("");
  const [requestNote, setRequestNote] = useState("");

  const requestsQuery = useQuery({
    queryKey: ["requests"],
    queryFn: () => resourceApi.list<DataSubjectRequest>("/requests")
  });
  const usersQuery = useQuery({
    queryKey: ["users"],
    queryFn: () => resourceApi.get<UserRecord[]>("/users"),
    enabled: canManageWorkflow
  });
  const historyQuery = useQuery({
    queryKey: ["request-history", selectedId],
    queryFn: () => resourceApi.get<Array<{ id: number; fromStatus: string | null; toStatus: string; changedAt: string; note?: string | null }>>(`/requests/${selectedId}/history`),
    enabled: canManageWorkflow && !!selectedId
  });

  const selectedRequest = useMemo(
    () => requestsQuery.data?.items.find((item) => item.id === selectedId) ?? null,
    [requestsQuery.data, selectedId]
  );

  const createMutation = useMutation({
    mutationFn: async () =>
      resourceApi.create<DataSubjectRequest>("/requests", {
        requesterEmail,
        requestType,
        submissionDate: new Date().toISOString().slice(0, 10),
        dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
        status: "SUBMITTED",
        completionNotes: null
      }),
    onSuccess: (created) => {
      setSelectedId(created.id);
      setAssignedUserId("");
      queryClient.invalidateQueries({ queryKey: ["requests"] });
    }
  });

  const assignMutation = useMutation({
    mutationFn: async () =>
      resourceApi.post<DataSubjectRequest>(`/requests/${selectedId}/assign`, {
        assignedUserId: Number(assignedUserId),
        note: requestNote || null
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["requests"] });
      queryClient.invalidateQueries({ queryKey: ["request-history", selectedId] });
    }
  });

  const transitionMutation = useMutation({
    mutationFn: async (status: string) =>
      resourceApi.post<DataSubjectRequest>(`/requests/${selectedId}/transition`, {
        status,
        note: requestNote || null
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["requests"] });
      queryClient.invalidateQueries({ queryKey: ["request-history", selectedId] });
    }
  });

  return (
    <Stack spacing={2}>
      <Typography variant="h4">Data Subject Requests</Typography>
      <Card>
        <CardContent>
          <Stack direction={{ xs: "column", md: "row" }} spacing={2}>
            <TextField label="Requester Email" value={requesterEmail} onChange={(e) => setRequesterEmail(e.target.value)} />
            <TextField select label="Request Type" value={requestType} onChange={(e) => setRequestType(e.target.value)}>
              {["ACCESS", "DELETION", "CORRECTION", "RESTRICTION"].map((option) => (
                <MenuItem key={option} value={option}>{option}</MenuItem>
              ))}
            </TextField>
            <Button variant="contained" onClick={() => createMutation.mutate()}>
              Create request
            </Button>
          </Stack>
        </CardContent>
      </Card>

      <ResourceTable
        columns={["ID", "Requester", "Type", "Submission", "Due Date", "Assigned", "Status"]}
        rows={(requestsQuery.data?.items ?? []).map((item) => [
          item.id,
          item.requesterEmail,
          item.requestType,
          item.submissionDate,
          item.dueDate,
          item.assignedUserId ?? "-",
          item.status
        ])}
      />

      <Card>
        <CardContent>
          <Stack spacing={2}>
            <TextField
              select
              label="Selected Request"
              value={selectedId ?? ""}
              onChange={(e) => setSelectedId(Number(e.target.value))}
            >
              {(requestsQuery.data?.items ?? []).map((item) => (
                <MenuItem key={item.id} value={item.id}>{`${item.id} - ${item.requesterEmail} (${item.status})`}</MenuItem>
              ))}
            </TextField>

            {canManageWorkflow ? (
              <>
                <TextField
                  select
                  label="Assign To"
                  value={assignedUserId}
                  onChange={(e) => setAssignedUserId(e.target.value)}
                >
                  {(usersQuery.data ?? []).map((user) => (
                    <MenuItem key={user.id} value={user.id}>{`${user.firstName} ${user.lastName}`}</MenuItem>
                  ))}
                </TextField>

                <TextField label="Notes" multiline minRows={3} value={requestNote} onChange={(e) => setRequestNote(e.target.value)} />

                <Stack direction="row" spacing={2}>
                  <Button variant="outlined" disabled={!selectedId || !assignedUserId} onClick={() => assignMutation.mutate()}>
                    Assign request
                  </Button>
                  {(selectedRequest ? transitionMap[selectedRequest.status] ?? [] : []).map((nextStatus) => (
                    <Button key={nextStatus} variant="contained" onClick={() => transitionMutation.mutate(nextStatus)}>
                      Move to {nextStatus}
                    </Button>
                  ))}
                </Stack>
              </>
            ) : (
              <Alert severity="info">Standard users can submit and review their own request details, while workflow actions stay with compliance staff.</Alert>
            )}
          </Stack>
        </CardContent>
      </Card>

      {selectedRequest ? <Alert severity="info">Selected request status: {selectedRequest.status}</Alert> : null}
      {canManageWorkflow && historyQuery.data?.length ? (
        <ResourceTable
          columns={["From", "To", "Changed", "Note"]}
          rows={historyQuery.data.map((entry) => [
            entry.fromStatus ?? "-",
            entry.toStatus,
            new Date(entry.changedAt).toLocaleString(),
            entry.note ?? "-"
          ])}
        />
      ) : null}
    </Stack>
  );
}
