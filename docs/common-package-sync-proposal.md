# Common Package Synchronization & Version Merge Validation Proposal

## Executive Summary

This proposal addresses two critical needs for the CookConnect microservices project:

1. **Version Merge Validation**: Ensure all services and documentation are updated before merging version branches (e.g., `0.1`) to `main`
2. **Common Package Consistency**: Ensure Java classes in the `common` packages across services remain synchronized

## Current State Analysis

### Services Architecture

The CookConnect platform consists of **5 microservices**:

- **Business Services**: `user-service`, `recipe-service`, `social-service`
- **Infrastructure Services**: `config-server`, `eureka-server`

### Common Package Structure (from `0.1` branch)

Each business service contains a `common` package with shared authentication, authorization, and Feign client interceptor code:

**Location**: `services/{service}/src/main/java/com/tkforgeworks/cookconnect/{service}service/common/`

#### Files Present in All Three Services (6 files):

1. **`FeignInterceptor.java`** - Intercepts Feign client requests to add UserContext headers
2. **`IncomingRequestFilter.java`** - Servlet filter to capture incoming request headers
3. **`JwtAuthConverter.java`** - JWT authentication converter for Spring Security
4. **`UserContext.java`** - Thread-safe context holder for user information (correlation ID, auth token, user ID, social ID, username)
5. **`UserContextFilter.java`** - Filter to populate UserContext from request headers
6. **`UserContextHolder.java`** - ThreadLocal holder for UserContext

#### Files Unique to Specific Services:

- **`social-service` only**:
  - `AuthorizationHelper.java` - Service-specific authorization logic for social interactions

### Current Drift Problem

Analysis of the `0.1` branch reveals **existing inconsistencies** in the `common` package files that should be identical:

#### **Issue 1: UserContext.java Drift**

**File**: `UserContext.java`

**Inconsistency**:
- **user-service & recipe-service**: Define `AUTHENTICATION_HEADER` constant
- **social-service**: Has `AUTHENTICATION_HEADER` commented out

```java
// user-service and recipe-service (line 8):
public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";

// social-service (line 8):
//    public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";
```

**Impact**: Code inconsistency that could lead to bugs if social-service later needs this header.

#### **Issue 2: FeignInterceptor.java Drift**

**File**: `FeignInterceptor.java`

**Inconsistency**:
- **user-service**: Contains Keycloak endpoint detection logic (lines 11-18) to skip UserContext headers for token/admin endpoints
- **recipe-service & social-service**: Missing this Keycloak-specific logic

```java
// user-service only:
if (url.contains("/realms/") && url.contains("/protocol/openid-connect/token")) {
    log.debug("Skipping UserContext headers for Keycloak token endpoint");
    return;
}
if (url.contains("/admin/realms/")) {
    log.debug("Skipping UserContext headers for Keycloak admin endpoint");
    return;
}
```

**Impact**: recipe-service and social-service may incorrectly add UserContext headers to Keycloak requests.

### Why This Matters

These shared classes form the **distributed tracing and security infrastructure** for the entire platform. Inconsistencies can lead to:

- **Authentication failures** across services
- **Lost correlation IDs** in distributed traces
- **Debugging nightmares** when behavior differs between services
- **Copy-paste errors** when updating one service but forgetting others

---

## Proposed Solutions

### Solution 1: Version Merge Validation Workflow

**Objective**: Ensure completeness before merging version branches to `main`

#### New GitHub Actions Workflow: `version-merge-validation.yml`

**Triggers**: Pull requests from version branches (e.g., `0.1`, `0.2`, `1.0`) to `main`

**Validation Checks**:

1. **All Services Updated Check**
   - Detect which services changed between `main` and version branch
   - Verify all changed services have incremented versions
   - Verify all infrastructure services (config-server, eureka-server) are versioned
   - **Failure Condition**: Any service with code changes lacks version increment

2. **README Updated Check**
   - Compare `README.md` between `main` and version branch
   - Verify specific sections are updated:
     - "Current Status" section (version number)
     - "Features" section (if new features added)
     - "API Endpoints" section (if endpoints changed)
   - **Failure Condition**: README unchanged OR version number not updated

