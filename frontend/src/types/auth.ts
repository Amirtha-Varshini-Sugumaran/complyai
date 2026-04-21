export type Role =
  | "ROLE_SUPER_ADMIN"
  | "ROLE_TENANT_ADMIN"
  | "ROLE_COMPLIANCE_MANAGER"
  | "ROLE_USER";

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
    id: number;
    tenantId: number | null;
    firstName: string;
    lastName: string;
    email: string;
    status: string;
  };
  roles: Role[];
  tenant?: {
    id: number;
    name: string;
    slug: string;
    status: string;
  } | null;
}
