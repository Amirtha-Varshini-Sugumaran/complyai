export interface InventoryRecord {
  id: number;
  title: string;
  dataCategory: string;
  dataSubjectType: string;
  processingPurpose: string;
  lawfulBasis: string;
  storageLocation: string;
  retentionPeriodDays: number;
  sensitivityFlag: boolean;
  sourceSystem: string;
  status: string;
  justification?: string | null;
}

export interface ConsentRecord {
  id: number;
  subjectIdentifier: string;
  consentType: string;
  dateGranted: string;
  source: string;
  expiryDate?: string | null;
  proofReference?: string | null;
  status: string;
  expired: boolean;
  missingProof: boolean;
}

export interface DataSubjectRequest {
  id: number;
  requesterUserId?: number | null;
  requesterEmail: string;
  requestType: string;
  submissionDate: string;
  dueDate: string;
  assignedUserId?: number | null;
  status: string;
  completionNotes?: string | null;
}

export interface AuditLog {
  id: number;
  tenantId?: number | null;
  actorUserId?: number | null;
  action: string;
  entityType: string;
  entityId?: number | null;
  metadataSummary: string;
  createdAt: string;
}

export interface UserRecord {
  id: number;
  tenantId?: number | null;
  firstName: string;
  lastName: string;
  email: string;
  status: string;
  roles: string[];
}
