# CookConnect Data Domain Documentation
**Version:** 0.0.1  
**Date:** October 2025  
**Architecture:** Microservices with Database-per-Service

## Document Purpose

This document serves as the definitive reference for the CookConnect data domain as implemented in version 0.0.1. It describes the actual data models, relationships, and schema organization across all microservices in the platform.

## Architecture Overview

### Microservices Data Strategy
- **Pattern:** Database-per-Service
- **Database Technology:** MySQL with JPA/Hibernate ORM
- **Cross-Service Communication:** ID-based references (eventual consistency)
- **Primary Key Strategy:** Auto-increment Long integers
- **Audit Strategy:** Automatic timestamp tracking where applicable

### Service Boundaries
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   User Service  │  │ Recipe Service  │  │ Social Service  │
│                 │  │                 │  │                 │
│ • User Identity │  │ • Recipe Data   │  │ • Social Graph  │
│ • Profiles      │  │ • Ingredients   │  │ • Cookbooks     │
│ • Addresses     │  │ • Instructions  │  │ • Bookmarks     │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## User Service Data Domain

### Core Entities

#### CCUser Entity
**Purpose:** Central user identity and authentication
**Location:** `com.tkforgeworks.cookconnect.userservice.model.CCUser`

```java
@Entity CCUser {
    Long id                     // Primary key
    String username             // Unique identifier
    String password             // Authentication credential
    String email                // Contact and recovery
    boolean hasSocialInteraction // Social feature participation flag
    boolean isPrivate           // Profile visibility control
    boolean isClosed            // Account status control
    LocalDateTime createdAt     // Account creation timestamp
    LocalDateTime updatedAt     // Last modification timestamp
    SkillLevel skillLevel       // Cooking proficiency level
    ProfileInfo profileInfo     // One-to-one relationship
}
```

**Key Relationships:**
- `CCUser (1:1) ProfileInfo` - Extended profile information

#### ProfileInfo Entity
**Purpose:** Extended user profile and personal information
**Location:** `com.tkforgeworks.cookconnect.userservice.model.ProfileInfo`

```java
@Entity ProfileInfo {
    Long id                     // Shared primary key with CCUser
    CCUser user                 // Back-reference to user
    String firstName            // User's first name
    String lastName             // User's last name
    Date birthDate              // Date of birth
    int age                     // Calculated or stored age
    Gender gender               // Gender identity (optional)
    Pronouns pronouns           // Preferred pronouns (optional)
    Set<Address> addresses      // Multiple address support
    LocalDateTime createdAt     // Profile creation timestamp
    LocalDateTime updatedAt     // Last modification timestamp
}
```

**Key Relationships:**
- `ProfileInfo (1:*) Address` - Multiple addresses per user

#### Address Entity
**Purpose:** User location information
**Location:** `com.tkforgeworks.cookconnect.userservice.model.Address`

```java
@Entity Address {
    Long id                     // Primary key
    ProfileInfo profileInfo     // Parent profile reference
    String street               // Street address
    String apartmentNumber      // Unit/apartment (optional)
    String city                 // City name
    String zipCode              // Postal code
    State state                 // State/province enum
    Country country             // Country enum
}
```

### User Service Enums

#### SkillLevel
**Values:** `PROFESSIONAL`, `LINE_COOK`, `HOME_COOK`, `COLLEGE_STUDENT`, `AMATEUR`
**Usage:** User cooking proficiency classification

#### Gender
**Purpose:** Gender identity options for inclusive profiles

#### Pronouns
**Purpose:** Preferred pronoun specification for respectful communication

#### State & Country
**Purpose:** Geographic location classification

---

## Recipe Service Data Domain

### Core Entities

#### Recipe Entity
**Purpose:** Central recipe definition and metadata
**Location:** `com.tkforgeworks.cookconnect.recipeservice.model.Recipe`

