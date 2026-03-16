# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CookConnect is a Spring Boot microservices backend for a recipe management platform with social features. Java 21, Spring Boot 3.5.6, Spring Cloud 2025.0.0. Currently being refactored from an academic project to production-ready (see `docs/CookConnect_Refactor_Plan.md`).

## Project Tracking

- **Jira**: https://tkforgeworks.atlassian.net/jira/software/projects/CC/list
- **Confluence**: Associated with the `tkforgeworks` Atlassian organization
- All work follows epics and Confluence pages defined in the tkforgeworks space.

## Build Commands

```bash
# Build entire project
mvn clean install

# Build a single service
mvn clean install -pl services/recipe-service

# Run a service locally
mvn spring-boot:run -f services/recipe-service/pom.xml

# Build Docker image via Jib
mvn compile jib:dockerBuild -f services/recipe-service/pom.xml

# Run Gatling performance tests
mvn gatling:test -f testing/gatling-tests/pom.xml
```

No unit tests exist yet — test directories are not created. Gatling tests are skipped in normal builds (`skipTests=true`).

## Docker Compose

```bash
# Start databases (MySQL instances per service + Keycloak DB)
cd docker/cookconnect-db && docker-compose up -d

# Start services (requires config-server healthy first)
cd docker/cookconnect && docker-compose up -d
```

All containers share `cookconnect-network` (must be created externally). Environment files are in `docker/cookconnect/test-env/` and `docker/cookconnect-db/`.

## Architecture

**Multi-module Maven project** with parent POM managing versions and plugins.

### Services (business logic)
| Service | Port | Responsibility |
|---------|------|----------------|
| recipe-service | 8080 | Recipe CRUD, ingredients, tags, search |
| social-service | 8081 | Cookbooks, saved recipes |
| user-service | 8082 | User accounts, profiles, follow/follower relationships |

### Servers (infrastructure)
| Server | Port | Role |
|--------|------|------|
| config-server | 8888 | Spring Cloud Config (centralized config) |
| eureka-server | 8761 | Service discovery (planned for removal) |
| gateway-server | 8080 | API Gateway (Spring Cloud Gateway, reactive/WebFlux) |

### Layering within each service
Each service follows: **Controller → Service → Repository → Entity**, with:
- **DTO** classes separate from entities
- **MapStruct** mappers for entity↔DTO conversion (requires Lombok + MapStruct annotation processor ordering in compiler plugin)
- **ErrorHandler** package with global `ExceptionController`
- **Common** package for shared filters, interceptors, JWT handling
- **Config** package for Spring `@Configuration` classes
- **Message** package for Kafka consumers/producers (Spring Cloud Stream)

### Cross-cutting concerns
- **Security**: Spring Security + OAuth2 Resource Server (Keycloak)
- **Observability**: Logstash-encoded structured logging (ELK stack), Micrometer metrics, Zipkin tracing
- **Resilience**: Resilience4j (circuit breakers, rate limiting) — planned for deferral during refactor
- **Inter-service communication**: OpenFeign clients
- **Events**: Kafka via Spring Cloud Stream

## Key Conventions

- **Group ID**: `com.tkforgeworks.cookconnect`
- **Docker image naming**: `tkforgeworks/cookconnect-{artifactId}:{version}`
- **Base image**: `eclipse-temurin:21-jdk-jammy` (via Jib)
- **Databases**: MySQL 8.4.7 (one per service), each on a different host port (3306-3309)
- **Annotation processors**: Lombok must be listed before MapStruct in the compiler plugin config

## CI/CD

GitHub Actions workflow (`version-validation.yml`) runs on PRs to `main`:
- Auto-increments parent POM version
- Validates semantic versioning per service
- Detects changed services

## Documentation

Key docs in `docs/`:
- `CookConnect_Refactor_Plan.md` — phased refactoring roadmap
- `cookconnect_canonical_model.md` — data model reference
- `cookconnect_data_domain_v0-0-1.md` — domain documentation
- `cookconnect_architecture_deviations_v0-0-1.md` — intentional architecture deviations and rationale

Postman collections are in `postman/`.
