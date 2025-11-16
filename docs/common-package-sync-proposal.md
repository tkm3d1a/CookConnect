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

### Common Package Structure

Each business service contains duplicated "common" code in two packages:

#### 1. `errorhandler` Package
Located at: `services/{service}/src/main/java/com/tkforgeworks/cookconnect/{service}service/errorhandler/`

**Classes** (4 files per service):
- `ErrorMessage.java` - Error message structure with factory methods
- `ExceptionController.java` - Global exception handler (@RestControllerAdvice)
- `RestErrorList.java` - Collection wrapper for multiple errors
- `ResponseWrapper.java` - Generic API response wrapper

#### 2. `config` Package
Located at: `services/{service}/src/main/java/com/tkforgeworks/cookconnect/{service}service/config/`

**Classes** (1 file per service):
- `ServiceConfig.java` - Configuration properties (@ConfigurationProperties)

### Current Duplication Issue

These classes are **functionally identical** across all three business services, differing only in:
- Package declaration (e.g., `userservice` vs `recipeservice` vs `socialservice`)
- Imports that reference the service-specific package

**Example**: `ErrorMessage.java` is identical in user-service and recipe-service except for line 1:
```java
// user-service
package com.tkforgeworks.cookconnect.userservice.errorhandler;

// recipe-service
package com.tkforgeworks.cookconnect.recipeservice.errorhandler;
```

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
1. Discover all Java files in `errorhandler` and `config` packages across services
2. Normalize files by:
   - Removing package declarations
   - Normalizing service-specific imports
   - Removing whitespace variations
3. Compare normalized versions using SHA-256 checksums
4. Report inconsistencies with detailed diffs
5. Exit with error code if mismatches found

**Example Output**:
```
🔍 Validating Common Package Consistency...

Checking errorhandler/ErrorMessage.java:
  ✅ user-service     [SHA: a1b2c3...]
  ✅ recipe-service   [SHA: a1b2c3...]
  ✅ social-service   [SHA: a1b2c3...]

Checking errorhandler/ExceptionController.java:
  ✅ user-service     [SHA: d4e5f6...]
  ❌ recipe-service   [SHA: 123abc...]  MISMATCH!
  ✅ social-service   [SHA: d4e5f6...]

❌ Inconsistencies detected!

Diff between user-service and recipe-service:
services/user-service/.../ExceptionController.java
services/recipe-service/.../ExceptionController.java

--- user-service
+++ recipe-service
@@ -45,7 +45,7 @@
-    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
+    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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
              body: '❌ Common package consistency check failed. Please ensure all shared classes in `errorhandler` and `config` packages are identical across services.'
            });
```

#### Approach B: Centralized Common Library (Future Enhancement)

**Long-term recommendation**: Extract common packages into a shared Maven module

**Structure**:
```
services/
  common-lib/           # New shared module
    src/main/java/com/tkforgeworks/cookconnect/common/
      errorhandler/     # Moved from individual services
      config/           # Moved from individual services
    pom.xml
  user-service/
    pom.xml             # Add dependency on common-lib
  recipe-service/
    pom.xml             # Add dependency on common-lib
  social-service/
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
2. Extract errorhandler package
3. Extract config package
4. Update all service dependencies
5. Comprehensive integration testing
6. Update CI/CD for multi-module build

---

## Technical Specifications

### Common Package Detection Logic

**Packages to Monitor**:
```bash
# Define common package patterns
COMMON_PACKAGES=(
  "errorhandler"
  "config"
)

# Services to check
SERVICES=(
  "user-service"
  "recipe-service"
  "social-service"
)
```

**Normalization Process**:
```bash
normalize_file() {
  local file=$1

  # Remove package declaration
  sed '/^package /d' "$file" |

  # Normalize service-specific imports
  sed 's/com\.tkforgeworks\.cookconnect\.[a-z]*service\./com.tkforgeworks.cookconnect.SERVICE./g' |

  # Remove comments and extra whitespace
  sed '/^\/\//d' | sed '/^\/\*/,/\*\//d' | sed 's/[[:space:]]*$//'
}
```

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

**A**: The validation only checks classes that exist in multiple services. If `user-service` has `ErrorMessage.java` but `social-service` doesn't, no validation occurs. Only when both have the same filename are they compared.

### Q2: Can I intentionally make a common class different in one service?

**A**: Yes, but rename it to signal the difference (e.g., `UserSpecificErrorMessage.java`). The validator only compares files with identical names.

### Q3: What about auto-formatting differences?

**A**: The normalization process removes whitespace, comments, and formatting differences before comparison. Only functional code differences trigger failures.

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
│   ├── errorhandler/
│   │   ├── ErrorMessage.java
│   │   ├── ExceptionController.java
│   │   ├── RestErrorList.java
│   │   └── ResponseWrapper.java
│   └── config/
│       └── ServiceConfig.java
│
├── recipe-service/src/main/java/com/tkforgeworks/cookconnect/recipeservice/
│   ├── errorhandler/
│   │   ├── ErrorMessage.java
│   │   ├── ExceptionController.java
│   │   ├── RestErrorList.java
│   │   └── ResponseWrapper.java
│   └── config/
│       └── ServiceConfig.java
│
└── social-service/src/main/java/com/tkforgeworks/cookconnect/socialservice/
    ├── errorhandler/
    │   ├── ErrorMessage.java
    │   ├── ExceptionController.java
    │   ├── RestErrorList.java
    │   └── ResponseWrapper.java
    └── config/
        └── ServiceConfig.java
```

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
- ✅ errorhandler/ErrorMessage.java (3 services)
- ✅ errorhandler/ExceptionController.java (3 services)
- ✅ errorhandler/RestErrorList.java (3 services)
- ✅ errorhandler/ResponseWrapper.java (3 services)
- ✅ config/ServiceConfig.java (3 services)

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
