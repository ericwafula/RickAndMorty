---
name: add-remote-datasource
description: >-
  Add one Ktor-backed remote data source to :datasources:remote — its DTOs, an
  interface, an impl using safeApiCall, and its DI binding. Use when asked to
  "add a remote data source", "add an API call", "fetch <X> from the network",
  or to expose a new remote endpoint to the data layer.
---

# Add a remote data source

A remote data source owns one cohesive set of network calls. It returns
**DTOs wrapped in `RemoteResult`** and never leaks Ktor types to callers. Scope
this skill to a single data source — one interface + impl + binding.

## Conventions

- Lives in `:datasources:remote`, package
  `com.ericwafula.rickandmorty.datasources.remote.<feature>`.
- **DTOs** are `@Serializable` (the `kotlin-serialization` plugin is already
  applied to this module) and live in the `...remote.dto` package. The data
  layer maps them to its own models — see the `add-dto-mapper` skill.
- The impl depends only on the injected `HttpClient` and the IO dispatcher
  resolved with the `IODispatcher` qualifier — never constructs its own.
- Every call goes through `safeApiCall(ioDispatcher) { ... }` (see
  `helpers/SafeApiCall.kt`) and returns `RemoteResult<Dto>`.
- Bindings go in an **internal** Koin module included by
  `remoteDatasourceModule` (alongside `clientModule`, `dispatcherModule`).

## Steps

### 1. DTOs

`...remote.dto/<Name>Dto.kt`:

```kotlin
@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    // @SerialName("...") for fields whose JSON key differs
)
```

### 2. Interface

`<feature>/<Name>RemoteDataSource.kt`:

```kotlin
interface CharacterRemoteDataSource {
    suspend fun getCharacter(id: Int): RemoteResult<CharacterDto>
}
```

### 3. Impl using safeApiCall

```kotlin
internal class CharacterRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : CharacterRemoteDataSource {
    override suspend fun getCharacter(id: Int) = safeApiCall(ioDispatcher) {
        httpClient.get("https://rickandmortyapi.com/api/character/$id").body()
    }
}
```

### 4. Bind it (internal module included by the parent)

Add to an internal `sourceModule` (create it if absent) and include that module
in `remoteDatasourceModule`:

```kotlin
internal val sourceModule = module {
    single<CharacterRemoteDataSource> {
        CharacterRemoteDataSourceImpl(get(), get(IODispatcher))
    }
}
// in RemoteDatasourceModule.kt:
// includes(clientModule, dispatcherModule, sourceModule)
```

## Verify

```bash
./gradlew :datasources:remote:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] DTOs are `@Serializable`, in the `...remote.dto` package.
- [ ] Public interface returns `RemoteResult<Dto>`; impl is `internal`.
- [ ] Impl uses injected `HttpClient` + `get(IODispatcher)`, calls via
      `safeApiCall`.
- [ ] Bound in an internal module included by `remoteDatasourceModule`.
- [ ] `:datasources:remote:compileDebugSources` succeeds.
- [ ] Consumed by a repository — see the `add-repository` skill.
