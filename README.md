# CookConnect

> A practical recipe management platform with social features, built with Spring Boot microservices

[![Version](https://img.shields.io/badge/version-0.0.1-blue.svg)](https://github.com/yourusername/cookconnect)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## Table of Contents

- [About](#about)
- [Current Status](#current-status)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Configuration Management](#configuration-management)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Environment Variables](#environment-variables)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Development](#development)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

## About

**CookConnect** is a recipe management platform designed to help home cooks store, organize, and share their culinary creations while discovering new recipes from a community of cooking enthusiasts.

### Vision

CookConnect enables home cooks to:
- Centralize their recipe collections in one searchable repository
- Share successful recipes and cooking discoveries with others
- Connect with users who have similar cooking interests and skill levels

### Target Users

- **The Home Cook Organizer**: Needs centralized recipe storage with easy search and retrieval
- **The Recipe Creator**: Wants to document and share original recipes with an audience
- **The Culinary Explorer**: Seeks tried-and-tested recipes from real home cooks

### Mission

To simplify recipe management for home cooks while fostering a supportive community where culinary knowledge and creativity can be shared and preserved.

## Current Status

**Version**: 0.0.1 - Early Development (Foundation Phase)

### ✅ Completed
- [x] Microservices architecture foundation
- [x] Complete data models for all services
- [x] Database schema design and relationships
- [x] Docker-based development environment
- [x] Maven multi-module project setup
- [x] JPA entities with proper relationships
- [x] Docker Compose database stack

### 🚧 In Progress
- [ ] REST API endpoints for all services
- [ ] Service-to-service communication
- [ ] Authentication and authorization (planned: Keycloak)
- [ ] Config Server implementation

### 📋 Planned
- [ ] Frontend application
- [ ] API Gateway
- [ ] Service discovery (Eureka)
- [ ] Circuit breakers and resilience patterns
- [ ] Comprehensive test coverage

## Features

### Recipe Management Service

**Must-Have Features:**
- ✅ Recipe CRUD operations (data model complete)
- ✅ Ingredient lists with quantities and measurements
- ✅ Step-by-step cooking instructions
- ✅ Recipe categorization and tagging
- ✅ Personal notes and modifications
- 🚧 Recipe search (by name, ingredient, category)
- ✅ Visibility controls (private, public, friends-only)

**Planned Enhancements:**
- Recipe duplication/forking
- Bulk import from common formats
- Recipe version history
- Advanced search with filters
- Print-optimized views

### User Management Service

**Must-Have Features:**
- ✅ User registration and profile management
- ✅ Extended profile with demographics
- ✅ Privacy controls
- 🚧 Authentication system
- 🚧 Password reset functionality

**Planned Enhancements:**
- Skill level tracking
- Dietary restrictions and allergies
- Email notifications
- Account deletion with data cleanup

### Social Features Service

**Must-Have Features:**
- ✅ Follow/unfollow users
- ✅ Recipe bookmarking
- ✅ Cookbook collections
- 🚧 Recipe rating system
- 🚧 Activity feed

**Planned Enhancements:**
- Recipe comments and reviews
- User-to-user messaging
- Recommendation engine

## Technology Stack

### Backend
- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2025.0.0
- **Database**: MySQL 9.4
- **ORM**: Spring Data JPA / Hibernate

### Build & Deployment
- **Build Tool**: Maven 3.x
- **Containerization**: Docker, Jib Maven Plugin
- **Container Orchestration**: Docker Compose

### Libraries & Tools
- **Lombok**: Reduce boilerplate code
- **MapStruct**: Object mapping
- **Spring Boot Actuator**: Health monitoring
- **Spring Cloud Config**: Centralized configuration

## Architecture

CookConnect follows a **microservices architecture** with database-per-service pattern for maximum service independence and scalability.

### Services

```
┌─────────────────────────────────────────────────────────────┐
│                     CookConnect Platform                     │
├─────────────────┬─────────────────┬──────────────────────────┤
│  User Service   │ Recipe Service  │   Social Service         │
│                 │                 │                          │
│ • User Identity │ • Recipe Data   │ • Social Graph           │
│ • Profiles      │ • Ingredients   │ • Bookmarks              │
│ • Addresses     │ • Instructions  │ • Cookbooks              │
│ • Auth          │ • Tags          │ • Follows                │
│                 │                 │                          │
│  users-db       │  recipes-db     │  socials-db              │
│  (MySQL)        │  (MySQL)        │  (MySQL)                 │
└─────────────────┴─────────────────┴──────────────────────────┘
                           │
                  ┌────────┴────────┐
                  │  Config Server  │
                  │ (Spring Cloud)  │
                  └─────────────────┘
```

### Design Principles

- **Database-per-Service**: Each microservice has its own MySQL database
- **Loose Coupling**: Services communicate via REST APIs (in development)
- **Domain-Driven Design**: Clear service boundaries around business domains
- **Eventual Consistency**: Cross-service data references use ID-based lookups

## Configuration Management

CookConnect uses **Spring Cloud Config Server** for centralized configuration management, allowing all microservices to fetch their configuration from a single, version-controlled source.

### Why Externalized Configuration?

- **Separation of Concerns**: Configuration is separate from application code
- **Environment-Specific Settings**: Different configs for dev, test, staging, prod
- **Security**: Sensitive values can be encrypted and managed centrally
- **Version Control**: Configuration changes are tracked in Git
- **Dynamic Updates**: Services can refresh configuration without redeployment (with Spring Cloud Bus)

### Configuration Repository

All service configurations are stored in a separate Git repository:

**Repository**: [`CookConnect-config`](https://github.com/tkm3d1a/CookConnect-config)
**Status**: Currently private (contains sensitive configuration data)

### Repository Structure

The configuration repository is organized as follows:

```
CookConnect-config/
├── global/                    # Shared configuration across all services
│   └── application.properties
├── recipe-service/           # Recipe service specific configs
│   ├── recipe-service.properties
│   ├── recipe-service-dev.properties
│   └── recipe-service-prod.properties
├── social-service/           # Social service specific configs
│   ├── social-service.properties
│   ├── social-service-dev.properties
│   └── social-service-prod.properties
└── user-service/             # User service specific configs
    ├── user-service.properties
    ├── user-service-dev.properties
    └── user-service-prod.properties
```

### What's Configured?

Configuration typically includes:
- Database connection strings and credentials
- Service-specific ports and endpoints
- Feature flags and business logic parameters
- Third-party API keys and credentials
- Logging levels
- Cache settings
- JPA/Hibernate properties

### Security Considerations

**Important**: The configuration repository contains sensitive information including:
- Database passwords
- API keys
- OAuth credentials

All sensitive values in the config repo are encrypted using Spring Cloud Config's encryption features. The Config Server uses the `ENCRYPT_KEY` environment variable to decrypt values at runtime.

If you're forking this project:
1. Create your own configuration repository
2. Update `services/config-server/src/main/resources/application.properties` to point to your repo
3. Generate your own encryption key for sensitive values
4. Never commit unencrypted credentials to version control

### Config Server Configuration

The Config Server is configured in `services/config-server/src/main/resources/application.properties`:

```properties
spring.cloud.config.server.git.uri=https://github.com/tkm3d1a/CookConnect-config.git
spring.cloud.config.server.git.search-paths=global,recipe-service,social-service,user-service
spring.profiles.active=native,git
```

### How Services Connect

Each microservice connects to the Config Server using the `spring.config.import` property:

```properties
# Example from user-service/application.properties
spring.config.import=configserver:${CONFIGSERVER_URL}:${CONFIGSERVER_PORT}
```

The Config Server must be running before other services start, as they fetch their configuration on startup.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** (OpenJDK or Oracle JDK)
  ```bash
  java -version
  # Should show version 21.x.x
  ```

- **Maven 3.6+**
  ```bash
  mvn -version
  ```

- **Docker** and **Docker Compose**
  ```bash
  docker --version
  docker compose version
  ```

- **Git**
  ```bash
  git --version
  ```

### Local Setup

Follow these steps to get CookConnect running on your local machine:

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/cookconnect.git
cd cookconnect
```

#### 2. Clone Configuration Repository

**Important**: The configuration repository is required for services to start.

If you have access to the private configuration repository:

```bash
# Clone to a location outside the main project (recommended)
cd ..
git clone https://github.com/tkm3d1a/CookConnect-config.git
cd cookconnect
```

**For Contributors/Forkers**: If you're setting up your own instance:
1. Create your own configuration repository
2. Copy the structure from the example above (see [Configuration Management](#configuration-management))
3. Update `services/config-server/src/main/resources/application.properties`:
   ```properties
   spring.cloud.config.server.git.uri=https://github.com/YOUR-USERNAME/your-config-repo.git
   ```
4. Set up your own encryption key (see Environment Variables below)

#### 3. Create Docker Network

The services communicate through a shared Docker network:

```bash
docker network create cookconnect-network
```

#### 4. Start the Databases

Navigate to the database directory and start the MySQL containers:

```bash
cd docker/databases
docker compose -f database-compose.yml up -d
```

This will start three MySQL instances:
- `users-db` on port `3308`
- `recipes-db` on port `3306`
- `socials-db` on port `3307`

#### 5. Set Up Environment Variables

Before building and running services, set the required environment variables:

**Linux/macOS:**
```bash
export CONFIGSERVER_URL=http://localhost
export CONFIGSERVER_PORT=8888
export ENCRYPT_KEY=your-encryption-key-here
```

**Windows (PowerShell):**
```powershell
$env:CONFIGSERVER_URL="http://localhost"
$env:CONFIGSERVER_PORT="8888"
$env:ENCRYPT_KEY="your-encryption-key-here"
```

**Windows (Command Prompt):**
```cmd
set CONFIGSERVER_URL=http://localhost
set CONFIGSERVER_PORT=8888
set ENCRYPT_KEY=your-encryption-key-here
```

See [Environment Variables](#environment-variables) section for more details.

#### 6. Build the Project

Return to the project root and build all services:

```bash
cd ../..
mvn clean install
```

This compiles all microservices and runs tests.

#### 7. Run Services

**Important**: Services must be started in the correct order. The Config Server must start first, followed by other services.

**Step 1 - Start Config Server:**
```bash
cd services/config-server
mvn spring-boot:run
```

Wait for the Config Server to fully start (look for "Started ConfigServerApplication" in the logs).
The Config Server will be available at `http://localhost:8888`.

**Step 2 - Start Microservices** (in separate terminal windows):

Open new terminal windows for each service and set environment variables in each.

**User Service:**
```bash
cd services/user-service
mvn spring-boot:run
```

**Recipe Service:**
```bash
cd services/recipe-service
mvn spring-boot:run
```

**Social Service:**
```bash
cd services/social-service
mvn spring-boot:run
```

Each service will connect to the Config Server on startup to fetch its configuration.

#### 8. Verify Installation

Check that databases are running:

```bash
docker ps
```

You should see three MySQL containers running.

Check service health (once REST APIs are implemented):
```bash
# These endpoints will be available once Actuator is fully configured
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Recipe Service
curl http://localhost:8083/actuator/health  # Social Service
```

### Environment Variables

#### Required Service Environment Variables

The following environment variables must be set before running services:

| Variable | Description | Default/Example | Required For |
|----------|-------------|-----------------|--------------|
| `CONFIGSERVER_URL` | URL where Config Server is running | `http://localhost` | All microservices |
| `CONFIGSERVER_PORT` | Port Config Server listens on | `8888` | All microservices |
| `ENCRYPT_KEY` | Encryption key for decrypting config values | `your-secret-key` | Config Server |

**Setting Environment Variables:**

**Option 1 - Terminal Session (Temporary):**

*Linux/macOS:*
```bash
export CONFIGSERVER_URL=http://localhost
export CONFIGSERVER_PORT=8888
export ENCRYPT_KEY=your-encryption-key-here
```

*Windows PowerShell:*
```powershell
$env:CONFIGSERVER_URL="http://localhost"
$env:CONFIGSERVER_PORT="8888"
$env:ENCRYPT_KEY="your-encryption-key-here"
```

*Windows CMD:*
```cmd
set CONFIGSERVER_URL=http://localhost
set CONFIGSERVER_PORT=8888
set ENCRYPT_KEY=your-encryption-key-here
```

**Option 2 - IDE Configuration (Recommended for Development):**

In IntelliJ IDEA or Eclipse, set environment variables in your run configurations for each service.

**Option 3 - System Environment Variables (Permanent):**

Add these to your system's environment variables for persistent configuration.

#### Database Environment Variables

Database environment variables are stored in `.env` files within `docker/databases/`:
- `.env` - Common MySQL configuration
- `.env-users` - User database specifics
- `.env-recipe` - Recipe database specifics
- `.env-social` - Social database specifics

**Note**: These `.env` files contain sensitive information and should not be committed to version control in production environments.

#### Encryption Key Generation

For production use, generate a strong encryption key:

```bash
# Generate a random 256-bit key
openssl rand -base64 32
```

Use this key as your `ENCRYPT_KEY` value.

## Project Structure

```
CookConnect/                               # Main application repository
├── docs/                                  # Project documentation
│   ├── documentation.md                   # Documentation index
│   ├── cookconnect_pdd.md                 # Product Definition Document
│   ├── cookconnect_data_domain_v0-0-1.md  # Data model reference
│   ├── cookconnect_architecture_deviations_v0-0-1.md
│   ├── base-pom-setup.md                  # Maven setup guide
│   ├── prereqs.md                         # Prerequisites
│   └── running-services.md                # Service running guide
├── docker/                                # Docker configurations
│   ├── databases/                         # Database Docker Compose
│   │   ├── database-compose.yml
│   │   └── .env files
│   └── [env-specific directories]         # IDE, test, stage, prod environments
├── services/                              # Microservices
│   ├── user-service/                      # User management microservice
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/userservice/
│   │   │   ├── model/                     # JPA entities
│   │   │   ├── repository/                # Spring Data repositories
│   │   │   └── service/                   # Business logic
│   │   └── pom.xml
│   ├── recipe-service/                    # Recipe management microservice
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/recipeservice/
│   │   │   ├── model/                     # Recipe entities
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── pom.xml
│   ├── social-service/                    # Social features microservice
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/socialservice/
│   │   │   ├── model/                     # Social entities
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── pom.xml
│   └── config-server/                     # Spring Cloud Config Server
│       ├── src/main/resources/
│       │   └── application.properties     # Points to config repo
│       └── pom.xml
├── pom.xml                                # Parent POM
└── README.md                              # This file

CookConnect-config/                        # External configuration repository
├── global/                                # (Separate Git repository)
│   └── application.properties             # Shared configuration
├── recipe-service/
│   ├── recipe-service.properties
│   └── recipe-service-{profile}.properties
├── social-service/
│   ├── social-service.properties
│   └── social-service-{profile}.properties
└── user-service/
    ├── user-service.properties
    └── user-service-{profile}.properties
```

**Note**: The `CookConnect-config` repository is separate and contains all service configurations. It must be cloned independently (see [Getting Started](#getting-started)).

### Service Details

#### User Service
**Package**: `com.tkforgeworks.cookconnect.userservice`

Handles user identity, authentication, profiles, and personal information including addresses and skill levels.

#### Recipe Service
**Package**: `com.tkforgeworks.cookconnect.recipeservice`

Manages recipes, ingredients, cooking instructions, tags, and user notes. Implements a sophisticated list management pattern for complex recipe data.

#### Social Service
**Package**: `com.tkforgeworks.cookconnect.socialservice`

Handles social interactions including user follows, recipe bookmarks, and cookbook collections.

#### Config Server
**Package**: `com.tkforgeworks.cookconnect.configserver`

Centralized configuration management for all microservices using Spring Cloud Config.

## Documentation

Comprehensive documentation is available in the `/docs` directory:

- **[Documentation Index](docs/documentation.md)** - Quick links to all documentation
- **[Product Definition Document](docs/cookconnect_pdd.md)** - Complete product vision and requirements
- **[Data Domain Documentation](docs/cookconnect_data_domain_v0-0-1.md)** - Detailed entity and relationship reference
- **[Architecture Deviations](docs/cookconnect_architecture_deviations_v0-0-1.md)** - Analysis of design decisions
- **[Base POM Setup](docs/base-pom-setup.md)** - Maven configuration guide

## Development

### Building the Project

**Clean build:**
```bash
mvn clean install
```

**Skip tests:**
```bash
mvn clean install -DskipTests
```

**Build specific service:**
```bash
cd services/user-service
mvn clean package
```

### Creating Docker Images

Docker images are built using Jib Maven Plugin:

```bash
# Build image for a specific service
cd services/user-service
mvn compile jib:dockerBuild

# Images are tagged as:
# tkforgeworks/cookconnect-user-service:latest
# tkforgeworks/cookconnect-recipe-service:latest
# tkforgeworks/cookconnect-social-service:latest
# tkforgeworks/cookconnect-config-server:latest
```

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific service
cd services/recipe-service
mvn test
```

### Database Management

**Start databases:**
```bash
cd docker/databases
docker compose -f database-compose.yml up -d
```

**Stop databases:**
```bash
docker compose -f database-compose.yml down
```

**View logs:**
```bash
docker compose -f database-compose.yml logs -f
```

**Reset databases (⚠️ Destroys all data):**
```bash
docker compose -f database-compose.yml down -v
docker compose -f database-compose.yml up -d
```

### Troubleshooting

#### Config Server Connection Issues

**Problem**: Services fail to start with "Could not resolve placeholder" or "Connection refused to Config Server"

**Solutions:**

1. **Verify Config Server is running:**
   ```bash
   curl http://localhost:8888/actuator/health
   ```
   Should return: `{"status":"UP"}`

2. **Check environment variables are set:**
   ```bash
   echo $CONFIGSERVER_URL $CONFIGSERVER_PORT  # Linux/macOS
   echo %CONFIGSERVER_URL% %CONFIGSERVER_PORT%  # Windows CMD
   ```

3. **Test Config Server endpoint:**
   ```bash
   # Should return configuration for user-service
   curl http://localhost:8888/user-service/default
   ```

4. **Verify config repository access:**
   - Ensure CookConnect-config repository is cloned
   - Check Git credentials in config-server/application.properties
   - Verify `ENCRYPT_KEY` is set if using encrypted values

**Problem**: "Failed to decrypt" errors

**Solution:**
- Ensure `ENCRYPT_KEY` environment variable matches the key used to encrypt values
- Verify the key is set before starting Config Server

**Problem**: Services start but don't have correct configuration

**Solution:**
1. Check Config Server logs for errors fetching from Git
2. Verify the service name in `spring.application.name` matches the config directory name
3. Ensure the correct profile is active (default, dev, prod, etc.)
4. Refresh configuration endpoint (if implemented):
   ```bash
   curl -X POST http://localhost:8081/actuator/refresh
   ```

#### Database Connection Issues

**Problem**: Service fails with "Communications link failure" or "Unknown database"

**Solutions:**

1. **Check databases are running:**
   ```bash
   docker ps | grep db
   ```

2. **Verify database ports:**
   - users-db: 3308
   - recipes-db: 3306
   - socials-db: 3307

3. **Check database configuration in config repository**

4. **Test database connection:**
   ```bash
   docker exec -it users-db mysql -u root -p
   ```

#### Build Issues

**Problem**: Maven build fails with "package does not exist"

**Solution:**
- Clean and rebuild: `mvn clean install`
- Check Java version: `java -version` (should be 21)
- Verify Maven version: `mvn -version` (should be 3.6+)

## Roadmap

### Version 0.1 (Current - Foundation)
- ✅ Microservices architecture
- ✅ Data models and database schema
- 🚧 REST API implementation
- 🚧 Service communication

### Version 0.2 (Authentication & Core Features)
- Authentication with Keycloak
- Complete REST APIs
- Basic recipe search
- User registration and login

### Version 1.0 (MVP)
- API Gateway
- Service discovery
- Complete recipe management
- User profiles and social features
- Recipe bookmarking and collections

### Version 1.1
- Media upload and recipe photos
- Advanced search with filters
- Recipe recommendations

### Version 1.2
- Meal planning integration
- Shopping list generation
- Recipe import/export

### Version 2.0
- Mobile application
- Nutritional analysis service
- Enhanced social features (groups, events)

### Version 3.0
- Smart kitchen device integration
- Recipe cost calculation
- Inventory management

## Contributing

This is primarily a personal learning project, but suggestions and improvements are welcome!

If you'd like to contribute:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot) and [Spring Cloud](https://spring.io/projects/spring-cloud)
- Database: [MySQL](https://www.mysql.com/)
- Containerization: [Docker](https://www.docker.com/)
- Object mapping: [MapStruct](https://mapstruct.org/)
- Code generation: [Lombok](https://projectlombok.org/)

---

**Note**: CookConnect is currently in early development. APIs and features are subject to change. This is a learning project exploring microservices architecture, Spring Boot, and distributed systems design.

For questions or suggestions, please open an issue on GitHub.
