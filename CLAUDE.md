# Phantom Android

Cross-platform debug toolkit for Android apps. Kotlin library with Jetpack Compose UI providing runtime debugging features: logging, network inspection, API mocking, and configuration overrides.

## Build & Test Commands

```bash
# Build library
./gradlew :phantom:assembleDebug

# Build demo app
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :phantom:test

# Clean
./gradlew clean
```

## Architecture

### Modules

- **`:phantom`** - Library module (AAR). Published via JitPack.
- **`:app`** - Demo app that seeds sample data and opens the debug panel.

### Core Pattern: Singleton Managers + StateFlow

Each concern has a singleton `object` manager exposing `StateFlow` for reactive Compose UI:

| Manager | State | Persistence |
|---------|-------|-------------|
| `PhantomLogger` | `StateFlow<List<PhantomLogItem>>` | In-memory only |
| `PhantomNetworkLogger` | `StateFlow<List<PhantomNetworkItem>>` | In-memory only |
| `PhantomMockInterceptor` | `StateFlow<List<PhantomMockRule>>` | SharedPreferences + kotlinx.serialization |
| `PhantomConfig` | `StateFlow<List<PhantomConfigEntry>>` | SharedPreferences with `phantom_config_` prefix |

### Public API

`Phantom` object is the single entry point. All methods are synchronous (fire-and-forget). `Phantom.init(context)` must be called before use.

### Thread Safety

- Suspend functions use `Mutex.withLock()` for coroutine-safe mutations.
- Sync variants (`logSync`, `logRequestSync`) exist for non-coroutine contexts.
- `PhantomMockInterceptor` and `PhantomConfig` are not coroutine-aware (single-thread SharedPreferences writes via `apply()`).

### UI

- `PhantomActivity` hosts a Compose `NavHost` with 4 screens: Logs, Network, Mock, Config.
- Theme injected via `CompositionLocalProvider` (`LocalPhantomColors`).
- `PhantomJsonTreeView` uses `org.json` (Android framework) for expandable JSON rendering.

### OkHttp Integration

`PhantomOkHttpInterceptor` implements `okhttp3.Interceptor`:
1. Checks mock rules before proceeding
2. Auto-logs request/response
3. Returns mocked response if matched

## Key Decisions

- **No Room/database**: SharedPreferences is sufficient for mock rules + config. Logs are ephemeral.
- **`org.json` in UI only**: The JSON tree view uses Android's `org.json`. The `PhantomJson` utility uses `kotlinx.serialization` so it works in unit tests.
- **Sync + Suspend API**: The public `Phantom` facade uses sync methods (no coroutine scope required). Internal managers have both variants.
- **Min SDK 24**: Matches the iOS version's iOS 15+ target (broad device coverage).

## Testing

Unit tests cover core logic only (no UI tests, no instrumented tests):
- `PhantomLoggerTest` - log ordering, levels, tags, clear
- `PhantomMockInterceptorTest` - URL/method matching, toggle, delete, response cycling
- `PhantomNetworkLoggerTest` - request/response pairing, JSON pretty printing