3. **Comprehensive Version Report**
   - Generate markdown report showing:
     - Current `main` versions vs. version branch versions
     - Which services changed
     - README diff summary
   - Post as PR comment for visibility

**Implementation Details**:

```yaml
name: Version Merge Validation

on:
  pull_request:
    branches:
      - main
    types: [opened, synchronize, reopened]

jobs:
  validate-version-merge:
    # Only run when PR is from a version branch (e.g., 0.1, 1.0, 1.2)
    if: |
      github.event.pull_request.head.ref != 'main' &&
      github.event.pull_request.head.ref matches pattern: ^\d+\.\d+$

    steps:
      - Detect version branch pattern
      - Compare all service versions (main vs PR)
      - Check README.md changes
      - Validate all changed services have version increments
      - Generate comprehensive report
      - Post PR comment with results
      - Fail if validation errors
```

**Benefits**:
- Prevents incomplete version merges to `main`
- Ensures documentation stays synchronized with code
- Provides clear feedback before merge
- Maintains clean version history

---

### Solution 2: Common Package Consistency Check

**Objective**: Ensure shared classes remain identical across services

#### Approach A: Automated Validation Script (Recommended)

**New Script**: `.github/scripts/validate-common-packages.sh`

**Functionality**:
1. Discover all Java files in `common` packages across all three business services
2. Build a matrix of which files exist in which services
3. For files that exist in multiple services, normalize each version by:
   - Removing package declarations (e.g., `package com.tkforgeworks.cookconnect.userservice.common;`)
   - Normalizing service-specific class references (e.g., `userservice.common` → `SERVICE.common`)
   - Removing comments and whitespace variations
4. Compare normalized versions using SHA-256 checksums
5. Report inconsistencies with detailed diffs showing exact differences
6. Exit with error code if mismatches found

**Example Output** (based on actual drift found in `0.1` branch):
```
🔍 Validating Common Package Consistency...

Checking common/UserContext.java:
  ✅ user-service     [SHA: a1b2c3d4...]
  ✅ recipe-service   [SHA: a1b2c3d4...]
  ❌ social-service   [SHA: 9f8e7d6c...]  MISMATCH!

Checking common/FeignInterceptor.java:
  ✅ user-service     [SHA: 5e4f3a2b...]
  ❌ recipe-service   [SHA: 1a2b3c4d...]  MISMATCH!
  ❌ social-service   [SHA: 1a2b3c4d...]  MISMATCH!

Checking common/UserContextHolder.java:
  ✅ user-service     [SHA: 7f6e5d4c...]
  ✅ recipe-service   [SHA: 7f6e5d4c...]
  ✅ social-service   [SHA: 7f6e5d4c...]

Skipping common/AuthorizationHelper.java:
  ℹ️  Only exists in: social-service (service-specific implementation)

❌ 2 files have inconsistencies across services!

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
DIFF #1: common/UserContext.java
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Canonical version: user-service, recipe-service
Divergent version: social-service

--- canonical (user-service, recipe-service)
+++ divergent (social-service)
@@ -6,7 +6,7 @@
 @Setter
 public class UserContext {
     public static final String AUTHORIZATION_HEADER = "Authorization";
-    public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";
+//    public static final String AUTHENTICATION_HEADER = "X-Authentication-Token";
     public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
DIFF #2: common/FeignInterceptor.java
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Canonical version: user-service
Divergent version: recipe-service, social-service

--- canonical (user-service)
+++ divergent (recipe-service, social-service)
@@ -8,14 +8,6 @@
 public class FeignInterceptor implements RequestInterceptor {
     @Override
     public void apply(RequestTemplate requestTemplate) {
-        String url = requestTemplate.url();
-        if (url.contains("/realms/") && url.contains("/protocol/openid-connect/token")) {
-            log.debug("Skipping UserContext headers for Keycloak token endpoint");
-            return;
-        }
-        if (url.contains("/admin/realms/")) {
-            log.debug("Skipping UserContext headers for Keycloak admin endpoint");
-            return;
-        }
         log.debug("****FeignInterceptor intercept start****");

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 To fix these issues:
1. Decide which version should be canonical for each file
2. Copy the canonical version to all services
3. Update only the package declaration line
4. Re-run validation to confirm consistency
```

