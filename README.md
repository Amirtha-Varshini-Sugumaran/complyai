# ComplyAI

ComplyAI is a portfolio-grade multi-tenant SaaS MVP for EU privacy and compliance support. It helps SMEs manage personal-data inventory, consent records, data subject requests, audit history, retention awareness, and AI-assisted risk review in one tenant-aware workspace.

This project is intentionally built as a modular monolith. The goal is to demonstrate clean full-stack engineering, explicit tenant isolation, strong API design, and realistic SaaS tradeoffs without hiding complexity behind microservices.

## Why This Matters

Many SMEs need practical tooling for GDPR-oriented workflows, but they often lack a central system for inventory, request handling, consent evidence, and auditability. ComplyAI is designed as a technical compliance-support platform that helps operations teams work more consistently. It is not legal advice.

## Stack

- Backend: Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Actuator, springdoc OpenAPI
- Frontend: React, TypeScript, Vite, React Router, React Query, Axios, Material UI
- DevOps: Docker, Docker Compose, GitHub Actions
- Testing: JUnit 5, Mockito, Spring Security Test, Testcontainers, Vitest, React Testing Library
- AI: Mock-first compliance analysis provider with deterministic heuristics

## Architecture Summary

- Modular monolith with package-by-feature modules under [backend/src/main/java/com/complyai](C:\Users\amirt\OneDrive\Documents\Playground\complyai\backend\src\main\java\com\complyai)
- Shared-database multi-tenancy using explicit `tenant_id`
- Stateless JWT authentication and role-based access control
- DTO-first REST APIs under `/api/v1/**`
- Centralized audit logging for key actions
- Flyway-managed schema and demo seed data
- Mock AI provider abstraction for local/offline usability

## Repo Layout

- [backend](C:\Users\amirt\OneDrive\Documents\Playground\complyai\backend)
- [frontend](C:\Users\amirt\OneDrive\Documents\Playground\complyai\frontend)
- [docs](C:\Users\amirt\OneDrive\Documents\Playground\complyai\docs)
- [infra](C:\Users\amirt\OneDrive\Documents\Playground\complyai\infra)
- [scripts](C:\Users\amirt\OneDrive\Documents\Playground\complyai\scripts)

## Demo Credentials

All seeded demo users use the password `password`.

- `superadmin@complyai.dev`
- `tenantadmin@northwind.dev`
- `compliance@northwind.dev`
- `user@northwind.dev`
- `tenantadmin@blueharbor.dev`
- `compliance@blueharbor.dev`
- `user@blueharbor.dev`

Recommended demo account:

- `tenantadmin@northwind.dev / password`

## How To Run

### Docker full stack

From [complyai](C:\Users\amirt\OneDrive\Documents\Playground\complyai):

```powershell
docker compose -f .\infra\docker-compose.yml up --build
```

URLs:

