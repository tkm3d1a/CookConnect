# CookConnect - Product Definition Document (PDD)

## Product Overview

**Product Name:** CookConnect  
**Version:** 0.1  
**Date:** September 2025  
**Type:** Recipe Management Platform with Social Features

### Vision Statement

CookConnect is a practical recipe management platform that enables home cooks to store, organize, and share their culinary creations while discovering new recipes from a community of cooking enthusiasts.

### Mission

To simplify recipe management for home cooks while fostering a supportive community where culinary knowledge and creativity can be shared and preserved.

## Product Goals

### Primary Goals

1. **Recipe Management**: Provide a reliable, searchable repository for personal recipe collections
2. **Knowledge Sharing**: Enable users to share successful recipes and cooking discoveries
3. **Community Building**: Create connections between users with similar cooking interests and skill levels

## Target Users

### Primary User Personas

**The Home Cook Organizer**

- Needs: Centralized recipe storage, easy search and retrieval
- Pain Points: Recipes scattered across cookbooks, websites, and handwritten notes
- Goals: Digitize and organize personal recipe collection

**The Recipe Creator**

- Needs: Platform to document and share original recipes
- Pain Points: Limited audience for recipe sharing, no feedback mechanism
- Goals: Build reputation as a skilled cook, help others succeed with their recipes

**The Culinary Explorer**

- Needs: Discover new recipes from trusted sources
- Pain Points: Overwhelming choice on recipe websites, unreliable ratings
- Goals: Find tried-and-tested recipes from real home cooks

## Core Features & Flows

### Recipe Management Service

**Must-Have Features:**

- Create, edit, and delete recipes
- Ingredient list with quantities and units
- Step-by-step cooking instructions
- Recipe categorization (cuisine type, meal type, dietary tags, etc.)
- Personal notes and modifications tracking
- Basic recipe search (by name, ingredient, category)
- Recipe visibility controls (private, public, friends-only)

**Should-Have Features:**

- Recipe duplication/forking functionality
- Bulk recipe import from common formats
- Recipe version history
- Advanced search with filters
- Recipe printing optimization

### User Management Service

**Must-Have Features:**

- User registration and authentication
- Basic profile management (name, bio, profile picture)
- Account settings and privacy controls
- Password reminder functionality

**Should-Have Features:**

- Cooking skill level and interests
- Dietary restrictions and allergies tracking
- Email notification preferences
- Password reset functionality without Admin support
- Account deletion with data cleanup

### Social Features Service

**Must-Have Features:**

- Follow/unfollow other users
- Recipe bookmarking/favorites
- Basic recipe rating (thumbs up/down)
- User activity feed showing recent public recipes from followed users
- Recipe sharing via direct links

**Should-Have Features:**

- Recipe collections/cookbooks creation
- Recipe comments and reviews
- User-to-user messaging
- Recipe recommendation engine based on preferences

### Core User Flows

**Recipe Creation Flow:**

1. User logs in and navigates to "Add Recipe"
2. Enters recipe title, description, and metadata
3. Adds ingredients with quantities
4. Enters step-by-step instructions
5. Sets visibility and tags
6. Saves recipe and receives confirmation

**Recipe Discovery Flow:**

1. User searches by ingredient or browses categories
2. Views search results with preview information
3. Selects recipe to view full details
4. Can bookmark, rate, or share recipe
5. Can follow recipe creator for future updates

**Social Interaction Flow:**

1. User discovers interesting recipe creators
2. Follows users with appealing content
3. Views personalized feed of recent recipes
4. Engages through bookmarks, ratings, and shares

### Accessibility Requirements

- WCAG 2.1 AA compliance
- Keyboard navigation support
- Screen reader compatibility
- High contrast mode support
- Mobile-responsive design

## Data Models

### Recipe Data Model

```default
Recipe:
- [PK]ID::uuid
- [UK]title::String
- [NN]description::String
- [NN]ingredients::Ingredient[]
- [NN]instructions::Instruction[]
- [NN]createdBy::uuid
- userNotes::Note[]
- [NN]createdAt::Date
- modifiedAt::Date
- [NN]visibility::VisibilitySettings
- tags::String[]

createdBy is fetched from user-service
```

```default
Ingredient:
- [PK]ID::uuid
- [UK]title::String
- [NN]qty::Float
- [NN]measurementValue::MeasurementValue
- link::String
```

```default
MeasurementValue(Enumeration):
- tsp
- tblsp
- cups
- mL
- L
- g
- kg
- lbs
```

```default
Instruction:
- [PK]ID::uuid
- [FK/*]recipeID::uuid
- [*]stepNum::String
- instructionDetail::String

* -> recipeID and stepNum are meant to combine as a unique key for this schema (No recipe should have the same instruction step number twice)
```

```default
Note:
- [PK]ID::uuid
- [FK]recipeID::uuid
- [NN]noteText::String
```

```default
VisibilitySettings:
- isVisible::Boolean

This is a object that is stored as a JSON in the db, available for scaling at a later date
```

### User Data Model

```default
User:
- [pk]ID::uuid
- [nn]username::String
- [nn]email::String
- profile::ProfileInfo
- [nn]skillLevel::Int
- restrictions::String[]
- [nn]socialID::uuid
- isPrivate::Boolean
- isClosed::Boolean
- [nn]createdAt::Date
- updatedAt::Date

socialID is used for fetching social details for this user from the social service
```

```default
ProfileInfo:
- [pk]ID::uuid
- [fk]userID::uuid
- [nn]firstName::String
- lastName::String
```

### Social Data Model

```default
Social Interactions:
- [pk]ID::uuid
- [nn]userID::uuid
- following::uuid[]
- followedBy::uuid[]
- bookmarkedRecipes::uuid[]
- cookBooks::uuid[]
```

```default
Cook Book:
- [pk]ID::uuid
- entryIDs::uuid[]
- notes::String
```

```default
Cook book entry:
- [pk]ID::uuid
- [nn]recipeID::uuid
- notes::String
```

## Non-Functional Requirements

### Scalability

- Horizontal scaling capability for all services
- Database read replicas for query optimization
- Caching strategy for frequently accessed recipes
- CDN integration for media content (future)

### Reliability

- Circuit breaker patterns for service resilience
- Graceful degradation when services are unavailable
- Automated backup and recovery procedures
- Health monitoring and alerting

### Security & Privacy

- User data privacy controls
- GDPR-compliant data handling
- Secure API endpoints with proper authentication
- Regular security audits and updates

## Out of Scope (Future Releases)

### Explicitly Not Included in V1.0

- Nutritional information calculation
- Meal planning and shopping list generation
- Recipe video/photo upload and management
- Mobile application
- Advanced recommendation algorithms
- Integration with smart kitchen devices
- Recipe cost calculation
- Inventory management
- Advanced social features (groups, events)

### Future Roadmap Considerations

- **V1.1**: Media upload and recipe photos
- **V1.2**: Meal planning service integration
- **V2.0**: Mobile app development
- **V2.1**: Nutritional analysis service
- **V3.0**: Smart kitchen device integration

---

## References

- Spring Boot Documentation: <https://spring.io/projects/spring-boot>
- Spring Cloud Documentation: <https://spring.io/projects/spring-cloud>  
- Keycloak Documentation: <https://www.keycloak.org/documentation>
- Microservices Architecture Patterns: Martin Fowler's Microservices articles and industry best practices
