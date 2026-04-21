# ComplyAI - Compliance SaaS MVP

ComplyAI is a multi-tenant SaaS MVP for EU privacy and compliance support. It helps small and medium-sized companies manage personal-data inventory, consent records, data subject requests, audit history, retention awareness, and AI-assisted risk review in one tenant-aware workspace.

The application is built as a modular monolith: one deployable system with clear module boundaries. That keeps the project practical to run locally while still showing how a real SaaS product can separate concerns across authentication, tenancy, compliance workflows, audit logging, reporting, and AI analysis.

## Why This Matters

GDPR-oriented operations often depend on scattered spreadsheets, emails, and manual reminders. ComplyAI provides a structured workflow for tracking personal data, consent evidence, subject requests, and compliance risks. It is designed as technical support software, not as legal advice.

## AI-Assisted Development

This project was built with AI as a delivery accelerator. I used AI to help structure the product scope, compare architecture options, draft implementation patterns, generate test ideas, improve documentation, and iterate faster across backend, frontend, and DevOps work.

The important part is how AI was used: I treated it like a technical pair programmer, not an autopilot. I reviewed the generated suggestions, adapted them to the project goals, checked the code against the architecture, and kept the final decisions focused on maintainability, tenant safety, compliance workflows, and recruiter-readable delivery evidence.

This shows practical AI fluency: turning a broad product idea into a working SaaS-style MVP, using AI to move faster while still applying engineering judgment, testing discipline, and clear communication.

## Core Capabilities

- Tenant-aware data isolation using explicit `tenant_id` fields
- JWT authentication with role-based access control
- Personal data inventory management
- Consent record tracking with expired and missing-proof indicators
- Data subject request workflow with controlled status transitions
- Central audit logging for important actions
- Dashboard metrics for inventory, consent, requests, retention, audit activity, and AI risk counts
- Mock-first AI compliance analysis with a provider interface for future LLM integration
- Dockerized local environment with PostgreSQL, backend, and frontend

## Tech Stack

- Backend: Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Actuator, springdoc OpenAPI
- Frontend: React, TypeScript, Vite, React Router, React Query, Axios, Material UI
- DevOps: Docker, Docker Compose, GitHub Actions
- Testing: JUnit 5, Mockito, Spring Security Test, Testcontainers, Vitest, React Testing Library
- AI: Deterministic mock compliance provider behind a pluggable interface

## Architecture

- Package-by-feature modular monolith under [`backend/src/main/java/com/complyai`](backend/src/main/java/com/complyai)
- DTO-first REST API design under `/api/v1/**`
- Shared database multi-tenancy with explicit repository and service-level tenant checks
- Stateless JWT security with Spring Security method authorization
- Flyway migrations for schema creation and demo seed data
- Centralized exception handling with consistent API error responses
- Audit log service used by authentication, inventory, consent, requests, and AI analysis

## Repository Layout

- [`backend`](backend) - Spring Boot API, domain modules, migrations, and tests
- [`frontend`](frontend) - Vite React SPA
- [`docs`](docs) - architecture, security, API, deployment, and testing notes
- [`infra`](infra) - Docker Compose and Nginx configuration
- [`scripts`](scripts) - local helper scripts and seed notes

## Demo Credentials

All seeded demo users use the password `password`.

- `superadmin@complyai.dev`
- `tenantadmin@northwind.dev`
- `compliance@northwind.dev`
- `user@northwind.dev`
- `tenantadmin@blueharbor.dev`
- `compliance@blueharbor.dev`
- `user@blueharbor.dev`

Recommended account for exploring the main tenant-admin flow:

```text
tenantadmin@northwind.dev / password
```

## Run With Docker

From the repository root:

```powershell
docker compose -f .\infra\docker-compose.yml up --build
```

URLs:

- Frontend: [http://localhost:8081](http://localhost:8081)
- Backend API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Run Locally

Backend requires Java 21 and PostgreSQL.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend requires Node 20+.

```powershell
cd frontend
npm install
npm run dev
```

## Test Commands

Backend unit tests:

```powershell
cd backend
.\mvnw.cmd test
```

Frontend build and tests:

```powershell
cd frontend
npm install
npm run build
npx vitest run --maxWorkers=1 --reporter=basic
```

Docker-based backend test example:

```powershell
docker run --rm `
  -e TESTCONTAINERS_RYUK_DISABLED=true `
  -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal `
  --add-host=host.docker.internal:host-gateway `
  -v //var/run/docker.sock:/var/run/docker.sock `
  -v ${PWD}\backend:/workspace `
  -w /workspace `
  maven:3.9.9-eclipse-temurin-21 `
  sh -lc "./mvnw test"
```

## Key Workflows

- Login with a seeded user and receive a JWT
- Create and list data inventory records
- Create, assign, and transition data subject requests through the approved workflow
- Create and update consent records while tracking expired or missing evidence
- Review dashboard metrics and recent audit events
- Run a mock AI compliance scan and persist the result
- Verify tenant isolation through tenant-scoped API access

## Design Notes

- Explicit tenant filtering is used instead of hidden ORM multitenancy so access boundaries stay visible in repositories and services.
- Flyway migrations keep schema changes repeatable and make local setup predictable.
- The AI module is intentionally mock-first, so the project works without external API keys while keeping a clean provider contract for future integrations.
- Audit logging is append-only for traceability across compliance-sensitive workflows.
- Docker Compose is the recommended local path because it runs PostgreSQL, backend, and frontend with consistent configuration.

## Debugging Notes

- If Docker API calls fail, start Docker Desktop first.
- If Flyway reports a checksum mismatch, do not edit old applied migrations; add a new migration instead.
- If the frontend loads but API calls fail, check `CORS_ORIGINS` and `VITE_API_BASE_URL`.
- If JWT calls fail, verify that the backend `JWT_SECRET` matches the runtime environment.

## Known Limitations

- Refresh tokens are not implemented.
- File ingestion is limited to pasted text and simple `.txt` / `.csv` content.
- AI output is deterministic mock analysis, not legal or regulatory advice.
- Tenant billing, invitation email, and attachment storage are roadmap items.

## Future Improvements

- Refresh-token rotation
- Password reset and invitation flows
- Report exports and reminders
- S3-backed attachment storage
- Richer compliance rules engine
- Real LLM provider integration with prompt and version observability

## Compliance Disclaimer

ComplyAI is a technical compliance-support system. It is not legal advice and should not be used as a substitute for qualified legal or regulatory counsel.
