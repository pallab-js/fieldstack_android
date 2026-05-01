# FieldStack Android

Offline-first field operations management app for Android. Built with Kotlin, Jetpack Compose, and the BudgetZen design system.

## Features

- **Offline-first** — full task management without connectivity; syncs via WorkManager when back online
- **Report builder** — 5-step wizard with photo capture (CameraX), GPS tagging, barcode/QR scanning (ML Kit), and custom fields
- **Role-based access** — FieldTech / Supervisor / Admin roles with scoped permissions
- **Task comments** — threaded comments with @mention highlighting
- **Insights dashboard** — weekly metrics, CSV export, and PDF report generation
- **Biometric unlock** — BiometricPrompt integration
- **Accessible** — WCAG AA contrast, full TalkBack support

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| DB | Room |
| Network | Retrofit + OkHttp |
| Sync | WorkManager |
| Camera | CameraX |
| Scanning | ML Kit Barcode |
| Auth | DataStore + BiometricPrompt |
| CI/CD | GitHub Actions + Firebase App Distribution |

## Getting Started

1. Clone the repo
2. Add `google-services.json` to `app/` (from Firebase Console)
3. Add `local.properties` with your SDK path
4. Run on a device or emulator:

```bash
./gradlew :app:installDebug
```

## CI/CD

- **PRs** → runs unit tests automatically
- **Tag push** (`v*`) → builds signed release APK and uploads to Firebase App Distribution

See [SECRETS.md](SECRETS.md) for required GitHub Actions secrets.

## License

MIT — see [LICENSE.md](LICENSE.md)
