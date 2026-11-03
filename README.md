# permit-service

A Spring Boot authorization API that answers one question: can this role perform this action? It denies by default, so unknown roles, unknown actions, and missing fields are all rejected.

**Status:** deployed shell. The core check works and is tested; persistence, JWT auth, and tenant isolation are on the roadmap below.

## Stack
Java 21, Spring Boot 4.0.9, Maven, JUnit 5, AWS Elastic Beanstalk

## Endpoints
| Method | Path | Purpose |
|---|---|---|
| POST | `/authorize` | Body: `{"role": "...", "action": "..."}`. Returns `{"allowed": bool, "reason": "..."}` |
| GET | `/actuator/health` | Health check |

Roles (hardcoded for now): `ADMIN` (READ, WRITE, DELETE), `EDITOR` (READ, WRITE), `VIEWER` (READ).

## Demo
Deployed on AWS Elastic Beanstalk (health check):

<img width="826" height="101" alt="image" src="https://github.com/user-attachments/assets/6b15aa6a-e74c-4c40-8a1a-53b6171fbf51" />


`/authorize` run locally, where VIEWER is denied WRITE:

<img width="551" height="155" alt="image" src="https://github.com/user-attachments/assets/57cd77d9-7a58-43d6-9806-bb12bb8b41f6" />


## Run locally
```
.\mvnw spring-boot:run
```
The app listens on port 5000 (set in `application.properties` to match Elastic Beanstalk).

## Tests
```
.\mvnw test
```
[N] tests cover allow and deny per role, unknown roles, and missing fields, plus an HTTP-level test of `/authorize`.

## Design notes
- **Deny by default.** Anything not explicitly granted is rejected.
- **Null-safe.** Missing fields return a deny, not a 500.

## Roadmap
- [ ] PostgreSQL-backed roles and permissions (Flyway migrations)
- [ ] JWT authentication
- [ ] Scoped roles (project-level vs. org-level)
- [ ] Tenant isolation with tests that try to break it
- [ ] Audit log of every allow/deny decision
