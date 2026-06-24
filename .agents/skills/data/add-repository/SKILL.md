---
name: add-repository
description: >-
  Add one repository to the :data:core layer — its interface, an impl that maps a
  remote data source's RemoteResult into DataResult, and its DI binding in
  repositoryModule. Use when asked to "add a repository", "wire up a repository",
  or to expose data-layer operations to the app/domain.
---

# Add a repository

A repository is the data layer's public contract for one concept. It consumes a
remote data source, maps DTOs to data models, and returns **`DataResult`** so
callers never see a data-source type (`RemoteResult`). Scope this skill to one
repository — interface + impl + binding.

## Conventions

- Lives in `:data:core`, package `com.ericwafula.rickandmorty.data.<feature>`.
- **Dependency inversion**: the **interface** is the public contract (returns
  `DataResult<Model>`, model in `...data.model`); the **impl is `internal`** and
  reachable only through Koin. Consumers depend on the interface, never the impl —
  a use case when aggregating, or a ViewModel **directly** for a one-shot read
  that needs no aggregation (no use case in that case — see `add-usecase`).
- The impl takes a remote data source via constructor and maps results with
  `RemoteResult.toDataResult { dto -> dto.toData() }` (see `helpers/DataResult.kt`).
- The data model lives in `...data.model` and the DTO → model mapper in
  `...data.mappers` — see the `add-dto-mapper` skill.
- Bind the impl in the existing internal `repositoryModule` (already included by
  `dataModule`).

## Steps

### 1. Data model + mapper

Add the data model in `...data.model` and the DTO → model mapper in
`...data.mappers` (see the `add-dto-mapper` skill):

```kotlin
// ...data.model/Character.kt
data class Character(val id: Int, val name: String)

// ...data.mappers/CharacterMapper.kt
internal fun CharacterDto.toData() = Character(id = id, name = name)
```

### 2. Interface

`<feature>/<Name>Repository.kt`:

```kotlin
interface CharacterRepository {
    suspend fun getCharacter(id: Int): DataResult<Character>
}
```

### 3. Impl mapping RemoteResult -> DataResult

```kotlin
internal class CharacterRepositoryImpl(
    private val remoteDataSource: CharacterRemoteDataSource,
) : CharacterRepository {
    override suspend fun getCharacter(id: Int): DataResult<Character> =
        remoteDataSource.getCharacter(id).toDataResult { it.toData() }
}
```

### 4. Bind it in repositoryModule

In `:data:core` `.../data/di/RepositoryModule.kt` (replace the TODO):

```kotlin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal val repositoryModule = module {
    singleOf(::CharacterRepositoryImpl) { bind<CharacterRepository>() }
}
```

`singleOf(::Impl)` autowires constructor params from the graph — the data source
comes from `remoteDatasourceModule`, already aggregated by `dataModule`.

## Verify

```bash
./gradlew :data:core:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.) A runtime `checkModules()` test confirms the binding resolves.

## Checklist

- [ ] Public interface returns `DataResult<DomainModel>`; impl is `internal`.
- [ ] Impl consumes a remote data source (see `add-remote-datasource`) and maps
      via `toDataResult { ... }`.
- [ ] Data model in `...data.model`; DTO → model mapper in `...data.mappers`.
- [ ] Bound in `repositoryModule` with `singleOf(::Impl) { bind<Contract>() }`.
- [ ] `:data:core:compileDebugSources` succeeds.