```java
@Entity Recipe {
    Long id                         // Primary key
    String title                    // Recipe name (required)
    String description              // Recipe overview
    Long createdBy                  // User ID from User Service
    LocalDateTime createdAt         // Creation timestamp
    LocalDateTime updatedAt         // Modification timestamp
    VisibilitySettings recipeVisibilitySettings // Privacy control
    SkillLevel skillLevel           // Required cooking skill
    IngredientList ingredientList   // One-to-one relationship
    InstructionList instructionList // One-to-one relationship
    TagList tagList                 // One-to-one relationship
    Set<RecipeNote> recipeNotes     // User annotations
}
```

**Key Relationships:**
- `Recipe (1:1) IngredientList` - Recipe ingredients
- `Recipe (1:1) InstructionList` - Cooking steps
- `Recipe (1:1) TagList` - Recipe categorization
- `Recipe (1:*) RecipeNote` - User annotations

#### Ingredient Entity
**Purpose:** Reusable ingredient definitions
**Location:** `com.tkforgeworks.cookconnect.recipeservice.model.Ingredient`

```java
@Entity Ingredient {
    Long id                     // Primary key
    String name                 // Ingredient name (required)
    String description          // Ingredient details (optional)
    String link                 // Reference URL (optional)
    LocalDateTime createdAt     // Creation timestamp
    LocalDateTime updatedAt     // Modification timestamp
}
```

**Design Note:** Ingredients are normalized entities that can be reused across multiple recipes

#### List Management Pattern

The Recipe Service implements a sophisticated list management pattern for complex collections:

```
Recipe ──(1:1)──→ IngredientList ──(1:*)──→ IngredientListItem ──(*:1)──→ Ingredient
       ──(1:1)──→ InstructionList ──(1:*)──→ InstructionListItem ──(*:1)──→ Instruction  
       ──(1:1)──→ TagList ──────────(1:*)──→ TagListItem ──────────(*:1)──→ Tag
```

#### IngredientListItem Entity
**Purpose:** Ingredient usage within a specific recipe
**Location:** `com.tkforgeworks.cookconnect.recipeservice.model.IngredientListItem`

```java
@Entity IngredientListItem {
    Long id                         // Primary key
    IngredientList ingredientList   // Parent list reference
    int quantity                    // Amount needed
    MeasurementValue measurementValue // Unit of measurement
    Ingredient ingredient           // Reference to ingredient entity
}
```

#### Instruction Entity
**Purpose:** Individual cooking step
**Location:** `com.tkforgeworks.cookconnect.recipeservice.model.Instruction`

```java
@Entity Instruction {
    Long id                     // Primary key
    String text                 // Step instruction (required)
    String note                 // Additional notes (optional)
}
```

#### InstructionListItem Entity
**Purpose:** Ordered instruction within a recipe
**Location:** `com.tkforgeworks.cookconnect.recipeservice.model.InstructionListItem`

```java
@Entity InstructionListItem {
    Long id                         // Primary key
    InstructionList instructionList // Parent list reference
    int position                    // Step order
    Instruction instruction         // Reference to instruction entity
}
```

### Recipe Service Enums

#### MeasurementValue
**Values:** `CUP`, `GALLON`, `TSP`, `TBLSP`, `G`, `KG`, `ML`, `L`, `CLOVE`, `WHOLE`, `HALF`, `PINCH`, `TO_TASTE`, `HANDFUL`
**Usage:** Standardized cooking measurements

#### VisibilitySettings
**Values:** `PUBLIC`, `PRIVATE`, `FRIENDS_ONLY`
**Usage:** Recipe privacy control

#### SkillLevel
**Values:** Mirrors User Service enum for consistency
**Usage:** Required cooking skill for recipe

#### TagCategory
**Purpose:** Recipe categorization system (cuisine, meal type, dietary restrictions)

---

## Social Service Data Domain

### Core Entities

#### SocialInteraction Entity
**Purpose:** User social graph and bookmarking
**Location:** `com.tkforgeworks.cookconnect.socialservice.model.SocialInteraction`