**Integration with GitHub Actions**:

Add to existing `version-validation.yml` (for feature PRs) and new `version-merge-validation.yml` (for version merges):

```yaml
  validate-common-packages:
    name: Validate Common Package Consistency
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Run common package validation
        run: |
          chmod +x .github/scripts/validate-common-packages.sh
          .github/scripts/validate-common-packages.sh

      - name: Post results to PR
        if: failure()
        uses: actions/github-script@v7
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: '❌ Common package consistency check failed. Please ensure all shared classes in the `common` package are identical across services. Check the workflow logs for detailed diffs.'
            });
```

#### Approach B: Centralized Common Library (Future Enhancement)

**Long-term recommendation**: Extract common packages into a shared Maven module

**Structure**:
```
services/
  common-lib/           # New shared module
    src/main/java/com/tkforgeworks/cookconnect/common/
      FeignInterceptor.java
      IncomingRequestFilter.java
      JwtAuthConverter.java
      UserContext.java
      UserContextFilter.java
      UserContextHolder.java
    pom.xml
  user-service/
    pom.xml             # Add dependency on common-lib
  recipe-service/
    pom.xml             # Add dependency on common-lib
  social-service/
    src/main/java/.../socialservice/common/
      AuthorizationHelper.java    # Keep service-specific class
    pom.xml             # Add dependency on common-lib
```

**Benefits**:
- Single source of truth
- No synchronization needed
- Easier maintenance
- Shared versioning

**Migration Effort**: Medium (requires refactoring, testing, documentation)

---

## Implementation Plan

### Phase 1: Immediate Implementation (Recommended for Now)

**Week 1: Common Package Validation**
1. Create `validate-common-packages.sh` script
2. Add to existing `version-validation.yml` workflow
3. Test with intentional mismatches
4. Document usage in README

**Week 2: Version Merge Validation**
1. Create `version-merge-validation.yml` workflow
2. Implement service version detection
3. Implement README change detection
4. Add comprehensive reporting
5. Test with mock version branch

**Week 3: Documentation & Rollout**
1. Update README with new validation processes
2. Create `.github/BRANCHING_STRATEGY.md` guide
3. Create developer guide for common package updates
4. Train team on new workflows

### Phase 2: Future Enhancement (Optional)

**Month 2-3: Centralized Common Library**
1. Design shared module structure
2. Extract common package (6 shared classes)
3. Keep service-specific classes in individual services (e.g., AuthorizationHelper)
4. Update all service dependencies
5. Comprehensive integration testing
6. Update CI/CD for multi-module build

---

## Technical Specifications

### Common Package Detection Logic

**Package to Monitor**:
```bash
# The common package in each service
COMMON_PACKAGE="common"

# Services to check
SERVICES=(
  "user-service"
  "recipe-service"
  "social-service"
)

# Build file paths
for service in "${SERVICES[@]}"; do
  COMMON_DIR="services/${service}/src/main/java/com/tkforgeworks/cookconnect/${service//-/}service/common/"
  # Find all .java files in this directory
done
```

**Normalization Process**:
```bash
normalize_file() {
  local file=$1

  cat "$file" | \
    # Remove package declaration
    sed '/^package /d' | \
    # Normalize service-specific imports (userservice/recipeservice/socialservice → SERVICE)
    sed 's/com\.tkforgeworks\.cookconnect\.[a-z]*service\./com.tkforgeworks.cookconnect.SERVICE./g' | \
    # Remove single-line comments
    sed '/^[[:space:]]*\/\//d' | \
    # Remove multi-line comments (/* ... */)
    sed '/^[[:space:]]*\/\*/,/\*\//d' | \
    # Remove trailing whitespace
    sed 's/[[:space:]]*$//' | \
    # Remove empty lines
    sed '/^$/d'
}
```

