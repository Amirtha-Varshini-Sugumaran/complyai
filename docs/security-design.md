# Security Design

- BCrypt password hashing
- Stateless JWT auth
- Method-level role checks for critical endpoints
- Tenant-aware repository methods to prevent accidental cross-tenant reads
- Global error handling and limited sensitive logging
- Actuator health endpoint exposed for readiness checks
