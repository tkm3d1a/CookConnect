# CookConnect Canonical Model & Bounded Context Reference Guide

**Version:** 0.0.5
**Date:** October 2025

## Summary

CookConnect implements a **microservices architecture** with three bounded contexts:

1. **User Management Context** - Identity, authentication, and profiles
2. **Recipe Management Context** - Recipe content and culinary data
3. **Social Interaction Context** - Social graph and recipe curation

Each context owns its data completely, and contexts integrate through **ID-based references** with **eventual consistency** rather than hard foreign keys. This enables true service independence and horizontal scalability.

---

## Canonical Model Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         COOKCONNECT CANONICAL MODEL                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        USER MANAGEMENT CONTEXT                       │   │
│  │                                                                      │   │
│  │    ┌─────────────────┐                                               │   │
│  │    │     CCUser      │ ◄─── Aggregate Root                           │   │
│  │    ├─────────────────┤                                               │   │
│  │    │ id (PK)         │ ◄─────┐                                       │   │
│  │    │ username        │       │                                       │   │
│  │    │ email           │       │ Published to other contexts           │   │
│  │    │ password        │       │ as "userId" or "createdBy"            │   │
│  │    │ skillLevel      │       │                                       │   │
│  │    │ privateAccount  │       │                                       │   │
│  │    │ hasSocialInt..  │       │                                       │   │
│  │    └─────────────────┘       │                                       │   │
│  │            │                 │                                       │   │
│  │            │ 1:1             │                                       │   │
│  │            ▼                 │                                       │   │
│  │    ┌─────────────────┐       │                                       │   │
│  │    │   ProfileInfo   │       │                                       │   │
│  │    ├─────────────────┤       │                                       │   │
│  │    │ id (PK=User.id) │       │                                       │   │
│  │    │ firstName       │       │                                       │   │
│  │    │ lastName        │       │                                       │   │
│  │    │ birthDate       │       │                                       │   │
│  │    │ gender          │       │                                       │   │
│  │    │ pronouns        │       │                                       │   │
│  │    └─────────────────┘       │                                       │   │
│  │            │                 │                                       │   │
│  │            │ 1:N             │                                       │   │
│  │            ▼                 │                                       │   │
│  │    ┌─────────────────┐       │                                       │   │
│  │    │    Address      │       │                                       │   │
│  │    │ id (PK)         │       │                                       │   │
│  │    │ street          │       │                                       │   │
│  │    │ city            │       │                                       │   │
│  │    │ state           │       │                                       │   │
│  │    │ zipCode         │       │                                       │   │
│  │    │ country         │       │                                       │   │
│  │    └─────────────────┘       │                                       │   │
│  │                              │                                       │   │
│  └──────────────────────────────┼───────────────────────────────────────┘   │
│                                 │                                           │
│                    ╔════════════╩═════════════╗                             │
│                    ║  ID Reference (Long)     ║                             │
│                    ║  No Database FK          ║                             │
│                    ║  Eventual Consistency    ║                             │
│                    ╚════════════╦═════════════╝                             │
│                                 │                                           │
│                    ┌────────────┴──────────────┐                            │
│                    │                           │                            │
│                    ▼                           ▼                            │
│  ┌─────────────────────────────────┐  ┌───────────────────────────────┐     │
│  │        RECIPE CONTEXT           │  │        SOCIAL CONTEXT         │     │
│  │                                 │  │                               │     │
│  │                                 │  │                               │     │
│  │  ┌─────────────────┐            │  │  ┌─────────────────────────┐  │     │
│  │  │     Recipe      │ ◄── Root   │  │  │  SocialInteraction      │  │     │
│  │  ├─────────────────┤            │  │  ├─────────────────────────┤  │     │
│  │  │ id (PK)         │            │  │  │ forUserId (PK) ───────┐ │  │     │
│  │  │ title           │            │  │  │ followingIds[]        │ │  │     │
│  │  │ description     │            │  │  │ followerIds[]         │ │  │     │
│  │  │ createdBy ◄─────┼──────────┌─┘──┼──┤ bookmarkedRecipeIds[] │ │  │     │
│  │  │ visibility      │          │    │  └─────────────────────────┘  │     │
│  │  │ skillLevel      │          │    │              ┌───────────┘    │     │
│  │  └─────────────────┘          │    │              │ 1:N            │     │
│  │          │                    │    │              ▼                │     │
│  │          │ 1:1                │    │  ┌─────────────────────────┐  │     │
│  │          ▼                    │    │  │      Cookbook           │  │     │
│  │  ┌─────────────────┐          │    │  ├─────────────────────────┤  │     │
│  │  │ IngredientList  │          │    │  │ id (PK)                 │  │     │
│  │  └─────────────────┘          │    │  │ name                    │  │     │
│  │          │                    │    │  │ description             │  │     │
│  │          │ 1:N                │    │  │ visibility              │  │     │
│  │          ▼                    │    │  └─────────────────────────┘  │     │
│  │  ┌──────────────────┐         │    │              │                │     │
│  │  │IngredientListItem│         │    │              │ 1:N            │     │
│  │  ├──────────────────┤         │    │              ▼                │     │
│  │  │ quantity         │       ┌─┘    │ ┌─────────────────────────┐   │     │
│  │  │ unit             │       │      │ │    CookbookEntry        │   │     │
│  │  │ ingredientId ────┼────┐  │      │ ├─────────────────────────┤   │     │
│  │  └──────────────────┘    │  │      │ │ id (PK)                 │   │     │
│  │                          │  │      │ │ recipeId ◄──────────────┼───┼─┐   │
│  │  ┌─────────────────┐     │  │      │ └─────────────────────────┘   │ │   │
│  │  │   Ingredient    │ ◄───┘  │      │                               │ │   │
│  │  ├─────────────────┤        │      └───────────────────────────────┘ │   │
│  │  │ id (PK)         │        │                                        │   │
│  │  │ name            │        │◄───────────────────────────────────────┘   │
│  │  │ description     │        │                                            │
│  │  │ link            │        │   ╔════════════════════════════════════╗   │
│  │  └─────────────────┘        │   ║       Cross-Context Reference      ║   │
│  │                             │   ║       CookbookEntry.recipeId       ║   │
│  │  ┌─────────────────┐        │   ║        → Recipe.id (via API)       ║   │
│  │  │InstructionList  │        │   ╚════════════════════════════════════╝   │
│  │  └─────────────────┘        │                                            │
│  │          │                  │                                            │ 
│  │          │ 1:N              │                                            │
│  │          ▼                  │                                            │
│  │  ┌───────────────────┐      │                                            │
│  │  │InstructionListItem│      │                                            │
│  │  ├───────────────────┤      │                                            │
│  │  │ position          │      │                                            │
│  │  │ instructionId     │      │                                            │
│  │  └───────────────────┘      │                                            │
│  │          │                  │                                            │
│  │          ▼                  │                                            │
│  │  ┌─────────────────┐        │                                            │
│  │  │  Instruction    │        │                                            │
│  │  ├─────────────────┤        │                                            │
│  │  │ id (PK)         │        │                                            │
│  │  │ text            │        │                                            │
│  │  │ note            │        │                                            │
│  │  └─────────────────┘        │                                            │
│  │                             │                                            │
│  │  ┌─────────────────┐        │                                            │
│  │  │    TagList      │        │                                            │
│  │  └─────────────────┘        │                                            │
│  │          │                  │                                            │
│  │          │ 1:N              │                                            │
│  │          ▼                  │                                            │
│  │  ┌─────────────────┐        │                                            │
│  │  │  TagListItem    │        │                                            │
│  │  └─────────────────┘        │                                            │
│  │          │                  │                                            │
│  │          ▼                  │                                            │
│  │  ┌─────────────────┐        │                                            │
│  │  │      Tag        │        │                                            │
│  │  ├─────────────────┤        │                                            │
│  │  │ id (PK)         │        │                                            │
│  │  │ tagName         │        │                                            │
│  │  │ category        │        │                                            │
│  │  └─────────────────┘        │                                            │
│  │                             │                                            │
│  └─────────────────────────────┘                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Bounded Context Details

