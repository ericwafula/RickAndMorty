---
name: add-worker
description: >-
  Add a WorkManager worker to the :data:core layer — a CoroutineWorker controller
  that orchestrates data sources for background/deferred work, injected by Koin.
  Use when asked to "add a worker", "background sync", "schedule a job",
  "periodic refresh", or "do <X> in the background".
---

# Add a WorkManager worker

## Controllers vs. data sources

A WorkManager worker is a **controller** (like a repository or a PagingSource):
it decides what background work to do and orchestrates one or more **data
sources** to do it. It lives in `:data:core`; the data sources it drives live in
`:datasources:*`. Scope this skill to one worker + its binding.

## Prerequisites: dependencies

Add via the `add-dependency` skill:

```toml
[versions]
work = "2.10.0"
[libraries]
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "work" }
koin-androidx-workmanager = { group = "io.insert-koin", name = "koin-androidx-workmanager", version.ref = "koin" }
```

- `:data:core` needs **both** (`CoroutineWorker` + `workerOf`).
- `:app` needs `koin-androidx-workmanager` (for `workManagerFactory()`).

## Steps

### 1. The worker controller

`:data:core`, `com.ericwafula.rickandmorty.data.<feature>`. First two constructor
params are always `Context` + `WorkerParameters`; everything after is injected:

```kotlin
internal class CharacterSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val remoteDataSource: CharacterRemoteDataSource,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        when (remoteDataSource.getCharacters(page = 1)) {
            is RemoteResult.Success -> Result.success()
            is RemoteResult.Error -> Result.retry()
        }
}
```

### 2. Bind it (internal module included by the parent)

`workerOf` wires the injected params from the graph. Put it in an internal
`workerModule` included by `dataModule`:

```kotlin
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

internal val workerModule = module {
    workerOf(::CharacterSyncWorker)
}
// in DataModule.kt: includes(remoteDatasourceModule, repositoryModule, workerModule)
```

### 3. Hand WorkManager creation to Koin (app, one-time)

In `initKoin` add `workManagerFactory()`:

```kotlin
startKoin {
    androidLogger()
    androidContext(this@initKoin)
    workManagerFactory()
    modules(dataModule)
}
```

Then disable WorkManager's default initializer so Koin's factory is used —
in `app/src/main/AndroidManifest.xml`:

```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="androidx.work.WorkManagerInitializer"
        tools:node="remove" />
</provider>
```

### 4. Enqueue it

From wherever the work is triggered (often a repository — another controller —
or app startup):

```kotlin
WorkManager.getInstance(context).enqueue(
    OneTimeWorkRequestBuilder<CharacterSyncWorker>().build()
)
```

## Verify

```bash
./gradlew :data:core:compileDebugSources :app:assembleDebug --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] `work-runtime-ktx` + `koin-androidx-workmanager` in `:data:core`;
      `koin-androidx-workmanager` in `:app`.
- [ ] Worker is `internal`, extends `CoroutineWorker`, injects data sources after
      the `Context, WorkerParameters` params.
- [ ] Bound with `workerOf(::Worker)` in an internal module included by
      `dataModule`.
- [ ] App calls `workManagerFactory()` in `initKoin` and removes the default
      `WorkManagerInitializer` from the manifest.
- [ ] `:data:core` compiles and `:app:assembleDebug` succeeds.
