# ER Diagram

Core relationships:

- `Tenant 1..* Users`
- `User *..* Role`
- `Tenant 1..* DataInventoryRecord`
- `Tenant 1..* ConsentRecord`
- `Tenant 1..* DataSubjectRequest`
- `DataSubjectRequest 1..* DataSubjectRequestAudit`
- `Tenant 1..* AuditLog`
- `Tenant 1..* AiAnalysisResult`
