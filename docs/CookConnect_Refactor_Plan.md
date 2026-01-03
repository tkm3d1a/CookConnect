# CookConnect Refactoring Plan

**Goal**: Transform academic project into a production-ready backend that supports a future frontend, while maintaining microservices architecture.

**Target Features**:
1. User account creation
2. Follow users and get notifications on their updates/recipes
3. Add recipes (anonymous or registered users)
4. Browse/search recipes (by tag, creator, ingredients, etc.)
5. Maintain saved recipe lists
6. Create "Cookbooks" from saved recipes
7. Get notifications when saved recipes are updated

**Deployment Strategy**:
- Local development: Docker Compose
- Homelab: Proxmox + Kubernetes
- Future: AWS (consideration, not immediate implementation)

---

## Phase 1: Codebase Analysis

### 1.1 Current Architecture Audit

**Action Items**:
- [ ] Document current service structure
  - List all microservices
  - Identify each service's responsibilities
  - Map service dependencies
  
- [ ] Database schema review
  - [ ] List all tables per service
  - [ ] Identify which tables support the 7 target features
  - [ ] Flag tables/schemas only used for class demos
  
- [ ] Domain model complexity assessment
  - [ ] Count entities per service
  - [ ] Count DTOs per service
  - [ ] Identify excessive mapping/conversion layers
  
- [ ] Dead code identification
  - [ ] List endpoints not needed for target features
  - [ ] Identify demo-specific features
  - [ ] Find unused dependencies

**Deliverable**: Create `CURRENT_ARCHITECTURE.md` with findings

---

## Phase 2: Target Architecture Design

### 2.1 Proposed Service Structure

**Core Services** (Evaluate against current implementation):

1. **User Service**
   - User accounts (CRUD)
   - User profiles
   - Follow/follower relationships
   - User preferences
   
2. **Recipe Service**
   - Recipe CRUD operations
   - Ingredients management
   - Tags management
   - Search/filtering logic
   - Recipe ownership (user or anonymous)
   
3. **Collection Service** OR merge into User Service
   - Saved recipes (user → recipe relationships)
   - Cookbooks (collections of saved recipes)
   - Cookbook sharing/permissions
   
4. **Notification Service**
   - Follow notifications (user posts recipe)
   - Recipe update notifications (saved recipe changed)
   - Notification preferences
   - Delivery mechanisms
   
5. **API Gateway**
   - Single entry point for frontend
   - Routing to backend services
   - Authentication/authorization
   - Rate limiting (future)

**Action Items**:
- [ ] Map current services to proposed structure
- [ ] Identify services to consolidate
- [ ] Identify services to split (if any)
- [ ] Decide: Keep Collection Service separate or merge into User Service?

### 2.2 Components to Keep

- [x] **ELK Stack** - Log aggregation (valued by developer)
- [x] **Kafka** - Event-driven notifications (fits use case)
- [x] **Spring Security + Keycloak** - Auth working, don't touch
- [x] **PostgreSQL** - Relational model supports search requirements
- [x] **Docker/K8s deployment** - Keep containerization

### 2.3 Components to Remove/Simplify

- [ ] **Eureka** → Replace with K8s service discovery (homelab) or direct URLs (local dev)
- [ ] **Resilience4j** - Circuit breakers, rate limiters (add back later if needed)
- [ ] **Distributed Tracing** (Zipkin/Sleuth) - Simplify for now
- [ ] **Gatling/Performance Tests** - Keep for CI/CD later, not active dev
- [ ] **Demo-specific features** - To be identified in Phase 1

**Action Items**:
- [ ] Create migration plan for Eureka → K8s/Docker Compose URLs
- [ ] Identify Resilience4j usage across services
- [ ] Document Zipkin dependencies to remove

---

## Phase 3: Service Discovery Strategy

### 3.1 Local Development (Docker Compose)

