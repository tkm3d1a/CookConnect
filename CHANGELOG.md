# Changelog

All notable changes to the CookConnect project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-11-18

### Added

#### Microservices Architecture
- **User Service** (v0.1.0) - Complete user management microservice
  - User registration and profile management
  - Password management (add/update passwords)
  - Extended profiles with demographics and addresses
  - Privacy controls for user data
  - Full CRUD operations via REST API
  - Health monitoring with Spring Boot Actuator

- **Recipe Service** (v0.1.0) - Recipe management microservice
  - Recipe creation (simple and detailed modes)
  - Recipe retrieval (individual and list all)
  - Ingredient management with quantities and measurements
  - Step-by-step cooking instructions
  - Recipe categorization with tags
  - Visibility controls (private, public, friends-only)
  - Full CRUD operations via REST API

- **Social Service** (v0.1.0) - Social features microservice
  - User follow/unfollow functionality
  - Recipe bookmarking system
  - Cookbook collections with full CRUD
  - Cookbook entries and notes management
  - Social profile management
  - Full REST API implementation

#### Infrastructure Services
- **Config Server** (v0.1.0) - Centralized configuration management
  - Spring Cloud Config Server implementation
  - Git-backed configuration repository support
  - Environment-specific configurations (dev, prod)
  - Encryption support for sensitive configuration values
  - Configuration serving for all microservices

- **Eureka Server** (v0.1.0) - Service discovery
  - Netflix Eureka service registry implementation
  - Dynamic service registration and discovery
  - Health monitoring and heartbeat tracking
  - Diagnostic logging for service registration events
  - Integration with all microservices

- **Gateway Server** (v0.1.0) - API Gateway
  - Spring Cloud Gateway implementation
  - Reactive WebFlux-based routing
  - Eureka integration for dynamic service routing
  - Centralized entry point for all microservices
  - Actuator endpoints for gateway health monitoring

#### Development Infrastructure
- **Maven Multi-Module Project Structure**
  - Parent POM with unified dependency management
  - Automatic version management for parent POM
  - Standardized build configuration across all modules
  - Spring Boot 3.5.6 and Spring Cloud 2025.0.0 integration

- **Docker Support**
  - Jib Maven Plugin integration for containerization
  - Automated Docker image building during package phase
  - MySQL database stack with Docker Compose
  - Separate databases for each microservice (database-per-service pattern)
  - Docker network configuration for service communication

- **Testing Framework**
  - Gatling performance testing module
  - Load testing capabilities for all services
  - Configurable test scenarios
  - Performance metrics and reporting

#### Data Layer
- **Database Architecture**
  - MySQL 9.4 for all services
  - Database-per-service pattern implementation
  - Spring Data JPA / Hibernate ORM
  - JPA entities with proper relationships
  - Automatic schema generation and migration support

- **Data Transfer Layer**
  - MapStruct-based object mapping
  - DTO pattern implementation across all services
  - Type-safe entity-to-DTO conversions
  - Lombok integration for reduced boilerplate

#### API Features
- **RESTful API Endpoints**
  - User Service API at port 8081 (`/cc-user`)
  - Recipe Service API at port 8082 (`/recipes`)
  - Social Service API at port 8083 (`/social`, `/cookbook`)
  - Config Server at port 8888
  - Eureka Server at port 8761
  - Consistent REST patterns across all services

- **Error Handling**
  - Global exception handling for all services
  - Consistent error response format
  - Proper HTTP status codes
  - Detailed error messages for debugging

#### Configuration Management
- **Externalized Configuration**
  - Separate Git repository for all configurations
  - Environment-specific property files
  - Global shared configurations
  - Service-specific configurations
  - Support for configuration encryption

#### Build & CI/CD
- **GitHub Actions Workflows**
  - Automated version validation on pull requests
  - Semantic versioning enforcement
  - Auto-increment parent POM version
  - Service version change detection
  - Validation reporting in PR comments

- **Version Management**
  - Strict semantic versioning (SemVer) enforcement
  - Automated parent POM versioning
  - Manual service versioning with validation
  - Version increment validation (+1 only)
  - Reset rules for minor/major version bumps

#### Documentation
- **Project Documentation**
  - Comprehensive README with setup instructions
  - API endpoint documentation with examples
  - Architecture diagrams and explanations
  - Configuration management guide
  - Troubleshooting guide
  - Development workflow documentation

- **Technical Documentation**
  - Product Definition Document (PDD)
  - Data domain documentation
  - Architecture deviations analysis
  - Maven setup guide
  - Prerequisites and running services guides

### Changed
- Migrated all services from `application.properties` to `application.yml` for better readability
- Renamed `database-compose.yml` to `docker-compose.yml` in docker/databases directory
- Updated all services to use `@EnableDiscoveryClient` annotation
- Enhanced service startup with Eureka registration
- Configured services to fetch configuration from Config Server

### Technical Details

#### Technology Stack
- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.5.6
- **Cloud**: Spring Cloud 2025.0.0
- **Database**: MySQL 9.4
- **Build Tool**: Maven 3.x
- **Containerization**: Docker, Jib
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Config Management**: Spring Cloud Config
- **Object Mapping**: MapStruct 1.5.5
- **Utilities**: Lombok 1.18.42
- **Monitoring**: Spring Boot Actuator
- **Testing**: Gatling 3.13.5

#### Service Ports
- Config Server: 8888
- Eureka Server: 8761
- Gateway Server: 8080
- User Service: 8081
- Recipe Service: 8082
- Social Service: 8083
- Users Database: 3308
- Recipes Database: 3306
- Socials Database: 3307

### Infrastructure
- Docker Compose for database orchestration
- Separate MySQL instances for each microservice
- Centralized configuration repository (CookConnect-config)
- Service discovery with Eureka
- API Gateway for unified access

### Notes
- This is the first official release of CookConnect
- All core microservices are functional with CRUD operations
- Service discovery and API gateway are operational
- Configuration management is fully externalized
- Database-per-service pattern is implemented
- Ready for further feature development and integration testing

[0.1.0]: https://github.com/tkm3d1a/CookConnect/releases/tag/v0.1.0
