# Architecture Decisions

- Modular monolith over microservices for lower operational complexity
- Explicit `tenant_id` isolation in schema, repositories, and services
- JWT stateless auth for SPA compatibility
- DTO-based API surface with centralized error handling
- Mock-first AI abstraction for predictable local behavior