```yaml
# docker-compose.yml
services:
  recipe-service:
    image: cookconnect/recipe-service
    ports:
      - "8081:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=local
      - USER_SERVICE_URL=http://user-service:8080
      - NOTIFICATION_SERVICE_URL=http://notification-service:8080
      
  user-service:
    image: cookconnect/user-service
    ports:
      - "8082:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=local
```

**Configuration Strategy**:
- `application-local.yml`: Hardcoded service URLs
- `application-k8s.yml`: K8s service discovery via DNS

**Action Items**:
- [ ] Create profile-based configuration
- [ ] Update Feign clients to use profile-specific URLs
- [ ] Test local Docker Compose setup without Eureka

### 3.2 Homelab Deployment (Kubernetes)

Use native K8s service discovery:
- Services accessible via `<service-name>.<namespace>.svc.cluster.local`
- No Eureka needed

**Action Items**:
- [ ] Create K8s service manifests
- [ ] Update application-k8s.yml with K8s DNS patterns
- [ ] Document homelab deployment process

---

## Phase 4: Code Standardization

Before implementing new features, establish consistent patterns across all services.

### 4.1 Logging Standards

**Pattern**:
```
// Structured logging with key-value pairs
log.info("Recipe created", 
    kv("recipeId", recipe.getId()),
    kv("userId", userId),
    kv("action", "CREATE"),
    kv("correlationId", MDC.get("correlationId")));
```

**Action Items**:
- [ ] Create logging utility class/aspect
- [ ] Standardize log levels (DEBUG, INFO, WARN, ERROR)
- [ ] Add correlation IDs to all service calls
- [ ] Configure Logback for JSON output to ELK

### 4.2 Error Handling Standards

