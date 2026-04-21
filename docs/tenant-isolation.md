# Tenant Isolation

ComplyAI uses shared-database multitenancy with explicit `tenant_id` fields on business tables.

Controls:

- tenant-aware repository methods such as `findByIdAndTenantId`
- service-layer checks using the authenticated principal's tenant
- super admin bypass limited to tenant provisioning and inspection scenarios
- tenant-specific aggregation on dashboard and audit queries
