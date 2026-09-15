# Ziven Android

Native Android client for the Ziven household meal OS.

## Build

1. Install Android Studio + SDK.
2. Copy `local.properties.example` to `local.properties` and set `sdk.dir`.
3. Sync Gradle.
4. Run `./gradlew :app:assembleDebug`.

## Architecture

- `:shared` — Kotlin/JVM module with the generated OpenAPI client, auth
  interceptor, and repository interfaces (KMP-ready).
- `:app` — Jetpack Compose screens, navigation, theme, and camera entry points.

The app is a thin client: all planning, diet-law, nutrition, and shopping math
lives in `ziven-os`.
