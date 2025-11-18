# CookConnect

> A practical recipe management platform with social features, built with Spring Boot microservices

[![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)](https://github.com/tkm3d1a/cookconnect)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## Table of Contents

- [About](#about)
- [Current Status](#current-status)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Configuration Management](#configuration-management)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Environment Variables](#environment-variables)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Development](#development)
- [Versioning Strategy](#versioning-strategy)
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

**Version**: 0.1.0 - Service Discovery & Gateway Complete

### ✅ Completed
- [x] Microservices architecture foundation
- [x] Complete data models for all services
- [x] Database schema design and relationships
- [x] Docker-based development environment
- [x] Maven multi-module project setup
- [x] JPA entities with proper relationships
- [x] Docker Compose database stack
- [x] Config Server implementation
- [x] REST API endpoints (CRUD operations)
- [x] Global exception handling for all services
- [x] DTO pattern with MapStruct mappers
- [x] Service layer business logic
- [x] Service discovery (Eureka Server)
- [x] API Gateway (Spring Cloud Gateway)
- [x] Performance testing framework (Gatling)

### 🚧 In Progress
- [ ] API documentation (Swagger/OpenAPI - Issue #6)
- [ ] Service-to-service communication through Gateway
- [ ] Authentication and authorization (planned: Keycloak)
- [ ] Comprehensive test coverage

### 📋 Planned
- [ ] Frontend application
- [ ] Circuit breakers and resilience patterns
- [ ] Pagination for list endpoints
- [ ] Advanced search and filtering
- [ ] Load balancing and routing rules

## Features

### Recipe Management Service

**Completed Features:**
- ✅ Recipe creation (simple and detailed modes)
- ✅ Recipe retrieval (individual and list all)
- ✅ Ingredient lists with quantities and measurements
- ✅ Step-by-step cooking instructions
- ✅ Recipe categorization and tagging
- ✅ Visibility controls (private, public, friends-only)
- ✅ REST API endpoints with proper error handling

**In Progress:**
- 🚧 Recipe update and delete operations
- 🚧 Recipe search (by name, ingredient, category)

**Planned Enhancements:**
- Recipe duplication/forking
- Bulk import from common formats
- Recipe version history
- Advanced search with filters
- Print-optimized views

### User Management Service

**Completed Features:**
- ✅ User registration (create new users)
- ✅ User profile management (full CRUD)
- ✅ Extended profile with demographics and addresses
- ✅ Password management (add/update passwords)
- ✅ Privacy controls
- ✅ REST API endpoints with proper error handling

**In Progress:**
- 🚧 Authentication system (planned: Keycloak)
- 🚧 Password reset functionality

**Planned Enhancements:**
- Skill level tracking
- Dietary restrictions and allergies
- Email notifications
- Account deletion with cascade cleanup

### Social Features Service

**Completed Features:**
- ✅ Follow/unfollow users
- ✅ Recipe bookmarking/unbookmarking
- ✅ Cookbook collections (full CRUD)
- ✅ Cookbook entries management
- ✅ Cookbook notes
- ✅ Social profile management
- ✅ REST API endpoints with proper error handling

**In Progress:**
- 🚧 Recipe rating system
- 🚧 Activity feed

**Planned Enhancements:**
- Recipe comments and reviews
- User-to-user messaging
- Recommendation engine
- Social notifications

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
                    ┌─────────────────────┐
                    │   API Gateway       │
                    │   (Port 8080)       │
                    │  Spring Cloud GW    │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼────────┐  ┌───▼──────────┐  ┌──▼──────────────┐
    │  User Service    │  │Recipe Service│  │ Social Service  │
    │   (Port 8081)    │  │ (Port 8082)  │  │  (Port 8083)    │
    │                  │  │              │  │                 │
    │ • User Identity  │  │ • Recipes    │  │ • Social Graph  │
    │ • Profiles       │  │ • Ingredients│  │ • Bookmarks     │
    │ • Addresses      │  │ • Instructions│ │ • Cookbooks     │
    │ • Auth           │  │ • Tags       │  │ • Follows       │
    │                  │  │              │  │                 │
    │  users-db        │  │  recipes-db  │  │  socials-db     │
    │  (MySQL:3308)    │  │(MySQL:3306)  │  │ (MySQL:3307)    │
    └──────────────────┘  └──────────────┘  └─────────────────┘
              │                │                │
              └────────────────┼────────────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼────────┐  ┌───▼──────────┐     │
    │  Eureka Server   │  │Config Server │     │
    │   (Port 8761)    │  │ (Port 8888)  │     │
    │ Service Discovery│◄─┤Centralized   │◄────┘
    │                  │  │Configuration │
    └──────────────────┘  └──────────────┘
```

### Design Principles

- **Database-per-Service**: Each microservice has its own MySQL database
- **Loose Coupling**: Services communicate via REST APIs (in development)
- **Domain-Driven Design**: Clear service boundaries around business domains
- **Eventual Consistency**: Cross-service data references use ID-based lookups

## API Endpoints

CookConnect provides RESTful APIs for all microservices. Each service runs on a dedicated port and exposes CRUD operations for its domain entities.

### Service Base URLs

| Service | Port | Base URL | Status |
|---------|------|----------|--------|
| API Gateway | 8080 | `http://localhost:8080` | ✅ Active |
| User Service | 8081 | `http://localhost:8081` | ✅ Active |
| Recipe Service | 8082 | `http://localhost:8082` | ✅ Active |
| Social Service | 8083 | `http://localhost:8083` | ✅ Active |
| Eureka Server | 8761 | `http://localhost:8761` | ✅ Active |
| Config Server | 8888 | `http://localhost:8888` | ✅ Active |

### User Service API (`/cc-user`)

**Base Path**: `http://localhost:8081/cc-user`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/cc-user` | Get all users | - |
| `GET` | `/cc-user/{ccUserId}` | Get user by ID | - |
| `POST` | `/cc-user` | Create new user | `CCUserDto` |
| `POST` | `/cc-user/{ccUserId}/password-new` | Add/update password | `PasswordDto` |
| `PUT` | `/cc-user/{ccUserId}` | Update user | `UpdateCCUserDTO` |
| `DELETE` | `/cc-user/{ccUserId}` | Delete user | - |

**Example Request - Create User**:
```bash
curl -X POST http://localhost:8081/cc-user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Example Response**:
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2025-10-13T10:30:00"
}
```

### Recipe Service API (`/recipes`)

**Base Path**: `http://localhost:8082/recipes`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/recipes` | Get all recipes (summary) | - |
| `POST` | `/recipes` | Create simple recipe | `RecipeCreateSimpleDto` |
| `POST` | `/recipes/detailed` | Create detailed recipe | `RecipeCreateDetailedDto` |

**Example Request - Create Simple Recipe**:
```bash
curl -X POST http://localhost:8082/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Classic Chocolate Chip Cookies",
    "description": "Delicious homemade cookies",
    "creatorUserId": 1,
    "visibility": "PUBLIC"
  }'
```

**Example Request - Create Detailed Recipe**:
```bash
curl -X POST http://localhost:8082/recipes/detailed \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spaghetti Carbonara",
    "description": "Traditional Italian pasta dish",
    "creatorUserId": 1,
    "visibility": "PUBLIC",
    "ingredients": [
      {
        "name": "Spaghetti",
        "quantity": 400,
        "unit": "GRAMS"
      },
      {
        "name": "Eggs",
        "quantity": 4,
        "unit": "WHOLE"
      }
    ],
    "instructions": [
      {
        "stepNumber": 1,
        "instruction": "Cook pasta according to package directions"
      },
      {
        "stepNumber": 2,
        "instruction": "Beat eggs in a bowl"
      }
    ],
    "tags": [
      {
        "tagName": "Italian",
        "category": "CUISINE"
      }
    ]
  }'
```

### Social Service API

#### Cookbook Management (`/cookbook`)

**Base Path**: `http://localhost:8083/cookbook`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/cookbook` | Get all cookbooks | - |
| `GET` | `/cookbook/{cookbookId}` | Get cookbook by ID | - |
| `GET` | `/cookbook/{cookbookId}/entries` | Get all entries in cookbook | - |
| `GET` | `/cookbook/{cookbookId}/entries/{entryId}` | Get specific entry | - |
| `GET` | `/cookbook/{cookbookId}/note` | Get cookbook note | - |
| `POST` | `/cookbook` | Create new cookbook | `CookbookDto` |
| `POST` | `/cookbook/{cookbookId}/entries` | Add entry to cookbook | `CookbookEntryDto` |
| `POST` | `/cookbook/{cookbookId}/note` | Add note to cookbook | `CookbookNoteDto` |
| `PUT` | `/cookbook/{cookbookId}` | Update cookbook | `CookbookDto` |
| `PUT` | `/cookbook/{cookbookId}/entries/{entryId}` | Update entry | `CookbookEntryDto` |
| `PUT` | `/cookbook/{cookbookId}/note` | Update note | `CookbookNoteDto` |
| `DELETE` | `/cookbook/{cookbookId}` | Delete cookbook | - |
| `DELETE` | `/cookbook/{cookbookId}/entries` | Delete all entries | - |
| `DELETE` | `/cookbook/{cookbookId}/entries/{entryId}` | Delete specific entry | - |
| `DELETE` | `/cookbook/{cookbookId}/note` | Delete cookbook note | - |

**Example Request - Create Cookbook**:
```bash
curl -X POST http://localhost:8083/cookbook \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Favorite Desserts",
    "description": "A collection of my go-to dessert recipes",
    "userId": 1,
    "visibility": "PUBLIC"
  }'
```

#### Social Interactions (`/social`)

**Base Path**: `http://localhost:8083/social`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `GET` | `/social/{socialId}` | Get social profile | - |
| `GET` | `/social/{socialId}/followers` | Get list of follower IDs | - |
| `GET` | `/social/{socialId}/following` | Get list of following IDs | - |
| `GET` | `/social/{socialId}/bookmarks` | Get list of bookmarked recipe IDs | - |
| `POST` | `/social` | Create social profile | `SocialInteractionDto` |
| `POST` | `/social/{socialId}/follow/{targetUserId}` | Follow a user | - |
| `POST` | `/social/{socialId}/bookmark/{targetRecipeId}` | Bookmark a recipe | - |
| `POST` | `/social/{socialId}/create-cookbook` | Create cookbook for user | `CookbookDto` |
| `DELETE` | `/social/{socialId}/follow/{targetUserId}` | Unfollow a user | - |
| `DELETE` | `/social/{socialId}/bookmark/{targetRecipeId}` | Remove bookmark | - |

**Example Request - Follow User**:
```bash
curl -X POST http://localhost:8083/social/1/follow/5
```

**Example Request - Bookmark Recipe**:
```bash
curl -X POST http://localhost:8083/social/1/bookmark/42
```

### Common Response Patterns

All services implement consistent error handling with the following response structure:

**Success Response** (2xx):
```json
{
  "data": { /* entity data */ }
}
```

**Error Response** (4xx/5xx):
```json
{
  "timestamp": "2025-10-13T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 999 not found",
  "path": "/cc-user/999"
}
```

### API Documentation

**Note**: Comprehensive API documentation with Swagger/OpenAPI is in development (see [Issue #6](https://github.com/tkm3d1a/CookConnect/issues/6)). Once complete, interactive API documentation will be available at:
- User Service: `http://localhost:8081/swagger-ui.html`
- Recipe Service: `http://localhost:8082/swagger-ui.html`
- Social Service: `http://localhost:8083/swagger-ui.html`

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

**Important**: Services must be started in the correct order for proper initialization.

**Startup Order:**
1. Config Server (8888)
2. Eureka Server (8761)
3. Gateway Server (8080)
4. Microservices (8081-8083)

**Step 1 - Start Config Server:**
```bash
cd servers/config-server
mvn spring-boot:run
```

Wait for the Config Server to fully start (look for "Started ConfigServerApplication" in the logs).
The Config Server will be available at `http://localhost:8888`.

**Step 2 - Start Eureka Server:**

Open a new terminal window and set environment variables.

```bash
cd servers/eureka-server
mvn spring-boot:run
```

Wait for Eureka to start (look for "Started EurekaserverApplication" in the logs).
Access the Eureka dashboard at `http://localhost:8761`.

**Step 3 - Start Gateway Server:**

Open a new terminal window and set environment variables.

```bash
cd servers/gateway-server
mvn spring-boot:run
```

Wait for the Gateway to start and register with Eureka.
The Gateway will be available at `http://localhost:8080`.

**Step 4 - Start Microservices** (in separate terminal windows):

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

Each service will:
- Connect to the Config Server to fetch its configuration
- Register with Eureka Server for service discovery
- Become available through the API Gateway

#### 8. Verify Installation

Check that databases are running:

```bash
docker ps
```

You should see three MySQL containers running.

Check infrastructure services:
```bash
# Config Server
curl http://localhost:8888/actuator/health

# Eureka Server - view dashboard
open http://localhost:8761  # macOS
# or navigate to http://localhost:8761 in your browser

# Gateway Server
curl http://localhost:8080/actuator/health
```

Check microservices health:
```bash
# Health check endpoints (Actuator)
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Recipe Service
curl http://localhost:8083/actuator/health  # Social Service
```

Verify service registration with Eureka:
- Open the Eureka dashboard at `http://localhost:8761`
- You should see all services (Gateway, User, Recipe, Social) listed as registered instances
- Each service should show status "UP"

#### 9. Test the APIs

Once services are running, test the REST endpoints to verify functionality.

**Test User Service**:
```bash
# Create a new user
curl -X POST http://localhost:8081/cc-user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'

# Get all users
curl http://localhost:8081/cc-user

# Get specific user (use ID from create response)
curl http://localhost:8081/cc-user/1
```

**Test Recipe Service**:
```bash
# Create a simple recipe
curl -X POST http://localhost:8082/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Recipe",
    "description": "A test recipe",
    "creatorUserId": 1,
    "visibility": "PUBLIC"
  }'

# Get all recipes
curl http://localhost:8082/recipes
```

**Test Social Service**:
```bash
# Create a social profile
curl -X POST http://localhost:8083/social \
  -H "Content-Type: application/json" \
  -d '{
    "forUserId": 1
  }'

# Follow another user
curl -X POST http://localhost:8083/social/1/follow/2

# Create a cookbook
curl -X POST http://localhost:8083/cookbook \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Test Cookbook",
    "description": "Testing cookbook creation",
    "userId": 1,
    "visibility": "PRIVATE"
  }'
```

**Common Workflow - Create User and Recipe**:
```bash
# Step 1: Create a user
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/cc-user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "chef123",
    "email": "chef@example.com",
    "firstName": "Gordon",
    "lastName": "Cook"
  }')

# Step 2: Extract user ID (requires jq - install with: apt-get install jq or brew install jq)
USER_ID=$(echo $USER_RESPONSE | jq -r '.id')

# Step 3: Create a recipe for that user
curl -X POST http://localhost:8082/recipes/detailed \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Chef's Special Pasta\",
    \"description\": \"My signature dish\",
    \"creatorUserId\": $USER_ID,
    \"visibility\": \"PUBLIC\",
    \"ingredients\": [
      {
        \"name\": \"Pasta\",
        \"quantity\": 500,
        \"unit\": \"GRAMS\"
      }
    ],
    \"instructions\": [
      {
        \"stepNumber\": 1,
        \"instruction\": \"Boil water and cook pasta\"
      }
    ]
  }"

# Step 4: Create social profile and bookmark the recipe
curl -X POST http://localhost:8083/social \
  -H "Content-Type: application/json" \
  -d "{\"forUserId\": $USER_ID}"

curl -X POST http://localhost:8083/social/$USER_ID/bookmark/1
```

For more detailed API documentation, see the [API Endpoints](#api-endpoints) section.

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
│   │   ├── docker-compose.yml             # Database stack configuration
│   │   └── .env files                     # Database environment variables
│   └── [env-specific directories]         # IDE, test, stage, prod environments
├── services/                              # Microservices
│   ├── user-service/                      # User management microservice
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/userservice/
│   │   │   ├── controller/                # REST controllers (CCUserController)
│   │   │   ├── model/                     # JPA entities
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   └── mapper/                # MapStruct mappers
│   │   │   ├── repository/                # Spring Data repositories
│   │   │   ├── service/                   # Business logic
│   │   │   ├── errorhandler/              # Global exception handling
│   │   │   └── config/                    # Service configuration
│   │   └── pom.xml
│   ├── recipe-service/                    # Recipe management microservice
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/recipeservice/
│   │   │   ├── controller/                # REST controllers (RecipeController)
│   │   │   ├── model/                     # Recipe entities
│   │   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   │   ├── mapper/                # MapStruct mappers
│   │   │   │   └── enums/                 # Enumerations
│   │   │   ├── repository/                # Spring Data repositories
│   │   │   ├── service/                   # Business logic
│   │   │   ├── errorhandler/              # Global exception handling
│   │   │   └── config/                    # Service configuration
│   │   └── pom.xml
│   └── social-service/                    # Social features microservice
│       ├── src/main/java/com/tkforgeworks/cookconnect/socialservice/
│       │   ├── controller/                # REST controllers (CookbookController, SocialInteractionController)
│       │   ├── model/                     # Social entities
│       │   │   ├── dto/                   # Data Transfer Objects
│       │   │   └── mapper/                # MapStruct mappers
│       │   ├── repository/                # Spring Data repositories
│       │   ├── service/                   # Business logic
│       │   ├── errorhandler/              # Global exception handling
│       │   └── config/                    # Service configuration
│       └── pom.xml
├── servers/                               # Infrastructure servers
│   ├── config-server/                     # Spring Cloud Config Server
│   │   ├── src/main/resources/
│   │   │   └── application.yml            # Config server settings
│   │   └── pom.xml
│   ├── eureka-server/                     # Netflix Eureka Service Discovery
│   │   ├── src/main/java/com/tkforgeworks/cookconnect/eurekaserver/
│   │   │   ├── EurekaserverApplication.java
│   │   │   └── logging/                   # Eureka diagnostics
│   │   ├── src/main/resources/
│   │   │   └── application.yml            # Eureka server settings
│   │   └── pom.xml
│   └── gateway-server/                    # Spring Cloud Gateway
│       ├── src/main/resources/
│       │   └── application.yml            # Gateway routing configuration
│       └── pom.xml
├── testing/                               # Testing modules
│   ├── gatling-tests/                     # Performance testing
│   │   ├── src/test/java/                 # Gatling simulation scenarios
│   │   └── pom.xml
│   └── helper/                            # Testing utilities
├── pom.xml                                # Parent POM
├── CHANGELOG.md                           # Release notes and version history
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
├── user-service/
│   ├── user-service.properties
│   └── user-service-{profile}.properties
├── eureka-server/
│   ├── eureka-server.properties
│   └── eureka-server-{profile}.properties
└── gateway-server/
    ├── gateway-server.properties
    └── gateway-server-{profile}.properties
```

**Note**: The `CookConnect-config` repository is separate and contains all service configurations. It must be cloned independently (see [Getting Started](#getting-started)).

### Service Details

#### User Service
**Package**: `com.tkforgeworks.cookconnect.userservice`
**Port**: 8081

Handles user identity, authentication, profiles, and personal information including addresses and skill levels.

**Key Components**:
- **Controllers**: `CCUserController` - REST endpoints for user management
- **DTOs**: `CCUserDto`, `UpdateCCUserDTO`, `PasswordDto`, `ProfileInfoDto`, `AddressDto`
- **Mappers**: `UserServiceMapper` - MapStruct entity-to-DTO conversions
- **Services**: `CCUserService`, `ProfileInfoService` - Business logic
- **Error Handling**: Global exception handler with `ExceptionController`

#### Recipe Service
**Package**: `com.tkforgeworks.cookconnect.recipeservice`
**Port**: 8082

Manages recipes, ingredients, cooking instructions, tags, and user notes. Implements a sophisticated list management pattern for complex recipe data.

**Key Components**:
- **Controllers**: `RecipeController` - REST endpoints for recipe management
- **DTOs**: `RecipeDto`, `RecipeCreateSimpleDto`, `RecipeCreateDetailedDto`, `RecipeSummaryDto`, `IngredientDto`, `InstructionDto`, `TagDto`
- **Mappers**: `RecipeServiceMapper` - MapStruct entity-to-DTO conversions
- **Services**: `RecipeService`, `IngredientService`, `InstructionService`, `TagService` - Business logic
- **Error Handling**: Global exception handler with `ExceptionController`

#### Social Service
**Package**: `com.tkforgeworks.cookconnect.socialservice`
**Port**: 8083

Handles social interactions including user follows, recipe bookmarks, and cookbook collections.

**Key Components**:
- **Controllers**: `SocialInteractionController`, `CookbookController` - REST endpoints for social features
- **DTOs**: `SocialInteractionDto`, `CookbookDto`, `CookbookEntryDto`, `CookbookNoteDto`
- **Mappers**: `SocialInteractionMapper` - MapStruct entity-to-DTO conversions
- **Services**: `SocialInteractionService`, `CookbookService`, `CookbookEntryService`, `CookbookNoteService` - Business logic
- **Error Handling**: Global exception handler with `ExceptionController`

#### Config Server
**Package**: `com.tkforgeworks.cookconnect.configserver`
**Port**: 8888

Centralized configuration management for all microservices using Spring Cloud Config.

#### Eureka Server
**Package**: `com.tkforgeworks.cookconnect.eurekaserver`
**Port**: 8761

Service discovery and registration using Netflix Eureka, enabling microservices to locate and communicate with each other dynamically.

**Key Components**:
- **Application**: `EurekaserverApplication` - Main Eureka Server application
- **Diagnostics**: `EurekaDiagnostics` - Monitors and logs service registration events
- **Features**: Service registration, health monitoring, instance management

**Dashboard**: Access the Eureka dashboard at `http://localhost:8761` to view registered services and their status.

#### Gateway Server
**Package**: `com.tkforgeworks.cookconnect.gatewayserver`
**Port**: 8080

API Gateway providing a unified entry point for all microservices using Spring Cloud Gateway with reactive WebFlux.

**Key Features**:
- Dynamic routing to registered services via Eureka
- Load balancing across service instances
- Centralized cross-cutting concerns (future: authentication, rate limiting)
- Reactive, non-blocking architecture

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

# Build image for a specific server
cd servers/eureka-server
mvn compile jib:dockerBuild

# Images are tagged as:
# tkforgeworks/cookconnect-user-service:latest
# tkforgeworks/cookconnect-recipe-service:latest
# tkforgeworks/cookconnect-social-service:latest
# tkforgeworks/cookconnect-config-server:latest
# tkforgeworks/cookconnect-eureka-server:latest
# tkforgeworks/cookconnect-gateway-server:latest
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

#### API Issues

**Problem**: HTTP 404 Not Found when calling API endpoints

**Solutions:**

1. **Verify service is running:**
   ```bash
   curl http://localhost:8081/actuator/health  # User Service
   curl http://localhost:8082/actuator/health  # Recipe Service
   curl http://localhost:8083/actuator/health  # Social Service
   ```

2. **Check you're using the correct port:**
   - User Service: 8081
   - Recipe Service: 8082
   - Social Service: 8083

3. **Verify endpoint path** - common mistakes:
   - User endpoint is `/cc-user` not `/user`
   - Recipe endpoint is `/recipes` not `/recipe`
   - Social endpoints are `/social` and `/cookbook`

**Problem**: HTTP 500 Internal Server Error

**Solutions:**

1. **Check service logs** for detailed error messages
2. **Verify database is running:**
   ```bash
   docker ps | grep db
   ```
3. **Check database connectivity** in service logs
4. **Ensure foreign key references are valid** (e.g., `creatorUserId` exists in user-service)

**Problem**: HTTP 400 Bad Request

**Solutions:**

1. **Verify JSON payload is valid:**
   - Check for missing required fields
   - Ensure field names match DTO exactly (case-sensitive)
   - Validate enum values (e.g., `visibility` must be `PUBLIC`, `PRIVATE`, or `FRIENDS_ONLY`)

2. **Check Content-Type header:**
   ```bash
   -H "Content-Type: application/json"
   ```

3. **Test with minimal payload first**, then add fields incrementally

**Problem**: Data not persisting or returning null values

**Solutions:**

1. **Check entity relationships** in database
2. **Verify mapper configurations** (MapStruct may need rebuild)
3. **Rebuild project:**
   ```bash
   mvn clean install
   ```

**Problem**: Cannot connect to service after starting

**Solutions:**

1. **Wait for service startup** - Spring Boot services take 30-60 seconds to fully start
2. **Check logs for "Started [ServiceName]Application" message**
3. **Verify port is not already in use:**
   ```bash
   # Windows
   netstat -ano | findstr :8081

   # Linux/macOS
   lsof -i :8081
   ```

## Versioning Strategy

CookConnect follows strict **Semantic Versioning (SemVer)** with automated validation through GitHub Actions to ensure version consistency and traceability.

### Semantic Versioning Format

All versions follow the `X.Y.Z` format:
- **X** (Major): Breaking changes, significant API changes
- **Y** (Minor): New features, backwards-compatible additions
- **Z** (Patch): Bug fixes, minor improvements, backwards-compatible

### Version Management Rules

#### Parent POM Versioning
The parent POM version (`pom.xml` in the root directory) is **automatically incremented** on every pull request to `main`:
- GitHub Actions workflow increments the patch version (+1)
- No manual intervention required
- Commits with `[skip ci]` to prevent infinite loops

**Example**: `0.0.1` → `0.0.2` on every PR merge

#### Service POM Versioning
Individual service versions (`services/{service-name}/pom.xml`) are **manually managed**:
- **Must be incremented** when that service has code or POM changes
- Version must increase compared to the `main` branch
- Only **+1 increments** are allowed per component

### Increment Rules

#### Patch Version (Z)
```
0.0.1 → 0.0.2  ✅ Valid
0.0.1 → 0.0.3  ❌ Invalid (must increment by exactly 1)
```

**When to use**: Bug fixes, minor code improvements, dependency updates

#### Minor Version (Y)
```
0.0.5 → 0.1.0  ✅ Valid (resets patch to 0)
0.0.5 → 0.1.1  ❌ Invalid (patch must be 0)
0.0.5 → 0.2.0  ❌ Invalid (can only increment by 1)
```

**When to use**: New features, new API endpoints, backwards-compatible additions

#### Major Version (X)
```
0.5.3 → 1.0.0  ✅ Valid (resets minor and patch to 0)
0.5.3 → 1.0.1  ❌ Invalid (minor and patch must be 0)
0.5.3 → 1.1.0  ❌ Invalid (minor and patch must be 0)
0.5.3 → 2.0.0  ❌ Invalid (can only increment by 1)
```

**When to use**: Breaking changes, API redesign, incompatible updates

### Automated Version Validation

Every pull request to `main` triggers the **Version Validation** GitHub Actions workflow:

#### Workflow Steps
1. **Auto-Increment Parent POM**
   - Fetches current parent version from `main` branch
   - Increments patch version by 1
   - Updates `pom.xml` and commits back to PR branch
   - Skips if PR already has correct or higher version

2. **Detect Changed Services**
   - Scans PR for changes in service directories
   - Monitors: `services/{service-name}/src/**` and `services/{service-name}/pom.xml`
   - Builds matrix of services requiring validation

3. **Validate Service Versions**
   - For each changed service, validates:
     - Version format is `X.Y.Z`
     - Version increased compared to `main` branch
     - Increment is exactly +1 on one component
     - Reset rules applied correctly (Y↑→Z=0, X↑→Y=Z=0)

4. **Report Status**
   - Posts PR comment with validation results
   - Fails PR if validation errors found
   - Provides clear error messages with fix suggestions

### Example Validation Output

**Successful Validation:**
```
🔍 Version Validation Results

Parent POM Version: Auto-incremented to `0.0.5` ✅

Changed Services:
- `user-service`

✅ All service versions validated successfully!
```

**Failed Validation:**
```
🔍 Version Validation Results

Parent POM Version: Auto-incremented to `0.0.5` ✅

Changed Services:
- `user-service`

❌ Service version validation failed. Please check the logs above for details.

Common issues:
- Version not incremented in changed service
- Invalid semantic versioning format (must be X.Y.Z)
- Increment must be exactly +1 for one component
- Minor increment must reset patch to 0
- Major increment must reset minor and patch to 0
```

### Manual Validation Script

A standalone validation script is available for local testing:

```bash
# Usage
.github/scripts/validate-semver.sh <main_version> <pr_version> <service_name>

# Example
.github/scripts/validate-semver.sh 0.0.1 0.0.2 user-service
```

This script can be used before committing to verify your version increment is valid.

### Developer Workflow

When making changes to a service:

1. **Make your code changes** in the service directory
2. **Update the service version** in `services/{service-name}/pom.xml`:
   ```xml
   <version>0.0.2</version>  <!-- Increment appropriately -->
   ```
3. **Commit and push** your changes
4. **Create a pull request** to `main`
5. **GitHub Actions will**:
   - Auto-increment the parent POM version
   - Validate your service version increment
   - Report results in PR comment
6. **Fix any validation errors** if needed
7. **Merge** once validation passes

### Workflow Files

The version validation system consists of:
- `.github/workflows/version-validation.yml` - Main GitHub Actions workflow
- `.github/scripts/validate-semver.sh` - Reusable validation script

### Disabling Validation (Not Recommended)

To skip validation for a specific PR (emergency fixes only):
- Add `[skip ci]` to your commit message
- This will skip the workflow entirely

**Warning**: Skipping validation can lead to version inconsistencies and should only be used in exceptional circumstances.

## Roadmap

### Version 0.1.0 (Released - Service Discovery & Gateway) ✅
- ✅ Microservices architecture foundation
- ✅ Data models and database schema
- ✅ REST API implementation (CRUD operations)
- ✅ Global exception handling
- ✅ DTO pattern with MapStruct
- ✅ User service (full CRUD)
- ✅ Recipe service (create/read operations)
- ✅ Social service (full CRUD for cookbooks and interactions)
- ✅ Config Server for centralized configuration
- ✅ Eureka Server for service discovery
- ✅ API Gateway with Spring Cloud Gateway
- ✅ Performance testing framework with Gatling
- ✅ Docker support with Jib

### Version 0.2.0 (Next - Enhanced Features)
- Complete recipe service (update/delete operations)
- Basic recipe search (by name, ingredient, tags)
- Swagger/OpenAPI documentation
- Pagination for list endpoints
- Enhanced error responses with validation
- Gateway routing configurations for all services

### Version 0.3.0 (Authentication & Security)
- Authentication with Keycloak
- JWT token validation
- Role-based access control (RBAC)
- Secure inter-service communication
- User registration and login flows
- OAuth2 integration

### Version 1.0.0 (MVP - Production Ready)
- Circuit breakers and resilience patterns
- Comprehensive test coverage (unit, integration, e2e)
- Monitoring and observability (Prometheus, Grafana)
- Complete recipe management features
- Distributed tracing
- Centralized logging

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
