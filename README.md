# FieldStack Android

Offline-first field operations management app for Android. Built with Kotlin, Jetpack Compose, and the BudgetZen design system.

**Version:** 0.1.0-mvp · **Min SDK:** 26 (Android 8.0) · **Target SDK:** 34

## Features

- **Offline-first** — full task management without connectivity; syncs via WorkManager with exponential backoff and delta sync
- **Report builder** — 5-step wizard with photo capture (CameraX), GPS tagging, barcode/QR scanning (ML Kit), and custom fields
- **Role-based access** — FieldTech / Supervisor / Admin roles with scoped permissions
- **Task comments** — threaded comments with @mention highlighting
- **Insights dashboard** — weekly metrics, CSV export, and PDF report generation
- **Biometric unlock** — BiometricPrompt integration with session re-auth
- **Accessible** — WCAG AA contrast, full TalkBack support, heading semantics

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| DB | Room (with schema export) |
| Network | Retrofit + OkHttp + Moshi |
| Sync | WorkManager |
| Camera | CameraX |
| Scanning | ML Kit Barcode |
| Auth | DataStore + EncryptedSharedPreferences + BiometricPrompt |
| Image loading | Coil 3 |
| Crash reporting | Firebase Crashlytics |
| Logging | Timber |
| CI/CD | GitHub Actions + Firebase App Distribution |

## Build Variants

Two product flavors × three build types:

| Flavor | Build type | App ID suffix | API |
|---|---|---|---|
| `dev` | `debug` | `.dev.debug` | `dev-api.fieldstack.com` |
| `dev` | `staging` | `.dev.staging` | `dev-api.fieldstack.com` |
| `prod` | `release` | *(none)* | `api.fieldstack.com` |

## Getting Started

1. Clone the repo
2. Add `google-services.json` to `app/` (from Firebase Console)
3. Add `local.properties` with your SDK path:
   ```
   sdk.dir=/Users/<you>/Library/Android/sdk
   ```
4. Generate dependency lockfiles (first time, and after any version change):
   ```bash
   ./gradlew :app:dependencies --write-locks
   ```
5. Run on a device or emulator:
   ```bash
   # Dev debug (default for development)
   ./gradlew :app:installDevDebug

   # Prod release (requires signing config)
   ./gradlew :app:assembleProdRelease
   ```

## Running Tests

```bash
# Unit tests (dev flavor)
./gradlew :app:testDevDebugUnitTest

# Lint
./gradlew :app:lintDevDebug

# OWASP dependency vulnerability check (requires NVD API key)
NVD_API_KEY=<your-key> ./gradlew :app:dependencyCheckAnalyze
```

## CI/CD

| Trigger | Pipeline |
|---|---|
| PR / push to `main` or `develop` | Unit tests + lint + OWASP check + Trivy scan |
| Tag push (`v*.*.*`) | Tests → signed prod release APK (artifact) → dev debug to Firebase App Distribution |
| Manual `workflow_dispatch` | Same as tag push (dry-run) |

See [SECRETS.md](SECRETS.md) for required GitHub Actions secrets.

## Project Structure

```
app/src/
├── main/          # Production source + resources
├── dev/           # Dev-flavor overrides (mock data, debug tooling)
├── prod/          # Prod-flavor overrides
├── test/          # Unit tests
└── androidTest/   # Instrumented tests
```

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## License

MIT — see [LICENSE.md](LICENSE.md)