### 1. User Management Context

**Responsibility:** User identity, authentication, profiles, and personal information

**Aggregate Root:** `CCUser`

#### Entities

| Entity | Type | Description |
|--------|------|-------------|
| `CCUser` | Root | Core user identity with authentication credentials |
| `ProfileInfo` | Entity | Extended profile information (demographics, personal details) |
| `Address` | Entity | Physical location information |

#### Published Interface

```
CCUser.id → Referenced by other contexts as "userId" or "createdBy"
```

**Published to:**
- Recipe Service as `Recipe.createdBy`
- Social Service as `SocialInteraction.forUserId`
- Social Service as `followingIds[]` and `followerIds[]`

#### Key Domain Concepts
- User Identity (username, email, password)
- Profile Management
- Privacy Settings (`privateAccount`)
- Skill Level classification
- Social participation flag (`hasSocialInteraction`)

---

### 2. Recipe Management Context

**Responsibility:** Recipe creation, storage, ingredient management, and cooking instructions

**Aggregate Root:** `Recipe`

#### Entities

| Entity | Type | Description |
|--------|------|-------------|
| `Recipe` | Root | Recipe metadata and ownership |
| `IngredientList` | Entity | Container for recipe ingredients |
| `IngredientListItem` | Entity | Specific ingredient usage with quantity |
| `Ingredient` | Entity | Reusable ingredient definition |
| `InstructionList` | Entity | Container for cooking steps |
| `InstructionListItem` | Entity | Ordered instruction reference |
| `Instruction` | Entity | Individual cooking step |
| `TagList` | Entity | Container for recipe tags |
| `TagListItem` | Entity | Tag association |
| `Tag` | Entity | Recipe categorization |
| `RecipeNote` | Entity | User annotations on recipes |

