---
name: add-remote-datasource
description: >-
  Add a remote data source to :datasources:remote as composable single-unit data
  sources ([Verb][Name]RemoteDatasource) encapsulated by a parent contract. Use when
  asked to "add a remote data source", "add an API call", "fetch <X> from the
  network", or to expose a new remote endpoint to the data layer.
---

# Add a remote data source

A feature's remote data source is **not one class with many methods** — it is a
set of **single-unit data sources** (one network operation each), encapsulated by
a **parent data source contract**. Each unit is a tiny functional interface, so
it owns exactly one responsibility and tests in isolation.

## Why this shape

- **Single responsibility**: one type = one network operation. No god-object
  data source that grows a method per endpoint.
- **Testability**: each unit is a `fun interface`, so a fake is a one-line lambda
  — `GetCharacterRemoteDatasource { id -> RemoteResult.Success(dto) }`. The parent is
  just a holder, trivially built from those fakes.

## Naming & layout

- **Single unit** — `[Verb][Name]RemoteDatasource` (e.g. `GetCharacterRemoteDatasource`,
  `LoginRemoteDatasource`): a `fun interface` contract + an `internal` impl, **in the
  same file**.
- **Parent** — `[Feature]RemoteDatasource` (e.g. `CharacterRemoteDatasource`): an
  `interface` exposing each child as a `val`, + an `internal` impl that overrides
  those children as constructor parameters, **in the same file**.
- Lives in `:datasources:remote`, package `...remote.<feature>`. DTOs are
  `@Serializable` in `...remote.dto` (see `add-dto-mapper`).
- **Never hand-write a URL.** Endpoints come from the internal `Routes` sealed
  class (`...remote.helpers`): add a route (`data object` for no-arg,
  `data class` for args) and call `Routes.X.route` (base URL + path).

## Steps

### 1. Single-unit data source (contract + impl, one file)

`...remote.<feature>/GetCharacterRemoteDatasource.kt`:

```kotlin
fun interface GetCharacterRemoteDatasource {
    suspend operator fun invoke(id: Int): RemoteResult<CharacterDto>
}

internal class GetCharacterRemoteDatasourceImpl(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : GetCharacterRemoteDatasource {
    override suspend fun invoke(id: Int) = safeApiCall(ioDispatcher) {
        httpClient.get(Routes.Character(id).route).body()
    }
}
```

One file per operation; repeat for `GetCharactersRemoteDatasource`, etc.

### 2. Parent data source (contract + impl, one file)

`...remote.<feature>/CharacterRemoteDatasource.kt`. The impl just holds the
children — override each as a constructor parameter:

```kotlin
interface CharacterRemoteDatasource {
    val getCharacter: GetCharacterRemoteDatasource
    val getCharacters: GetCharactersRemoteDatasource
}

internal class CharacterRemoteDatasourceImpl(
    override val getCharacter: GetCharacterRemoteDatasource,
    override val getCharacters: GetCharactersRemoteDatasource,
) : CharacterRemoteDatasource
```

Consumers depend on the parent and call a unit straight through:
`characterRemoteDatasource.getCharacter(id)` — the `val` is invoked via its
`operator fun invoke`.

### 3. Bind it (internal module included by remoteDatasourceModule)

Each unit's impl needs `HttpClient` + `get(IODispatcher)`; the parent autowires
its children:

```kotlin
internal val sourceModule = module {
    single<GetCharacterRemoteDatasource> { GetCharacterRemoteDatasourceImpl(get(), get(IODispatcher)) }
    single<GetCharactersRemoteDatasource> { GetCharactersRemoteDatasourceImpl(get(), get(IODispatcher)) }
    singleOf(::CharacterRemoteDatasourceImpl) { bind<CharacterRemoteDatasource>() }
}
// in RemoteDatasourceModule.kt: includes(clientModule, dispatcherModule, sourceModule)
```

## Verify

```bash
./gradlew :datasources:remote:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Each operation is a `[Verb][Name]RemoteDatasource` `fun interface` + `internal`
      impl, in one file.
- [ ] Each impl uses injected `HttpClient` + `get(IODispatcher)` via
      `safeApiCall`, returns `RemoteResult<Dto>`.
- [ ] Parent `[Feature]RemoteDatasource` interface exposes children as `val`s;
      its `internal` impl overrides them as constructor params (same file).
- [ ] Units + parent bound in an internal module included by
      `remoteDatasourceModule`.
- [ ] Consumed by a repository via the parent (see `add-repository`).
- [ ] `:datasources:remote:compileDebugSources` succeeds.
