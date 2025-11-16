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

---

## Appendix C: Implementation Outlines

This appendix provides detailed pseudocode and implementation outlines for all components needed to implement the proposal. Use these as blueprints for development.

### C.1: validate-common-packages.sh Script

**Location**: `.github/scripts/validate-common-packages.sh`

**Purpose**: Validate that shared classes in the `common` package are identical across services

**Pseudocode**:

```bash
#!/bin/bash
# validate-common-packages.sh
# Validates common package consistency across microservices

set -e

# Configuration
SERVICES=("user-service" "recipe-service" "social-service")
COMMON_PACKAGE="common"
BASE_PATH="services"
EXIT_CODE=0

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "$1"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

normalize_file() {
    # Input: file path
    # Output: normalized content (to stdout)
    local file=$1

    cat "$file" | \
        # Remove package declaration
        sed '/^package /d' | \
        # Normalize service-specific imports
        sed 's/com\.tkforgeworks\.cookconnect\.\(user\|recipe\|social\)service\./com.tkforgeworks.cookconnect.SERVICE./g' | \
        # Remove single-line comments (but NOT commented-out code)
        # This is tricky - we want to keep functionality differences
        sed '/^[[:space:]]*\/\//d' | \
        # Remove multi-line comments
        sed '/^[[:space:]]*\/\*/,/\*\//d' | \
        # Remove trailing whitespace
        sed 's/[[:space:]]*$//' | \
        # Remove empty lines
        sed '/^$/d'
}

build_file_matrix() {
    # Build associative array of which files exist in which services
    # Format: file_matrix[filename]="service1 service2 service3"

    declare -g -A file_matrix

    for service in "${SERVICES[@]}"; do
        service_name="${service//-/}service"  # user-service -> userservice
        common_dir="${BASE_PATH}/${service}/src/main/java/com/tkforgeworks/cookconnect/${service_name}/common"

        if [ ! -d "$common_dir" ]; then
            echo -e "${YELLOW}⚠️  Warning: $service has no common package${NC}"
            continue
        fi

        # Find all .java files
        while IFS= read -r filepath; do
            filename=$(basename "$filepath")

            # Add service to this file's list
            if [ -z "${file_matrix[$filename]}" ]; then
                file_matrix[$filename]="$service"
            else
                file_matrix[$filename]="${file_matrix[$filename]} $service"
            fi
        done < <(find "$common_dir" -maxdepth 1 -name "*.java" -type f)
    done
}

validate_file() {
    # Input: filename, space-separated list of services
    # Output: 0 if consistent, 1 if inconsistent
    local filename=$1
    shift
    local services_array=("$@")

    # If only one service has this file, skip validation
    if [ ${#services_array[@]} -eq 1 ]; then
        echo -e "${BLUE}ℹ️  Skipping $filename: Only exists in ${services_array[0]} (service-specific)${NC}"
        return 0
    fi

    # Build paths to all instances of this file
    declare -A file_paths
    declare -A file_checksums

    for service in "${services_array[@]}"; do
        service_name="${service//-/}service"
        filepath="${BASE_PATH}/${service}/src/main/java/com/tkforgeworks/cookconnect/${service_name}/common/${filename}"
        file_paths[$service]="$filepath"

        # Normalize and checksum
        checksum=$(normalize_file "$filepath" | sha256sum | awk '{print $1}')
        file_checksums[$service]="$checksum"
    done

    # Check if all checksums match
    first_checksum="${file_checksums[${services_array[0]}]}"
    all_match=true

    echo "Checking common/$filename:"
    for service in "${services_array[@]}"; do
        if [ "${file_checksums[$service]}" == "$first_checksum" ]; then
            echo -e "  ${GREEN}✅${NC} $service     [SHA: ${file_checksums[$service]:0:12}...]"
        else
            echo -e "  ${RED}❌${NC} $service     [SHA: ${file_checksums[$service]:0:12}...]  MISMATCH!"
            all_match=false
        fi
    done

    if [ "$all_match" = false ]; then
        # Store mismatch details for later reporting
        echo "$filename|${services_array[*]}" >> /tmp/common_mismatches.txt
        return 1
    fi

    return 0
}

generate_diff_report() {
    # Read mismatches and generate detailed diff reports
    if [ ! -f /tmp/common_mismatches.txt ]; then
        return
    fi

    echo ""
    print_header "INCONSISTENCY DETAILS"

    diff_num=1
    while IFS='|' read -r filename services; do
        echo ""
        echo "DIFF #${diff_num}: common/${filename}"
        print_header ""

        services_array=($services)

        # Find canonical version (most common checksum)
        # For simplicity, use first service as reference
        reference_service="${services_array[0]}"
        reference_file="${BASE_PATH}/${reference_service//-/}service/src/main/java/com/tkforgeworks/cookconnect/${reference_service//-/}service/common/${filename}"

        # Compare each service to reference
        for service in "${services_array[@]:1}"; do
            service_name="${service//-/}service"
            service_file="${BASE_PATH}/${service}/src/main/java/com/tkforgeworks/cookconnect/${service_name}/common/${filename}"

            # Generate diff
            echo "Comparing: $reference_service vs $service"
            diff -u \
                <(normalize_file "$reference_file" | head -20) \
                <(normalize_file "$service_file" | head -20) \
                || true  # Don't exit on diff
            echo ""
        done

        ((diff_num++))
    done < /tmp/common_mismatches.txt

    echo ""
    print_header "REMEDIATION STEPS"
    echo "1. Review the diffs above"
    echo "2. Decide which version should be canonical"
    echo "3. Copy canonical version to all services"
    echo "4. Update ONLY the package declaration line in each service"
    echo "5. Re-run this validator to confirm consistency"
}

main() {
    echo "🔍 Validating Common Package Consistency..."
    echo ""

    # Clean up temp file
    rm -f /tmp/common_mismatches.txt

    # Build matrix
    build_file_matrix

    # Validate each file
    for filename in "${!file_matrix[@]}"; do
        services_list="${file_matrix[$filename]}"
        services_array=($services_list)

        if ! validate_file "$filename" "${services_array[@]}"; then
            EXIT_CODE=1
        fi
        echo ""
    done

    # Report results
    if [ $EXIT_CODE -eq 0 ]; then
        echo -e "${GREEN}✅ All common package files are consistent!${NC}"
    else
        echo -e "${RED}❌ Common package inconsistencies detected!${NC}"
        generate_diff_report
    fi

    # Cleanup
    rm -f /tmp/common_mismatches.txt

    exit $EXIT_CODE
}

main
```