**Important Note on Normalization**:
The normalization process removes comments because the validator compares **functional equivalence**, not stylistic choices. However, this means that a commented-out line (like `// public static final String AUTHENTICATION_HEADER`) will be treated differently than an active line. This is intentional - commenting out code is a functional change, not just a style difference.

**Checksum Comparison**:
```bash
# Generate checksum for normalized file
checksum=$(normalize_file "$file" | sha256sum | awk '{print $1}')
```

### Version Branch Detection

**Pattern Matching**:
```yaml
# Detect version branch (e.g., 0.1, 1.0, 2.3)
if [[ "${BRANCH_NAME}" =~ ^[0-9]+\.[0-9]+$ ]]; then
  IS_VERSION_BRANCH=true
fi
```

### README Section Detection

**Target Sections**:
```bash
# Extract specific README sections
get_readme_section() {
  local section_name=$1
  awk "/^## ${section_name}/,/^## /" README.md
}

# Check if Current Status updated
MAIN_STATUS=$(git show origin/main:README.md | get_readme_section "Current Status")
PR_STATUS=$(get_readme_section "Current Status")

if [ "$MAIN_STATUS" == "$PR_STATUS" ]; then
  echo "❌ README 'Current Status' section unchanged"
  exit 1
fi
```

---

## Validation Rules

### Rule 1: Service Version Consistency
- **Trigger**: PR from version branch to `main`
- **Check**: All services with code changes have incremented versions
- **Severity**: BLOCKING (PR cannot merge)

### Rule 2: README Documentation
- **Trigger**: PR from version branch to `main`
- **Check**: README.md modified AND "Current Status" version updated
- **Severity**: BLOCKING (PR cannot merge)

### Rule 3: Common Package Synchronization
- **Trigger**: Any PR with changes to common packages
- **Check**: Classes with same name in different services are identical (after normalization)
- **Severity**: BLOCKING (PR cannot merge)

---

## Rollback & Emergency Procedures

### Disabling Validations

If validations block critical hotfixes, add bypass mechanism:

**Option 1: Label-based bypass**
```yaml
# In workflow
if: |
  !contains(github.event.pull_request.labels.*.name, 'skip-validation')
```

**Option 2: Admin override**
- Repository settings → Branch protection → Allow administrators to bypass

### Emergency Common Package Fix

If services diverge and block all PRs:

1. Create emergency branch
2. Run validation script locally to identify diffs
3. Choose canonical version (usually most recent)
4. Update all services to canonical version
5. Create emergency PR with `[skip-validation]` label
6. Merge and remove label

---

## Metrics & Monitoring

### Success Metrics

1. **Version Merge Accuracy**: 100% of version merges include all service updates
2. **Documentation Freshness**: README version matches latest release
3. **Common Package Drift**: Zero inconsistencies in production
4. **Developer Friction**: < 5% of PRs require common package fixes

### Monitoring Dashboard (Future)

Track via GitHub Actions:
- Common package validation failures over time
- Most frequently divergent classes
- Average time to fix common package issues

---

## FAQ

### Q1: What if a service doesn't need all common classes?

**A**: The validation only checks classes that exist in multiple services. For example, `social-service` has `AuthorizationHelper.java` but the other services don't - this is fine. The validator skips it since it only exists in one service. Only when multiple services have the same filename (e.g., `UserContext.java`) are they compared.

### Q2: Can I intentionally make a common class different in one service?

**A**: Yes, but rename it to signal the difference (e.g., `SocialUserContext.java` instead of `UserContext.java`). The validator only compares files with identical names. If you need service-specific behavior, create a new class with a unique name.

### Q3: What about auto-formatting differences?

**A**: The normalization process removes whitespace and formatting differences before comparison. Only functional code differences trigger failures.

**Important**: Commented-out code IS considered a functional difference. For example:
- `public static final String FOO = "bar";` ≠ `// public static final String FOO = "bar";`

This is intentional - commenting out code changes functionality, not just style.

### Q4: How do I update a common class across all services?

