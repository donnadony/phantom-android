# Phantom Android

Cross-platform debug toolkit for mobile apps - Android implementation.

[![Android](https://img.shields.io/badge/Android-7.0%2B-green)](phantom/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)](phantom/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue)](phantom/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

## Features

- **Logs** - App-level logging with levels (debug, info, warning, error, critical) and tag filtering
- **Network Inspector** - Capture and inspect HTTP requests/responses with expandable JSON tree viewer
- **Mock Services** - Intercept network requests and return mock responses at runtime, persisted across restarts
- **Configuration** - Generic key-value override system for runtime config changes (text, toggle, picker)
- **OkHttp Interceptor** - One-line integration for automatic network logging and mock interception

## Installation

### JitPack

Add the repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.donnadony:phantom-android:0.0.1")
}
```

### Local Module

Copy the `phantom/` directory into your project and add to `settings.gradle.kts`:

```kotlin
include(":phantom")
```

Then add the dependency:

```kotlin
dependencies {
    implementation(project(":phantom"))
}
```

## Quick Start

### 1. Initialize

Call `Phantom.init()` in your `Application.onCreate()`:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Phantom.init(this)
    }
}
```

### 2. App Logging

```kotlin
Phantom.log(PhantomLogLevel.INFO, "User logged in", "Auth")
Phantom.log(PhantomLogLevel.ERROR, "Failed to fetch data", "API")
Phantom.log(PhantomLogLevel.WARNING, "Slow response: 2.3s", "Network")
```

### 3. Network Logging

**Option A: OkHttp Interceptor (recommended)**

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(PhantomOkHttpInterceptor())
    .build()
```

This automatically logs all requests/responses and checks mock rules before hitting the network.

**Option B: Manual logging**

```kotlin
Phantom.logRequest(
    url = "https://api.example.com/users",
    method = "GET",
    headers = mapOf("Authorization" to "Bearer ..."),
    body = null
)

Phantom.logResponse(
    url = "https://api.example.com/users",
    method = "GET",
    headers = mapOf("Content-Type" to "application/json"),
    body = """{"id": 1, "name": "John"}""",
    statusCode = 200
)
```

### 4. Mock Services

Mock rules are created in the debug panel UI and persisted via SharedPreferences. You can also check programmatically:

```kotlin
val mock = Phantom.mockResponse("https://api.example.com/users", "GET")
if (mock != null) {
    val (body, statusCode) = mock
    // Use mock data instead of real network call
}
```

### 5. Configuration

```kotlin
Phantom.registerConfig(
    label = "API Base URL",
    key = "api_base_url",
    defaultValue = "https://prod.api.com"
)

Phantom.registerConfig(
    label = "Environment",
    key = "environment",
    defaultValue = "production",
    type = PhantomConfigType.PICKER,
    options = listOf("development", "staging", "production")
)

Phantom.registerConfig(
    label = "Dark Mode",
    key = "dark_mode",
    defaultValue = "false",
    type = PhantomConfigType.TOGGLE
)

// Read config values anywhere in your app
val baseUrl = Phantom.config("api_base_url") ?: "https://prod.api.com"
```

### 6. Show Debug Panel

```kotlin
Phantom.show(context)
```

## Theme Configuration

Phantom ships with a dark theme (Kodivex) by default. Customize by providing a `PhantomTheme`:

```kotlin
val customTheme = PhantomTheme(
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E),
    accent = Color(0xFFE94560),
    // ... all other properties from PhantomTheme.default
)
Phantom.setTheme(customTheme)
```

| Property | Description |
|----------|-------------|
| `background` | Main screen background |
| `surface` | Card and container backgrounds |
| `surfaceSecondary` | Elevated surface backgrounds |
| `textPrimary` | Primary text color |
| `textSecondary` | Secondary/muted text |
| `textTertiary` | Tertiary/hint text |
| `accent` | Accent color for buttons, links, icons |
| `accentSecondary` | Accent variant |
| `border` | Border/divider color |
| `success` / `warning` / `error` / `critical` | Log level colors |
| `info` / `debug` | Log level colors |
| `statusSuccess` / `statusClientError` / `statusServerError` | HTTP status colors |
| `mockEnabled` / `mockDisabled` | Mock rule toggle colors |
| `configModified` / `configDefault` | Config badge colors |
| `searchBackground` | Search field background |

## Architecture

```
phantom/
└── src/main/kotlin/com/phantom/
    ├── Phantom.kt                    # Public API facade
    ├── PhantomOkHttpInterceptor.kt   # OkHttp integration
    ├── core/                         # Singleton managers
    │   ├── PhantomLogger.kt
    │   ├── PhantomNetworkLogger.kt
    │   ├── PhantomMockInterceptor.kt
    │   └── PhantomConfig.kt
    ├── model/                        # Data classes
    ├── ui/                           # Jetpack Compose screens
    │   ├── PhantomActivity.kt
    │   ├── PhantomScreen.kt
    │   ├── logs/
    │   ├── network/
    │   ├── mock/
    │   └── config/
    ├── theme/                        # Compose theme system
    └── util/                         # JSON formatting, cURL generator
```

## API Reference

```kotlin
object Phantom {
    fun init(context: Context)
    fun setTheme(theme: PhantomTheme)

    // Logging
    fun log(level: PhantomLogLevel = INFO, message: String, tag: String? = null)

    // Network - manual
    fun logRequest(url: String, method: String, headers: Map<String, String> = emptyMap(), body: String? = null)
    fun logResponse(url: String, method: String, headers: Map<String, String> = emptyMap(), body: String? = null, statusCode: Int? = null)

    // Network - OkHttp
    fun logRequest(request: okhttp3.Request)
    fun logResponse(request: okhttp3.Request, response: okhttp3.Response)

    // Mock
    fun mockResponse(url: String, method: String): Pair<ByteArray, Int>?

    // Config
    fun registerConfig(label: String, key: String, defaultValue: String, type: PhantomConfigType = TEXT, options: List<String> = emptyList())
    fun config(key: String): String?
    fun setConfig(key: String, value: String?)

    // UI
    fun show(context: Context)
}
```

## Requirements

- **Min SDK**: 24 (Android 7.0)
- **Kotlin**: 2.0+
- **Jetpack Compose** (Material3)
- **OkHttp** 4.x (for interceptor)

## License

MIT