**Pattern**:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Log with context
        log.error("Resource not found", 
            kv("resource", ex.getResourceType()),
            kv("id", ex.getResourceId()));
        
        // Return consistent error format
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }
}
```

**Action Items**:
- [ ] Create common exception hierarchy
- [ ] Implement GlobalExceptionHandler per service
- [ ] Define standard ErrorResponse format
- [ ] Document error codes

### 4.3 Event Message Standards

**Base Event Structure**:
```java
@Data
@SuperBuilder
public abstract class DomainEvent {
    private String eventId;        // UUID
    private String eventType;      // Class simple name
    private Instant timestamp;     // When event occurred
    private String aggregateId;    // ID of the aggregate root
    private String correlationId;  // For tracing
}

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RecipeUpdatedEvent extends DomainEvent {
    private String recipeId;
    private String recipeName;
    private String updatedBy;
    private List<String> changes;  // What changed
}

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserFollowedEvent extends DomainEvent {
    private String followerId;
    private String followedUserId;
}
```

**Action Items**:
- [ ] Create common-events module/package
- [ ] Define all domain events needed for 7 features
- [ ] Update Kafka producers/consumers to use typed events
- [ ] Add Jackson serialization configuration

### 4.4 API Response Standards

**Pattern**:
```java
@Data
@Builder
public class ApiResponse<T> {
    private T data;
    private List<String> errors;
    private Map<String, Object> metadata;  // pagination, timestamps, etc.
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .data(data)
            .errors(Collections.emptyList())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String... errors) {
        return ApiResponse.<T>builder()
            .errors(Arrays.asList(errors))
            .build();
    }
}
```

**Action Items**:
- [ ] Create ApiResponse wrapper class
- [ ] Update all REST controllers to use ApiResponse
- [ ] Add pagination metadata standards
- [ ] Document API response contract

---

## Phase 5: Simplification Work

### 5.1 Domain Model Simplification

**Analysis Questions**:
- Are there multiple DTOs representing the same concept?
- Are there excessive entity ↔ DTO conversions?
- Are JPA relationships overly complex?
- Are there unused fields/entities?

**Action Items**:
- [ ] Review all entities per service
- [ ] Consolidate duplicate DTOs
- [ ] Simplify mapper classes (consider MapStruct)
- [ ] Remove unused domain objects
- [ ] Document simplified domain model

### 5.2 Database Schema Cleanup

**Action Items**:
- [ ] Identify tables only used for demos
- [ ] Create migration scripts to drop unused tables
- [ ] Optimize indexes for search queries
- [ ] Review foreign key constraints
- [ ] Document final schema per service

### 5.3 Dependency Cleanup

**Action Items**:
- [ ] Remove Eureka client dependencies
- [ ] Remove Resilience4j (or mark for future use)
- [ ] Remove Zipkin/Sleuth dependencies
- [ ] Remove Gatling test dependencies from main builds
- [ ] Update Spring Boot/Cloud versions if needed

### 5.4 Code Cleanup

**Action Items**:
- [ ] Remove demo endpoints
- [ ] Remove unused service methods
- [ ] Delete dead code (unreferenced classes)
- [ ] Consolidate duplicate utility methods
- [ ] Run linter/formatter across all services

---

## Phase 6: Feature Mapping

Map each of the 7 target features to service responsibilities.

### Feature 1: User Account Creation
**Services**: User Service  
**Endpoints**:
- POST /api/users/register
- POST /api/users/login
- GET /api/users/profile
- PUT /api/users/profile

**Action Items**:
- [ ] Verify endpoints exist
- [ ] Ensure proper validation
- [ ] Add email verification (optional)

### Feature 2: Follow Users & Notifications
**Services**: User Service, Notification Service  
**Endpoints**:
- POST /api/users/{userId}/follow
- DELETE /api/users/{userId}/unfollow
- GET /api/users/{userId}/followers
- GET /api/users/{userId}/following

**Events**:
- UserFollowedEvent → Notification Service

**Action Items**:
- [ ] Implement follow/unfollow logic
- [ ] Create UserFollowedEvent
- [ ] Notification Service subscribes to event
- [ ] Deliver notifications (push, email, in-app)

### Feature 3: Add Recipes (Anonymous or Registered)
**Services**: Recipe Service  
**Endpoints**:
- POST /api/recipes (with optional userId)
- PUT /api/recipes/{recipeId}
- DELETE /api/recipes/{recipeId}

**Action Items**:
- [ ] Support null/anonymous userId
- [ ] Ensure proper ownership validation
- [ ] Handle anonymous recipe editing (challenge)

### Feature 4: Browse/Search Recipes
**Services**: Recipe Service  
**Endpoints**:
- GET /api/recipes?tag=...&createdBy=...&ingredient=...
- GET /api/recipes/{recipeId}

**Action Items**:
- [ ] Implement search query logic
- [ ] Add pagination support
- [ ] Optimize database queries/indexes
- [ ] Consider Elasticsearch (future enhancement)

### Feature 5: Saved Recipe Lists
**Services**: Collection Service (or User Service)  
**Endpoints**:
- POST /api/users/{userId}/saved-recipes/{recipeId}
- DELETE /api/users/{userId}/saved-recipes/{recipeId}
- GET /api/users/{userId}/saved-recipes

**Action Items**:
- [ ] Decide service ownership
- [ ] Implement saved recipes relationship
- [ ] Handle recipe deletion (cascade?)

### Feature 6: Create Cookbooks
**Services**: Collection Service (or User Service)  
**Endpoints**:
- POST /api/cookbooks
- PUT /api/cookbooks/{cookbookId}
- DELETE /api/cookbooks/{cookbookId}
- POST /api/cookbooks/{cookbookId}/recipes/{recipeId}
- GET /api/cookbooks/{cookbookId}

**Action Items**:
- [ ] Design Cookbook entity/schema
- [ ] Implement cookbook CRUD
- [ ] Add recipes to cookbook
- [ ] Cookbook sharing (optional)

### Feature 7: Recipe Update Notifications
**Services**: Recipe Service, Notification Service  
**Events**:
- RecipeUpdatedEvent → Notification Service

**Action Items**:
- [ ] Publish RecipeUpdatedEvent on updates
- [ ] Notification Service queries saved recipes
- [ ] Send notifications to users with saved recipe
- [ ] Handle notification preferences

---

## Phase 7: Testing Strategy

Once simplified architecture is in place, add testing layers.

### 7.1 Unit Tests
- [ ] Core business logic per service
- [ ] Utility classes
- [ ] Domain model validation

### 7.2 Integration Tests
- [ ] Repository/database layer
- [ ] REST endpoints
- [ ] Kafka event publishing/consuming
- [ ] Spring Boot test slices

### 7.3 Contract Tests
- [ ] Spring Cloud Contract between services
- [ ] API Gateway contracts
- [ ] Event schema contracts

### 7.4 E2E Tests
- [ ] Smoke tests for critical paths
- [ ] User registration → login → create recipe flow
- [ ] Follow user → receive notification flow

**Action Items**:
- [ ] Set up testing framework per service
- [ ] Write integration tests for existing endpoints
- [ ] Add contract tests between major services
- [ ] Create basic E2E test suite

---

## Phase 8: Documentation

### 8.1 API Documentation
- [ ] OpenAPI/Swagger for all services
- [ ] API Gateway aggregated docs
- [ ] Authentication flow documentation

### 8.2 Architecture Documentation
- [ ] Service dependency diagram
- [ ] Database schema diagrams
- [ ] Event flow diagrams
- [ ] Deployment architecture

### 8.3 Developer Documentation
- [ ] Local setup guide
- [ ] Homelab deployment guide
- [ ] Contributing guidelines
- [ ] Troubleshooting common issues

---

## Execution Checklist

### Immediate Actions (Week 1-2)
- [ ] Complete Phase 1: Codebase Analysis
- [ ] Document current architecture in `CURRENT_ARCHITECTURE.md`
- [ ] Create GitHub issues/tasks for each action item

### Foundation Work (Week 3-4)
- [ ] Remove Eureka, implement profile-based service discovery
- [ ] Standardize logging across all services
- [ ] Implement GlobalExceptionHandler in each service
- [ ] Create DomainEvent base classes

### Simplification (Week 5-6)
- [ ] Clean up domain models and DTOs
- [ ] Remove dead code and demo features
- [ ] Database schema cleanup
- [ ] Dependency cleanup

### Feature Refinement (Week 7-8)
- [ ] Map and implement 7 target features
- [ ] Ensure event-driven notifications work
- [ ] Test end-to-end workflows

### Testing & Documentation (Week 9-10)
- [ ] Add integration tests
- [ ] Create API documentation
- [ ] Document architecture
- [ ] Prepare for frontend development

---

## Success Criteria

- [ ] All 7 target features implemented and working
- [ ] Clean, maintainable codebase with minimal complexity
- [ ] Consistent logging, error handling, and API responses
- [ ] Local dev with Docker Compose (no K8s required)
- [ ] Homelab deployment ready with K8s
- [ ] Test coverage for critical paths
- [ ] Documentation sufficient for future development
- [ ] Ready to build frontend service

---

## Notes & Decisions

### Decision Log
- **Service Discovery**: Profile-based (Docker Compose for local, K8s for homelab)
- **Collection Service**: [TBD - Separate or merge with User Service?]
- **Anonymous Recipes**: [TBD - How to handle ownership/editing?]
- **Notification Delivery**: [TBD - In-app only or push/email?]

### Technical Debt to Address Later
- Advanced search with Elasticsearch
- Distributed tracing (re-add Zipkin/Sleuth)
- Circuit breakers and resilience patterns
- Performance testing and optimization
- AWS migration planning

### Questions for Resolution
1. Should Collection Service be separate or part of User Service?
2. How should anonymous recipe editing work (if at all)?
3. What notification delivery mechanisms are needed?
4. Should cookbooks be shareable/collaborative?
5. What level of recipe versioning is needed for update notifications?

