# Remaining Cleanup Items — Gitea Migration Prep

Tracked items that still need to be addressed before or during the repository transfer to self-hosted Gitea.

> **Note:** This file and the rest of `docs/` are slated for migration to Confluence under the tkforgeworks space. Once migrated, remove the `docs/` directory from the repo entirely.

---

## 1. Credentials Templating

**Priority:** High — should be done before the repo goes public or transfers to Gitea.

### Config Server encrypted password
- **File:** `servers/config-server/src/main/resources/application.yml:14`
- The git backend password is a `{cipher}` value that depends on the `ENCRYPT_KEY` env var. The encrypted blob is committed in the repo. Evaluate whether this is acceptable or if the entire credential should be externalized (e.g., env var, Vault, K8s secret).

### Database root password
- **File:** `docker/cookconnect-db/.env`
- Contains `MYSQL_ROOT_PASSWORD=root` in plain text. This file is tracked in git despite the `**/.env*` gitignore pattern (it was committed before the pattern was added).
- **Action:** Run `git rm --cached docker/cookconnect-db/.env` and all other `.env` files under `docker/` that are still tracked. Replace with `.env.example` templates that document required variables without real values.

### Tracked .env files to audit
All `.env` files under `docker/` were committed before the gitignore update. Run `git ls-files -- '**/.env*'` to find them and remove from tracking. Most are empty but should still be untracked for consistency.

---

## 2. Docker Compose — Hardcoded Image Versions

**Priority:** Medium

- **File:** `docker/cookconnect/docker-compose.yaml`
- Service images use hardcoded version tags that are already stale:
  - `config-server: 0.0.1`
  - `recipe-service: 0.0.2`
  - `social-service: 0.0.2`
  - `user-service: 0.0.3`
- **Action:** Replace with environment variable substitution (e.g., `image: tkforgeworks/cookconnect-config-server:${CONFIG_VERSION:-latest}`) or a single `COOKCONNECT_VERSION` var. Document the expected env vars in a `.env.example`.

---

## 3. GitHub Actions → Gitea CI/CD Migration

**Priority:** High — must be done at migration time.

- **File:** `.github/workflows/version-validation.yml`
- This workflow handles:
  1. Auto-incrementing the parent POM version on PRs to main
  2. Detecting which services changed
  3. Validating semantic versioning per service
  4. Posting PR comments with version info
- **Action:** Rewrite as a Gitea Actions workflow (Gitea supports a GitHub Actions-compatible runner) or migrate to an alternative CI tool (Woodpecker, Drone, Jenkins). The version validation logic in the shell scripts should transfer with minimal changes — the GitHub-specific parts are the trigger events and `gh` CLI calls.

---

## 4. Config Server Git URI

**Priority:** Must update post-migration.

- **File:** `servers/config-server/src/main/resources/application.yml:12`
- Currently points to `https://github.com/tkm3d1a/CookConnect-config.git`
- After the Gitea instance is set up, update this to the new Gitea URL. The `CookConnect-config` repo also needs to be migrated.

---

## 5. Migrate `docs/` to Confluence

**Priority:** Low — can be done incrementally.

The following files in `docs/` should be migrated to Confluence pages under the tkforgeworks space:

| File | Suggested Confluence Location |
|------|-------------------------------|
| `CookConnect_Refactor_Plan.md` | CC project space — Refactor epic documentation |
| `cookconnect_canonical_model.md` | CC project space — Data model reference |
| `cookconnect_data_domain_v0-0-1.md` | CC project space — Domain documentation |
| `cookconnect_architecture_deviations_v0-0-1.md` | CC project space — Architecture decisions |
| `documentation.md` | Becomes the Confluence space landing page |
| `base-pom-setup.md` | CC project space — Developer setup |
| `github-actions-setup.md` | CC project space — CI/CD (update for Gitea) |
| `prereqs.md` | CC project space — Developer setup |
| `running-services.md` | CC project space — Developer setup |
| `cookconnect_pdd.md` | CC project space — Design documentation |

After migration, remove `docs/` from the repo. Keep only `CLAUDE.md` and `README.md` in the repo root.
