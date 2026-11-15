# Technical Design

## Architecture

The application is a same-origin Spring Boot application organized into clear packages:

- `controller`: `CandidateController` exposes the REST API and maps HTTP input/output.
- `service`: `CandidateService` owns candidate operations, input normalization, and business validation.
- `dao`: `CandidateRepository` and `CandidateSpecifications` provide JPA persistence and composable database filtering.
- `model`: `Candidate` is the persistence model; request and response records keep the API contract separate.
- `common`: shared exceptions and the REST error handler.

A small static HTML/CSS/JavaScript client is served by Spring Boot and calls the REST API. This keeps local setup simple and avoids a separate frontend build/deployment.

## Data model

`Candidate` has a generated numeric ID, required name and unique email, optional phone/skills/location, non-negative integer experience years, status, and creation/update timestamps. Skills are stored as a comma-separated string to keep the assignment-sized model simple. For richer skill matching, use a normalized candidate-skill relation or a search index.

Email uniqueness is enforced in the database, not only in application code. Status is persisted as a string enum to avoid coupling stored values to enum ordinals.

## API and errors

The API uses conventional REST methods and status codes. The collection endpoint supports search by name/email, combined exact/partial filters, pagination, and allow-listed sorting. The maximum page size is 100. Request DTO validation checks required fields, email format, lengths, and experience bounds. Errors use RFC 9457-style Problem Details; duplicate email is a `409`, invalid input is a `400`, and missing records are a `404`.

## Testing

Spring MVC integration tests exercise HTTP requests against H2. Coverage includes CRUD, validation, combined search/filtering, invalid filter values, and duplicate-email conflicts. The test profile creates and drops its own schema, so it does not touch the developer's MySQL database.

## Security and operations

No authentication is included because it is not required for this local assignment. Before exposing this service to users, add authentication and role-based authorization, HTTPS, request rate limits, secure secret storage, audit logging for sensitive changes, and dependency/security scanning. Avoid returning internal exception details to clients.

Local schema management uses Hibernate `ddl-auto=update` for convenience; a deployed application should use reviewed, versioned migrations such as Flyway or Liquibase.

## Evolving for significantly higher scale

The current app is a single service with a relational database, appropriate for a small assignment and moderate traffic. A practical evolution would be:

1. Measure request latency, database load, and query patterns before scaling.
2. Add indexes based on measured filters (for example status, location, experience); use full-text or a dedicated search service if substring search becomes expensive.
3. Keep pagination bounded, use cursor/keyset pagination for deep result sets, and cache only stable/read-heavy data where invalidation is understood.
4. Add connection pooling, database monitoring, backups, and read replicas for read-heavy workloads.
5. Move to managed MySQL with high availability and tested migration/restore procedures.
6. Scale stateless application instances horizontally behind a load balancer; externalize sessions and configuration.
7. Split services or introduce asynchronous processing only when independent scaling or reliability needs justify the operational cost.

The API contract and service/repository separation provide a starting point for these changes without prematurely introducing distributed-system complexity.