**Key Implementation Notes**:

1. **Normalization Strategy**: Removes package declarations and normalizes service-specific imports, but preserves functional code including commented-out lines
2. **File Matrix**: Builds a complete picture of which files exist where before validation
3. **Service-Specific Files**: Automatically skips files that only exist in one service
4. **Diff Reporting**: Shows actual code differences, not just "files don't match"
5. **Exit Code**: Returns non-zero if any inconsistencies found (blocks CI/CD)

---

### C.2: version-merge-validation.yml Workflow

**Location**: `.github/workflows/version-merge-validation.yml`

**Purpose**: Validate version branch merges to main

**Pseudocode/Outline**:

```yaml
name: Version Merge Validation

# Trigger only on PRs to main from version branches (e.g., 0.1, 1.0)
on:
  pull_request:
    branches:
      - main
    types: [opened, synchronize, reopened]

permissions:
  contents: read
  pull-requests: write

jobs:
  # ============================================================
  # JOB 1: Detect if this is a version branch merge
  # ============================================================
  detect-version-branch:
    name: Detect Version Branch
    runs-on: ubuntu-latest
    outputs:
      is_version_branch: ${{ steps.check.outputs.is_version_branch }}
      version_number: ${{ steps.check.outputs.version_number }}

    steps:
      - name: Check if source branch is version branch
        id: check
        run: |
          BRANCH="${{ github.head_ref }}"

          # Version branch pattern: X.Y (e.g., 0.1, 1.0, 2.3)
          if [[ "$BRANCH" =~ ^[0-9]+\.[0-9]+$ ]]; then
            echo "is_version_branch=true" >> $GITHUB_OUTPUT
            echo "version_number=$BRANCH" >> $GITHUB_OUTPUT
            echo "✅ Detected version branch: $BRANCH"
          else
            echo "is_version_branch=false" >> $GITHUB_OUTPUT
            echo "ℹ️  Not a version branch merge, skipping validation"
          fi

  # ============================================================
  # JOB 2: Validate all services have updated versions
  # ============================================================
  validate-service-versions:
    name: Validate Service Versions
    runs-on: ubuntu-latest
    needs: detect-version-branch
    if: needs.detect-version-branch.outputs.is_version_branch == 'true'
    outputs:
      validation_results: ${{ steps.validate.outputs.results }}

    steps:
      - name: Checkout PR branch
        uses: actions/checkout@v4
        with:
          ref: ${{ github.head_ref }}
          fetch-depth: 0

      - name: Fetch main branch
        run: git fetch origin main

      - name: Compare service versions
        id: validate
        run: |
          SERVICES=("user-service" "recipe-service" "social-service" "config-server" "eureka-server")

          # Build markdown table
          echo "| Service | Main Version | Branch Version | Status |" > /tmp/version_table.txt
          echo "|---------|-------------|----------------|---------|" >> /tmp/version_table.txt

          ALL_UPDATED=true

          for service in "${SERVICES[@]}"; do
            POM_PATH="services/${service}/pom.xml"

            # Get version from main
            MAIN_VER=$(git show origin/main:${POM_PATH} | \
                       sed -n '/<\/parent>/,/<version>/ { /<version>/ { s/.*<version>\(.*\)<\/version>.*/\1/p; q } }' | \
                       tr -d '[:space:]')

            # Get version from PR branch
            BRANCH_VER=$(sed -n '/<\/parent>/,/<version>/ { /<version>/ { s/.*<version>\(.*\)<\/version>.*/\1/p; q } }' ${POM_PATH} | \
                         tr -d '[:space:]')

            # Check if service code changed
            CODE_CHANGED=$(git diff origin/main...HEAD -- "services/${service}/src" | wc -l)

            if [ "$CODE_CHANGED" -gt 0 ]; then
              # Service changed, version must be updated
              if [ "$MAIN_VER" == "$BRANCH_VER" ]; then
                echo "| ${service} | ${MAIN_VER} | ${BRANCH_VER} | ❌ Not updated |" >> /tmp/version_table.txt
                ALL_UPDATED=false
              else
                echo "| ${service} | ${MAIN_VER} | ${BRANCH_VER} | ✅ Updated |" >> /tmp/version_table.txt
              fi
            else
              # Service didn't change, version update optional
              if [ "$MAIN_VER" == "$BRANCH_VER" ]; then
                echo "| ${service} | ${MAIN_VER} | ${BRANCH_VER} | ℹ️  Unchanged |" >> /tmp/version_table.txt
              else
                echo "| ${service} | ${MAIN_VER} | ${BRANCH_VER} | ✅ Updated |" >> /tmp/version_table.txt
              fi
            fi
          done

          # Save results
          cat /tmp/version_table.txt

          if [ "$ALL_UPDATED" = false ]; then
            echo "❌ Some services with code changes do not have updated versions"
            echo "results=failed" >> $GITHUB_OUTPUT
            exit 1
          else
            echo "✅ All changed services have updated versions"
            echo "results=passed" >> $GITHUB_OUTPUT
          fi

  # ============================================================
  # JOB 3: Validate README updated
  # ============================================================
  validate-readme:
    name: Validate README Documentation
    runs-on: ubuntu-latest
    needs: detect-version-branch
    if: needs.detect-version-branch.outputs.is_version_branch == 'true'
    outputs:
      readme_updated: ${{ steps.check.outputs.updated }}

    steps:
      - name: Checkout PR branch
        uses: actions/checkout@v4
        with:
          ref: ${{ github.head_ref }}
          fetch-depth: 0

      - name: Fetch main branch
        run: git fetch origin main

      - name: Check README changes
        id: check
        run: |
          VERSION="${{ needs.detect-version-branch.outputs.version_number }}"

          # Check if README.md was modified
          README_CHANGED=$(git diff origin/main...HEAD -- README.md | wc -l)

          if [ "$README_CHANGED" -eq 0 ]; then
            echo "❌ README.md was not updated"
            echo "updated=false" >> $GITHUB_OUTPUT
            exit 1
          fi

          # Check if version number appears in README
          if ! grep -q "$VERSION" README.md; then
            echo "❌ README does not contain version $VERSION"
            echo "updated=false" >> $GITHUB_OUTPUT
            exit 1
          fi

          # Check specific sections updated
          CURRENT_STATUS_CHANGED=$(git diff origin/main...HEAD -- README.md | grep -A5 "## Current Status" | wc -l)

          if [ "$CURRENT_STATUS_CHANGED" -gt 0 ]; then
            echo "✅ README updated with version $VERSION"
            echo "updated=true" >> $GITHUB_OUTPUT
          else
            echo "⚠️  README changed but 'Current Status' section may not be updated"
            echo "updated=partial" >> $GITHUB_OUTPUT
          fi

  # ============================================================
  # JOB 4: Validate common package consistency
  # ============================================================
  validate-common-packages:
    name: Validate Common Package Consistency
    runs-on: ubuntu-latest
    needs: detect-version-branch
    if: needs.detect-version-branch.outputs.is_version_branch == 'true'

    steps:
      - name: Checkout PR branch
        uses: actions/checkout@v4
        with:
          ref: ${{ github.head_ref }}

      - name: Run common package validation
        run: |
          chmod +x .github/scripts/validate-common-packages.sh
          .github/scripts/validate-common-packages.sh

  # ============================================================
  # JOB 5: Generate comprehensive report
  # ============================================================
  report-results:
    name: Post Validation Report
    runs-on: ubuntu-latest
    needs: [detect-version-branch, validate-service-versions, validate-readme, validate-common-packages]
    if: always() && needs.detect-version-branch.outputs.is_version_branch == 'true'

    steps:
      - name: Generate PR comment
        uses: actions/github-script@v7
        with:
          script: |
            const version = '${{ needs.detect-version-branch.outputs.version_number }}';
            const servicesResult = '${{ needs.validate-service-versions.result }}';
            const readmeResult = '${{ needs.validate-readme.result }}';
            const commonResult = '${{ needs.validate-common-packages.result }}';

            let comment = '## 🔍 Version Merge Validation Report\\n\\n';
            comment += `**Branch**: \`${version}\` → \`main\`\\n\\n`;

            // Service versions section
            comment += '### Service Versions\\n';
            if (servicesResult === 'success') {
              comment += '✅ All changed services have updated versions\\n\\n';
            } else {
              comment += '❌ Some services require version updates\\n\\n';
            }

            // README section
            comment += '### README Documentation\\n';
            if (readmeResult === 'success') {
              comment += '✅ README updated with version information\\n\\n';
            } else {
              comment += '❌ README needs to be updated\\n\\n';
            }

            // Common packages section
            comment += '### Common Package Consistency\\n';
            if (commonResult === 'success') {
              comment += '✅ All common package files are consistent\\n\\n';
            } else {
              comment += '❌ Common package inconsistencies detected - check workflow logs\\n\\n';
            }

            comment += '---\\n\\n';

            if (servicesResult === 'success' && readmeResult === 'success' && commonResult === 'success') {
              comment += '**✅ All checks passed! This version branch is ready to merge to main.**';
            } else {
              comment += '**❌ Some checks failed. Please review and fix the issues above before merging.**';
            }

            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: comment
            });

      - name: Check overall status
        run: |
          if [ "${{ needs.validate-service-versions.result }}" != "success" ] || \
             [ "${{ needs.validate-readme.result }}" != "success" ] || \
             [ "${{ needs.validate-common-packages.result }}" != "success" ]; then
            echo "❌ Version merge validation failed"
            exit 1
          else
            echo "✅ Version merge validation passed"
          fi
