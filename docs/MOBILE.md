# Ziven Mobile Architecture

This document describes the native Android client. iOS is planned but not started.

## Repositories

- `ziven-os` — system of record; all planner/business logic lives here.
- `ziven-contracts` — versioned OpenAPI spec and generated TypeScript client.
- `ziven-android` — this native Kotlin Android project.

## Module map

```
ziven-android/
  build.gradle.kts          # root project + plugins
  settings.gradle.kts
  gradle/libs.versions.toml
  shared/                 # Kotlin/JVM module, KMP-ready boundary
    api/ApiClient.kt      # Ktor client + hand-scaffold until OpenAPI gen
    model/*.kt            # DTOs mirroring ziven-contracts
    api/generated/        # placeholder for OpenAPI Kotlin generator
  app/                    # Jetpack Compose Android app
    data/auth/            # SecureTokenStore, AuthRepository
    data/cache/           # DataStore read cache
    data/plan/            # PlanRepository
    data/shop/            # ShopRepository
    data/pantry/          # PantryRepository
    data/household/       # HouseholdRepository
    data/nutrition/       # NutritionRepository
    data/account/         # AccountRepository
    data/preview/         # PreviewRepository, ImportQueue
    ui/auth/              # Login / Register
    ui/main/              # MainScreen (5-tab shell)
    ui/today/             # Heute
    ui/plan/              # Plan
    ui/cook/              # Cook session
    ui/list/              # Einkaufsliste
    ui/pantry/            # Vorrat
    ui/barcode/           # Camera barcode scanner
    ui/more/              # Nutrition, household, profile, weight, legal, export/delete
    ui/preview/           # Guest week preview
    navigation/           # ZivenNavHost
    ui/theme/             # Živ Radix colors / typography
```

## Authentication

The server exposes a mobile bearer-token track at `/v1/auth/mobile/*`:

- `POST /v1/auth/mobile/register`
- `POST /v1/auth/mobile/login`
- `POST /v1/auth/mobile/refresh`
- `POST /v1/auth/mobile/logout`
- `GET /v1/auth/mobile/me`

Access tokens are short-lived (~1h); refresh tokens are long-lived (~90d) and rotated with a 30s grace window for races.

Tokens are stored in `EncryptedSharedPreferences` protected by Android Keystore (`MasterKey.AES256_GCM`). The web HttpOnly cookie is never used by the app.

## Base URLs

| Flavor | Base URL |
|--------|----------|
| `dev`  | `http://10.0.2.2:3200` (emulator) or override via `local.properties` `baseUrl=` for a physical LAN IP |
| `prod` | `https://zivenpulse.de` |

`local.properties` example:

```
sdk.dir=C:\\Users\\...\\Android\\Sdk
baseUrl=http://192.168.1.42:3200
```

## Deep links

The manifest declares `ziven://invite/<token>`. The web HTTPS invite path (`https://zivenpulse.de/invite/...`) is deferred to a future app-link verification.

Invites are accepted through `POST /v1/households/join { token }`. The app currently accepts the token via the household invite flow and manual entry; a future update will extract the token from the launch intent automatically.

## Native-only responsibilities

- Barcode scanning with CameraX + ML Kit → proxied to `/v1/products/{code}`
- Android share sheet for shopping list and export JSON
- System back stack / predictive back
- Biometric unlock (deferred)
- Local notifications (deferred)

## Server responsibilities (not duplicated)

- `selectPlan`, diet-law engine, allergen confidence, Mifflin macro targets
- Shopping aggregation, pantry decrement, leftover cook transactions
- Halal/Kosher confidence — the app only displays the existing confidence chips and never claims certification

## Offline

`CacheStore` (Preferences DataStore) keeps the last read Today plan, shopping list, and current cook session. Reads are served from cache first when the network is unavailable; writes (generate, cook completion, checkoffs) require connectivity.

## Shared code / iOS future

`:shared` currently compiles for JVM/Android. To support iOS later:

- Add `kotlin("multiplatform")` plugin and iOS targets to `:shared`.
- Keep `ApiClient` and DTOs platform-agnostic (Ktor + kotlinx.serialization already are).
- iOS SwiftUI app will use the same `/v1/auth/mobile/*` endpoints and the same OpenAPI spec; it can either consume the KMP shared module or generate a Swift client from `ziven-contracts`.

## Build

```bash
./gradlew :app:assembleDevDebug
```

## Security notes

- No API keys for OpenFoodFacts in the APK; barcode lookup goes through the backend.
- No Stripe/Home Connect secrets in the app.
- No live keys or `DEV_VERIFY_CODE` behaviour.
- HTTPS only in `prod`; `usesCleartextTraffic` is enabled only for the `dev` emulator/LAN builds.

## Gaps vs web

- Onboarding wizard UI is simplified.
- Invite deep-link token extraction from the launch intent is not yet automatic.
- Compose UI tests are scaffolded but not yet comprehensive.
- Biometric unlock and push notifications are deferred.