#### Consumed Interface

```
Recipe.createdBy → CCUser.id (from User Context)
```

#### Published Interface

```
Recipe.id → Referenced by Social Context
```

**Published to:**
- Social Service as `SocialInteraction.bookmarkedRecipeIds[]`
- Social Service as `CookbookEntry.recipeId`

#### Key Domain Concepts
- Recipe (title, description, visibility)
- Ingredient with measurements
- Instructions (ordered steps)
- Tags (cuisine, meal type, dietary)
- Visibility Settings (PUBLIC, PRIVATE, FRIENDS_ONLY)
- List Management Pattern (reusable ingredients/instructions)

---

### 3. Social Interaction Context

**Responsibility:** Social graph, user relationships, recipe bookmarking, and cookbook collections

**Aggregate Root:** `SocialInteraction`

#### Entities

| Entity | Type | Description |
|--------|------|-------------|
| `SocialInteraction` | Root | User social graph and bookmarks |
| `Cookbook` | Entity | User-created recipe collection |
| `CookbookEntry` | Entity | Recipe reference within cookbook |
| `CookbookNote` | Entity | Cookbook-level annotations |
| `EntryNote` | Entity | Entry-level annotations |

#### Consumed Interface

```
SocialInteraction.forUserId → CCUser.id (from User Context)
SocialInteraction.followingIds[] → CCUser.id[]
SocialInteraction.followerIds[] → CCUser.id[]
CookbookEntry.recipeId → Recipe.id (from Recipe Context)
SocialInteraction.bookmarkedRecipeIds[] → Recipe.id[]
```

#### Key Domain Concepts
- Social Graph (followers, following)
- Bookmarking (recipe favorites)
- Cookbook Collections (organized recipe groups)
- User-to-User relationships (many-to-many)
- User-to-Recipe relationships (many-to-many)

---

## Cross-Context Integration Patterns

### Integration Strategy

**Pattern:** ID-based references with eventual consistency

**No Hard Foreign Keys** - Services maintain referential integrity through:
1. ID validation at service boundaries
2. REST API calls for cross-context data resolution
3. Eventual consistency for updates/deletes

### Data Flow Scenarios

#### Scenario 1: Creating a Recipe

```
1. User logs in → User Service authenticates
2. User creates recipe → Recipe Service
   - Recipe Service receives: userId (from auth token)
   - Stores as: Recipe.createdBy = userId
   - Does NOT validate userId existence (eventual consistency)
   - Returns: recipeId

3. Optional: User Service can query:
   GET /recipes?createdBy={userId}
```