**A**:
1. Update in one service first
2. Validation will fail showing diffs
3. Copy changes to other services in same PR
4. Validation passes when all match

### Q5: Can I merge to main without updating all services?

**A**: No. The version merge validation requires all changed services to have updated versions. This ensures `main` always represents a complete, versioned release.

---

## Appendix A: File Structure Reference

### Common Package Locations

```
services/
├── user-service/src/main/java/com/tkforgeworks/cookconnect/userservice/
│   └── common/
│       ├── FeignInterceptor.java
│       ├── IncomingRequestFilter.java
│       ├── JwtAuthConverter.java
│       ├── UserContext.java
│       ├── UserContextFilter.java
│       └── UserContextHolder.java
│
├── recipe-service/src/main/java/com/tkforgeworks/cookconnect/recipeservice/
│   └── common/
│       ├── FeignInterceptor.java
│       ├── IncomingRequestFilter.java
│       ├── JwtAuthConverter.java
│       ├── UserContext.java
│       ├── UserContextFilter.java
│       └── UserContextHolder.java
│
└── social-service/src/main/java/com/tkforgeworks/cookconnect/socialservice/
    └── common/
        ├── AuthorizationHelper.java        ← Service-specific (OK to be unique)
        ├── FeignInterceptor.java
        ├── IncomingRequestFilter.java
        ├── JwtAuthConverter.java
        ├── UserContext.java
        ├── UserContextFilter.java
        └── UserContextHolder.java
```

**Key Point**: The 6 files present in all services must be identical (after package name normalization).
The `AuthorizationHelper.java` file is unique to social-service and will be ignored by validation.

### GitHub Actions Workflow Files

```
.github/
├── workflows/
│   ├── version-validation.yml              # Existing (for feature PRs)
│   └── version-merge-validation.yml        # NEW (for version branch merges)
└── scripts/
    ├── validate-semver.sh                  # Existing
    └── validate-common-packages.sh         # NEW
```

---

## Appendix B: Example PR Comment

**Version Merge Validation Report**

```markdown
## 🔍 Version Merge Validation Report

**Branch**: `0.1` → `main`

### ✅ Service Version Status

| Service | Main Version | Branch Version | Status |
|---------|-------------|----------------|---------|
| user-service | 0.0.5 | 0.1.0 | ✅ Updated |
| recipe-service | 0.0.5 | 0.1.0 | ✅ Updated |
| social-service | 0.0.5 | 0.1.0 | ✅ Updated |
| config-server | 0.0.1 | 0.1.0 | ✅ Updated |
| eureka-server | 0.0.1 | 0.1.0 | ✅ Updated |

### ✅ README Documentation

- ✅ README.md modified
- ✅ Current Status section updated to version 0.1.0
- ✅ Features section includes new additions

### ✅ Common Package Consistency

All shared classes validated:
- ✅ common/FeignInterceptor.java (3 services)
- ✅ common/IncomingRequestFilter.java (3 services)
- ✅ common/JwtAuthConverter.java (3 services)
- ✅ common/UserContext.java (3 services)
- ✅ common/UserContextFilter.java (3 services)
- ✅ common/UserContextHolder.java (3 services)
- ℹ️  common/AuthorizationHelper.java (social-service only - skipped)

---

**✅ All checks passed! This version branch is ready to merge to main.**
```

---

## Conclusion

This proposal provides a comprehensive solution for:

1. **Preventing incomplete releases**: Version merge validation ensures all services and documentation are updated before merging to `main`
2. **Maintaining code consistency**: Common package validation prevents drift in shared classes
3. **Developer confidence**: Automated checks catch issues early in the PR process
4. **Clear feedback**: Detailed reports help developers understand and fix issues quickly

**Recommended Action**: Implement Phase 1 (validation scripts and workflows) immediately. Evaluate Phase 2 (centralized common library) after 1-2 months of usage data.

**Estimated Implementation Time**:
- Phase 1: 1-2 weeks (including testing)
- Phase 2: 4-6 weeks (if pursued)

**Risk Level**: Low - Both solutions are additive (don't modify existing code) and can be disabled if issues arise.
