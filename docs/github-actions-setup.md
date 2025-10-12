# GitHub Actions Setup Guide

## Version Validation Workflow

The CookConnect project uses GitHub Actions to automatically validate semantic versioning and enforce version increment requirements on pull requests to the `main` branch.

## How It Works

### Workflow Steps

1. **Auto-Increment Parent POM** - Automatically increments the parent POM version (patch +1)
2. **Update Service Parent References** - Updates all service POMs to reference the new parent version
3. **Detect Changed Services** - Identifies which services have code or POM changes
4. **Validate Service Versions** - Ensures changed services have properly incremented versions
5. **Report Status** - Posts validation results as a PR comment

### Automatic Updates

When you create a PR to `main`, the workflow will:
- ✅ Automatically increment parent POM from `0.0.1` → `0.0.2`
- ✅ Update all service POMs to reference the new parent version
- ✅ Commit these changes back to your PR branch
- ✅ Validate that any changed services have incremented their own versions

### Smart Change Detection

The workflow intelligently filters out parent-only changes:
- If a service POM only has parent version updates → **No validation required**
- If a service has code changes or service version updates → **Validation required**

## Enabling Branch Protection

To **prevent merging PRs with validation failures**, you need to enable branch protection rules.

### Step-by-Step Instructions

1. **Navigate to Repository Settings**
   - Go to your repository on GitHub
   - Click **Settings** tab
   - Click **Branches** in the left sidebar (under "Code and automation")

2. **Add Branch Protection Rule**
   - Click **Add rule** or **Add branch protection rule**
   - In "Branch name pattern", enter: `main`

3. **Configure Protection Rules**

   Enable the following options:

   ✅ **Require a pull request before merging**
   - Ensures all changes go through PR review process

   ✅ **Require status checks to pass before merging**
   - This is the critical setting for blocking failed validations
   - Click the search box under "Status checks that are required"
   - Search for and select: **Version Validation / report-status**
   - This makes the validation workflow a required check

   ✅ **Require branches to be up to date before merging** (Optional but recommended)
   - Ensures PRs are tested against the latest main branch

   ⚠️ **Do not select "Include administrators"** unless you want these rules to apply to repo admins as well

4. **Save Changes**
   - Scroll to bottom and click **Create** or **Save changes**

### Verification

After enabling branch protection:

1. Create a test PR with a service change but **without** incrementing the service version
2. The workflow should run and **fail**
3. The PR merge button should be **disabled** with a message: "Merging is blocked - Required status check 'Version Validation / report-status' must pass"
4. The PR should have a red ❌ indicator next to the failing check

## Required GitHub Permissions

The workflow requires the following permissions (already configured in the workflow file):

```yaml
permissions:
  contents: write       # To commit auto-increment changes
  pull-requests: write  # To post PR comments
```

These permissions are automatically granted to `github-actions[bot]` when using `GITHUB_TOKEN`.

## Troubleshooting

### Issue: Workflow runs but doesn't block PR merges

**Solution:** Verify branch protection is configured correctly:
- Go to Settings → Branches
- Check that `main` has a protection rule
- Verify "Require status checks to pass before merging" is enabled
- Ensure "Version Validation / report-status" is in the required checks list

### Issue: Workflow runs twice on each push

**Solution:** This is expected behavior:
1. First run: Detects missing parent increment, commits update
2. Validation runs in the same workflow (not a second trigger)

The `[skip ci]` flag prevents infinite loops.

### Issue: Parent version not updating in service POMs

**Solution:** Check the workflow logs for the "Update service POM parent references" step. If it's being skipped, the parent version may have already been at the correct version.

### Issue: Service flagged as changed when it shouldn't be

**Solution:** The workflow filters parent-only changes. If a service is incorrectly flagged:
1. Check if the service has code changes in `src/`
2. Check if the service's own `<version>` tag (not parent reference) changed
3. Review the "Build changed services matrix" step in workflow logs

## Manual Validation

You can manually test version validation using the standalone script:

```bash
# Format: ./validate-semver.sh <main_version> <pr_version> <service_name>

# Example: Validate user-service increment from 0.0.1 to 0.0.2
.github/scripts/validate-semver.sh 0.0.1 0.0.2 user-service
```

This is useful for:
- Testing version increments locally before pushing
- Understanding why a version failed validation
- Verifying increment rules (major/minor/patch)

## Workflow Files

- **`.github/workflows/version-validation.yml`** - Main workflow definition
- **`.github/scripts/validate-semver.sh`** - Reusable validation script

## Versioning Rules Reference

See the [Versioning Strategy](../README.md#versioning-strategy) section in the README for detailed information about:
- Semantic versioning format (X.Y.Z)
- Increment rules (patch, minor, major)
- When to increment each version component
- Example validation scenarios

## Disabling Validation (Emergency Only)

If you absolutely must bypass validation (emergency hotfix):

1. Add `[skip ci]` to your commit message
2. This will skip the entire workflow

**Warning:** Only use this for critical production fixes. Skipping validation can lead to version inconsistencies.

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Semantic Versioning 2.0.0](https://semver.org/)
- [GitHub Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