#### Scenario 2: Bookmarking a Recipe

```
1. User finds recipe → Recipe Service
   GET /recipes/{recipeId}

2. User bookmarks → Social Service
   POST /social/{userId}/bookmark/{recipeId}
   - Social Service stores recipeId in bookmarkedRecipeIds[]
   - Does NOT validate recipeId existence (eventual consistency)

3. User views bookmarks → Social Service + Recipe Service
   GET /social/{userId}/bookmarks
   → Returns: [recipeId1, recipeId2, ...]

   For each recipeId:
   GET /recipes/{recipeId}
   → Returns: Recipe details
```

#### Scenario 3: Creating a Cookbook with Recipes

```
1. User creates cookbook → Social Service
   POST /cookbook
   {
     "name": "My Italian Recipes",
     "userId": 1
   }
   → Returns: cookbookId

2. User adds recipe to cookbook → Social Service
   POST /cookbook/{cookbookId}/entries
   {
     "recipeId": 42
   }
   - Stores CookbookEntry with recipeId reference
   - Does NOT validate recipe existence

3. User views cookbook → Social Service + Recipe Service
   GET /cookbook/{cookbookId}/entries
   → Returns: [{ entryId: 1, recipeId: 42 }, ...]

   For each recipeId:
   GET /recipes/{recipeId}
   → Returns: Recipe details with full content
```

#### Scenario 4: Following Users and Viewing Feed

```
1. User A follows User B → Social Service
   POST /social/{userIdA}/follow/{userIdB}
   - SocialInteraction for userIdA: add userIdB to followingIds[]
   - SocialInteraction for userIdB: add userIdA to followerIds[]

2. User A views feed → Social Service + Recipe Service + User Service
   GET /social/{userIdA}/following
   → Returns: [userIdB, userIdC, ...]

   For each followed user:
   GET /recipes?createdBy={followedUserId}
   → Returns: Recipe summaries

   GET /cc-user/{followedUserId}
   → Returns: User profile info
```

---

## Eventual Consistency Considerations

### Orphaned References

**Scenario:** User deletes a recipe, but bookmarks/cookbook entries remain

**Current Behavior:**
- `CookbookEntry.recipeId` points to non-existent recipe
- Social Service doesn't know recipe was deleted

**Planned Solution:**
- Domain Events (e.g., `RecipeDeletedEvent`)
- Event bus for cross-service notifications
- Social Service subscribes and cleans up references

### Stale Data

**Scenario:** User changes recipe visibility to PRIVATE, but other users still have it bookmarked

**Current Behavior:**
- Bookmarks remain valid
- Access control enforced at Recipe Service

**Resolution:**
- Recipe Service checks visibility on every GET request
- Returns 403 Forbidden if user lacks permission
- Social Service displays "Recipe unavailable" to user

---

## Aggregate Boundaries

### User Aggregate
```
CCUser (Root)
  └─ ProfileInfo
       └─ Address (multiple)
```

**Invariants:**
- Username must be unique
- Email must be unique and valid
- User must have ProfileInfo (created together)

### Recipe Aggregate
```
Recipe (Root)
  ├─ IngredientList
  │    └─ IngredientListItem (multiple)
  │         └─ Ingredient (reference)
  ├─ InstructionList
  │    └─ InstructionListItem (multiple)
  │         └─ Instruction (reference)
  ├─ TagList
  │    └─ TagListItem (multiple)
  │         └─ Tag (reference)
  └─ RecipeNote (multiple)
```

**Invariants:**
- Recipe must have title
- Recipe must have createdBy (user reference)
- IngredientList, InstructionList, TagList always exist (may be empty)

### Social Aggregate
```
SocialInteraction (Root)
  └─ Cookbook (multiple)
       └─ CookbookEntry (multiple)
            └─ EntryNote (optional)
```

**Invariants:**
- SocialInteraction.forUserId is primary key (1:1 with User)
- Cannot follow yourself
- Cookbook belongs to single user
- CookbookEntry must reference valid recipeId (validated by Recipe Service)