- Frontend: [http://localhost:8081](http://localhost:8081)
- Backend API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Local backend

Requires Java 21 and PostgreSQL.

```powershell
cd C:\Users\amirt\OneDrive\Documents\Playground\complyai\backend
.\mvnw.cmd spring-boot:run
```

### Local frontend

Requires Node 20+.

```powershell
cd C:\Users\amirt\OneDrive\Documents\Playground\complyai\frontend
npm install
npm run dev
```

## Test Commands

Backend unit tests:

```powershell
docker run --rm `
  -e TESTCONTAINERS_RYUK_DISABLED=true `
  -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal `
  --add-host=host.docker.internal:host-gateway `
  -v //var/run/docker.sock:/var/run/docker.sock `
  -v C:\Users\amirt\OneDrive\Documents\Playground\complyai\backend:/workspace `
  -w /workspace `
  maven:3.9.9-eclipse-temurin-21 `
  sh -lc "./mvnw -Dtest='AuthServiceTest,RequestWorkflowRulesTest,MockComplianceAiClientTest' test"
```

Backend integration tests:

```powershell
docker run --rm `
  -e TESTCONTAINERS_RYUK_DISABLED=true `
  -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal `
  --add-host=host.docker.internal:host-gateway `
  -v //var/run/docker.sock:/var/run/docker.sock `
  -v C:\Users\amirt\OneDrive\Documents\Playground\complyai\backend:/workspace `
  -w /workspace `
  maven:3.9.9-eclipse-temurin-21 `
  sh -lc "./mvnw -Dtest='PostgresIntegrationTest,ApiFlowIntegrationTest' test"
```

Frontend build:

```powershell
docker run --rm `
  -v C:\Users\amirt\OneDrive\Documents\Playground\complyai\frontend:/workspace `
  -w /workspace `
  node:20-alpine `
  sh -lc "npm run build"
```

Frontend tests:

```powershell
docker run --rm `
  -v C:\Users\amirt\OneDrive\Documents\Playground\complyai\frontend:/workspace `
  -w /workspace `
  node:20-alpine `
  sh -lc "npx vitest run --maxWorkers=1 --reporter=basic"
```

## What To Demo In 3–5 Minutes

1. Log in as `tenantadmin@northwind.dev / password`.
2. Open the dashboard and call out:
   - inventory count
   - missing consent evidence
   - overdue requests
   - recent audit events
   - AI risk counts
3. Go to Data Inventory and create a new record.
4. Go to Consent Records and create a consent without proof, then update it and point out the expired/missing-proof indicators.
5. Go to Requests and create a request, assign it, then move it through `SUBMITTED -> IN_REVIEW -> APPROVED -> COMPLETED`.
6. Open Audit Logs and show the recorded events for login, inventory creation, consent update, request workflow, and AI analysis.
7. Open AI Scan, paste risky text, and explain the mock provider abstraction plus persisted result/audit entry.

## Interview Talking Points

- Why explicit `tenant_id` filtering is easier to reason about than hidden multitenancy features
- How JWT auth is wired into a stateless Spring Security setup
- Why Flyway migrations plus seeded demo data improve reproducibility
- How audit logging supports traceability across compliance workflows
- Why the AI module is abstracted behind an interface with a deterministic mock implementation
- How Docker Compose, CI, and Testcontainers support delivery discipline

## Debugging Notes

- If Docker API calls fail, start Docker Desktop first
- If Flyway reports a checksum mismatch, do not edit old applied migrations; prefer additive migrations
- If the frontend loads but API calls fail in a browser, check `CORS_ORIGINS` and `VITE_API_BASE_URL`
- If JWT calls fail, verify the backend `JWT_SECRET` matches the environment used at runtime

## Current Demo-Ready Flows

- UI login with JWT token storage and protected route access
- Data Inventory create and list
- Data Subject Request create, assign, valid transitions, invalid transition rejection, and history
- Consent create/update plus summary issue counts
- Dashboard summary with audit and AI risk counts
- AI scan in mock mode with persisted results and audit logging
- Tenant isolation on inventory, requests, and consent access

## Screenshots

- Login page screenshot: [login-page.png](C:\Users\amirt\OneDrive\Documents\Playground\complyai\login-page.png)
- Additional screenshot placeholders can be added for dashboard, requests, and AI scan

## Known Limitations

- Refresh tokens are not implemented
- File ingestion is limited to pasted text and simple `.txt` / `.csv` content
- AI output is deterministic mock analysis, not a real legal or compliance model
- Frontend test execution is reliable with single-worker Vitest settings, but it is slower in constrained containers
- Tenant billing, invitation email, and attachment storage are still roadmap items

## Future Improvements

- Refresh-token rotation
- Password reset and invitation flows
- Report exports and reminders
- S3-backed attachment storage
- Richer compliance rules engine
- Real LLM provider integration with prompt/version observability

## Compliance Disclaimer

ComplyAI is a technical compliance-support system. It is not legal advice and should not be used as a substitute for qualified legal or regulatory counsel.