```java
@Entity SocialInteraction {
    Long forUserId              // Primary key and user reference
    Set<Long> followingIds      // Users this user follows
    Set<Long> followerIds       // Users following this user
    Set<Long> bookmarkedRecipeIds // Bookmarked recipe references
    Set<Cookbook> cookbooks     // User's recipe collections
}
```

**Design Note:** Uses `forUserId` as primary key to establish clear ownership semantics

#### Cookbook Entity
**Purpose:** User-created recipe collections
**Location:** `com.tkforgeworks.cookconnect.socialservice.model.Cookbook`

```java
@Entity Cookbook {
    Long id                         // Primary key
    String name                     // Cookbook title
    String description              // Cookbook overview
    LocalDateTime createdAt         // Creation timestamp
    LocalDateTime updatedAt         // Modification timestamp
    CookBookNote note               // One-to-one relationship
    Set<CookBookEntry> cookBookEntries // Recipe collection
}
```

#### CookBookEntry Entity
**Purpose:** Recipe reference within a cookbook
**Location:** `com.tkforgeworks.cookconnect.socialservice.model.CookBookEntry`

```java
@Entity CookBookEntry {
    Long id                     // Primary key
    Long recipeId               // Recipe Service reference
    EntryNote entryNote         // Optional annotation
}
```

#### Note Entities
**Purpose:** Rich annotation system

```java
@Entity CookBookNote {
    Long id                     // Primary key
    String content              // Note content
    // Additional metadata fields
}

@Entity EntryNote {
    Long id                     // Primary key  
    String content              // Note content
    // Additional metadata fields
}
```

---

## Cross-Service Data Integration

### Reference Strategy
```
User Service    ←── createdBy ──────┐
    ↓                               │
    userId ──→ SocialInteraction    │
                    ↓               │
              recipeId ─────────────┼──→ Recipe Service
                                   │
                bookmarkedRecipeIds ┘
```

### Data Consistency Patterns

#### Eventual Consistency
- Cross-service references use ID-based lookups
- Services maintain their own data integrity
- Referential integrity across services is eventually consistent

#### Service Communication
- Services communicate via REST APIs (planned)
- No direct database connections between services
- Shared data accessed through service contracts

## Data Constraints and Validation

### Primary Key Strategy
- All entities use auto-increment Long integers
- Consistent across all services for simplicity
- Foreign key relationships use Long references

### Required Fields
- Marked with JPA `@Column(nullable = false)`
- Business-critical fields are non-nullable
- Optional fields allow null values for flexibility

### Audit Trail
- `@CreationTimestamp` and `@UpdateTimestamp` where applicable
- Automatic timestamp management via Hibernate
- Consistent audit pattern across entities

## Future Considerations

### Scalability Patterns
- Current design supports horizontal scaling per service
- ID-based references enable service independence
- List management pattern supports complex query requirements

### Extension Points
- Enum expansions planned for measurement values and categories
- Note entities designed for rich content (formatting, attachments)
- Address model supports international expansion

### Data Migration Strategy
- Current Long IDs can be migrated to UUIDs if needed
- List pattern supports additional metadata without schema changes
- Service boundaries enable independent evolution

---

## Summary

The CookConnect v0.0.1 data domain implements a robust microservices architecture with clear service boundaries and well-defined data relationships. The design balances immediate implementation needs with future scalability requirements, providing a solid foundation for the recipe management platform while maintaining the flexibility to evolve as requirements grow.

Key strengths of the current design:
- **Clear Domain Separation**: Each service owns its data completely
- **Flexible Relationships**: List management pattern enables complex data modeling
- **Extensible Design**: Enum-driven configuration supports easy feature additions
- **Audit Capability**: Comprehensive timestamp tracking for debugging and analytics
- **User Privacy**: Multiple levels of privacy control across all domains

This data domain documentation should be updated with each significant schema change to maintain accuracy as the platform evolves.