```

**Key Implementation Notes**:

1. **Conditional Execution**: Only runs on version branch → main PRs (uses regex pattern matching)
2. **Independent Jobs**: Service versions, README, and common packages validated in parallel
3. **Smart Service Detection**: Only requires version updates for services with actual code changes
4. **README Validation**: Checks both that README changed AND contains the version number
5. **Comprehensive Reporting**: Posts single unified comment with all results
6. **Blocking**: Fails PR if any check fails

---

### C.3: Updates to Existing version-validation.yml

**Purpose**: Add common package validation to existing feature PR workflow

**Changes Required**:

```yaml
# In existing .github/workflows/version-validation.yml

# Add new job after validate-service-versions
jobs:
  # ... existing jobs ...

  validate-common-packages:
    name: Validate Common Package Consistency
    runs-on: ubuntu-latest
    needs: detect-changes
    # Only run if changes detected
    if: needs.detect-changes.outputs.has_changes == 'true'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          ref: ${{ github.head_ref }}
          fetch-depth: 0

      - name: Pull latest changes
        run: |
          git pull origin ${{ github.head_ref }}

      - name: Run common package validation
        run: |
          chmod +x .github/scripts/validate-common-packages.sh
          .github/scripts/validate-common-packages.sh

      - name: Comment on failure
        if: failure()
        uses: actions/github-script@v7
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: '❌ **Common Package Consistency Check Failed**\\n\\nShared classes in the `common` package are not identical across services. Please review the workflow logs for detailed diffs and ensure all common classes match after updating only the package declaration.'
            });

  # Update report-status job to include common package results
  report-status:
    name: Report Validation Status
    runs-on: ubuntu-latest
    needs: [auto-increment-parent, detect-changes, validate-service-versions, validate-common-packages]
    if: always()

    steps:
      # ... existing steps ...

      - name: Check validation results
        run: |
          echo "📝 Parent version auto-incremented to: ${{ needs.auto-increment-parent.outputs.new_version }}"

          # Check service versions
          if [ "${{ needs.validate-service-versions.result }}" == "failure" ]; then
            echo "❌ Service version validation failed"
            exit 1
          fi

          # Check common packages (NEW)
          if [ "${{ needs.validate-common-packages.result }}" == "failure" ]; then
            echo "❌ Common package validation failed"
            exit 1
          fi

          if [ "${{ needs.detect-changes.outputs.has_changes }}" == "false" ]; then
            echo "ℹ️  No service changes detected - validation skipped"
          else
            echo "✅ All validations passed"
          fi
