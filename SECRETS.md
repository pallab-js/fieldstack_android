# Required GitHub Secrets

Set these in **Settings → Secrets and variables → Actions** before pushing a release tag.

| Secret | Description |
|---|---|
| `SIGNING_KEY_BASE64` | `base64 -i keystore.jks` output |
| `STORE_PASSWORD` | Keystore store password |
| `KEY_ALIAS` | Key alias inside the keystore |
| `KEY_PASSWORD` | Key password |
| `FIREBASE_APP_ID` | Firebase App ID (from Firebase Console) |
| `FIREBASE_SERVICE_ACCOUNT` | Contents of Firebase service account JSON |
| `NVD_API_KEY` | NVD API key for OWASP dependency-check (get free key at https://nvd.nist.gov/developers/request-an-api-key) |

## Generate a keystore (first time)

```bash
keytool -genkey -v \
  -keystore fieldstack-release.jks \
  -alias fieldstack \
  -keyalg RSA -keysize 2048 \
  -validity 10000

# Encode for GitHub secret
base64 -i fieldstack-release.jks | pbcopy
```

## Trigger a release

```bash
git tag v0.1.0-mvp
git push origin v0.1.0-mvp
```

GitHub Actions will:
1. Run unit tests
2. Build signed prod release APK
3. Upload APK as workflow artifact
4. Upload dev debug build to Firebase App Distribution (internal-qa group)

## Play Store upload (after internal testing)

```bash
# Set the path via env var (never commit the file itself)
export PLAY_SERVICE_ACCOUNT_PATH=/path/to/play-service-account.json
./gradlew :app:publishProdReleaseApk

# Or pass as a Gradle property
./gradlew :app:publishProdReleaseApk -Pplay.serviceAccountCredentials=/path/to/play-service-account.json
```