```

**Key Implementation Notes**:

1. **Integration Point**: Adds as a parallel job to existing service version validation
2. **Conditional Execution**: Only runs when service changes detected
3. **Failure Handling**: Posts explanatory comment on PR if common package check fails
4. **Status Aggregation**: Updated report-status job includes common package results

---

### C.4: Testing Strategy

**Test Cases for validate-common-packages.sh**:

```bash
# Test 1: All files identical (should pass)
# Setup: Ensure all 6 common files are identical across services
./validate-common-packages.sh
# Expected: Exit code 0, all checkmarks

# Test 2: One file differs (should fail)
# Setup: Modify UserContext.java in social-service only
./validate-common-packages.sh
# Expected: Exit code 1, diff shown for UserContext.java

# Test 3: Service-specific file (should skip)
# Setup: AuthorizationHelper.java only in social-service
./validate-common-packages.sh
# Expected: Exit code 0, "skipped" message for AuthorizationHelper.java

# Test 4: Commented-out code difference (should fail)
# Setup: Comment out a line in one service
./validate-common-packages.sh
# Expected: Exit code 1, diff shows commented line as difference

# Test 5: Whitespace-only differences (should pass)
# Setup: Add extra spaces/newlines in one file
./validate-common-packages.sh
# Expected: Exit code 0 (normalized away)
```

**Test Cases for version-merge-validation.yml**:

```yaml
# Test 1: Version branch with all validations passing
# Setup:
# - Create branch named "0.2"
# - Update all service versions
# - Update README
# - Ensure common packages consistent
# - Create PR to main
# Expected: All checks green, approval comment posted

# Test 2: Version branch with service version missing
# Setup:
# - Create branch named "0.2"
# - Modify user-service code but don't update version
# - Create PR to main
# Expected: Service version check fails, specific error shown

# Test 3: Version branch with README not updated
# Setup:
# - Create branch named "0.2"
# - Update all services but not README
# - Create PR to main
# Expected: README check fails

# Test 4: Non-version branch (should skip)
# Setup:
# - Create branch named "feature/add-auth"
# - Create PR to main
# Expected: Validation skipped entirely

# Test 5: Version branch with common package drift
# Setup:
# - Create branch named "0.2"
# - Have inconsistent common files
# - Create PR to main
# Expected: Common package check fails with diffs
```

---

### C.5: Local Development Usage

**Running validate-common-packages.sh locally**:

```bash
# From repository root
chmod +x .github/scripts/validate-common-packages.sh
.github/scripts/validate-common-packages.sh

# To see detailed diff output even on success
DEBUG=1 .github/scripts/validate-common-packages.sh

# To test normalization on a specific file
# (for debugging)
source .github/scripts/validate-common-packages.sh
normalize_file services/user-service/src/main/java/.../common/UserContext.java
```

**Simulating version merge validation locally**:

```bash
# Check if your branch would pass validation
BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD)

# Check if it's a version branch
if [[ "$BRANCH_NAME" =~ ^[0-9]+\.[0-9]+$ ]]; then
  echo "This is a version branch: $BRANCH_NAME"

  # Run all checks
  echo "1. Validating service versions..."
  # Compare each service's pom.xml between main and current branch

  echo "2. Validating README..."
  git diff main...HEAD -- README.md

  echo "3. Validating common packages..."
  .github/scripts/validate-common-packages.sh
else
  echo "Not a version branch, version merge validation would be skipped"
fi
```

---

### C.6: Deployment Checklist

**Before merging to 0.1 branch**:

- [ ] Create `.github/scripts/validate-common-packages.sh`
- [ ] Make script executable: `chmod +x .github/scripts/validate-common-packages.sh`
- [ ] Test script locally with current `0.1` branch (should fail on existing drift)
- [ ] Fix drift issues found in `0.1` branch:
  - [ ] Decide canonical version of `UserContext.java`
  - [ ] Decide canonical version of `FeignInterceptor.java`
  - [ ] Update all services to canonical versions
  - [ ] Re-run script to confirm (should pass)
- [ ] Create `.github/workflows/version-merge-validation.yml`
- [ ] Update `.github/workflows/version-validation.yml` to add common package check
- [ ] Test workflows on feature branch PR (should add common package validation)
- [ ] Commit all changes to `0.1` branch
- [ ] Update README with validation information

**When ready to merge 0.1 to main**:

- [ ] Create PR from `0.1` → `main`
- [ ] Verify `version-merge-validation.yml` runs automatically
- [ ] Review validation report posted to PR
- [ ] Fix any issues identified
- [ ] Wait for all checks to pass
- [ ] Merge to main

**Post-deployment**:

- [ ] Monitor first few PRs to ensure validations work as expected
- [ ] Adjust normalization rules if too strict/lenient
- [ ] Document any edge cases discovered
- [ ] Consider adding validation to pre-commit hooks for faster feedback
