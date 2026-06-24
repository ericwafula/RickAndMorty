---
name: add-repository
description: >-
  Add a repository to :data:core as composable single-unit repositories
  ([Verb][Name]Repository) encapsulated by a parent contract, each mapping a data
  source's RemoteResult into DataResult. Use when asked to "add a repository",
  "wire up a repository", or to expose data-layer operations to domain/presentation.
---

# Add a repository

Like a data source, a repository is **not one class with many methods** — it is a
set of **single-unit repositories** (one operation each), encapsulated by a
**parent repository contract**. Each unit consumes the data source, maps its DTO
to a data model, and returns `DataResult`.

## Why this shape

- **Single responsibility**: one type = one operation.
- **Testability**: each unit takes a data source it can be handed a **fake** of —
  no network, no Koin (see `add-repository-test`).

## Naming & layout

- **Single unit** — `[Verb][Name]Repository` (e.g. `GetCharacterRepository`): a
  `fun interface` contract + an `internal` impl, **in the same file**. Returns
  `DataResult<Model>`.
- **Parent** — `[Feature]Repository` (e.g. `CharacterRepository`): an `interface`
  exposing each child as a `val`, + an `internal` impl that overrides them as
  constructor parameters, **in the same file**.
- Lives in `:data:core`, package `...data.<feature>`. Model in `...data.model`,
  the `toData()` mapper in `...data.mappers` (see `add-dto-mapper`).
- **DIP**: contracts are public, impls are `internal`. A unit depends on the data
  source's **parent** contract (`CharacterRemoteDatasource`) — never a single-unit
  data source or an impl.

## Steps

### 1. Data model + mapper

Add the model in `...data.model` and the `toData()` mapper in `...data.mappers`
(see `add-dto-mapper`).

### 2. Single-unit repository (contract + impl, one file)

`...data.<feature>/GetCharacterRepository.kt`:

```kotlin
fun interface GetCharacterRepository {
    suspend operator fun invoke(id: Int): DataResult<Character>
}

internal class GetCharacterRepositoryImpl(
    private val remoteDatasource: CharacterRemoteDatasource,
) : GetCharacterRepository {
    override suspend fun invoke(id: Int): DataResult<Character> =
        remoteDatasource.getCharacter(id).toDataResult { it.toData() }
}
```

One file per operation; repeat for `GetCharactersRepository`, etc.

### 3. Parent repository (contract + impl, one file)

`...data.<feature>/CharacterRepository.kt`. The impl just holds the children:

```kotlin
interface CharacterRepository {
    val getCharacter: GetCharacterRepository
    val getCharacters: GetCharactersRepository
}

internal class CharacterRepositoryImpl(
    override val getCharacter: GetCharacterRepository,
    override val getCharacters: GetCharactersRepository,
) : CharacterRepository
```

Consumers (use cases, ViewModels) depend on the parent and call a unit straight
through: `characterRepository.getCharacter(id)` — the `val` is invoked via its
`operator fun invoke`.

### 4. Bind it in repositoryModule

`:data:core` `.../data/di/RepositoryModule.kt` (replace the TODO). `singleOf`
autowires each impl's data source / children from the graph:

```kotlin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal val repositoryModule = module {
    singleOf(::GetCharacterRepositoryImpl) { bind<GetCharacterRepository>() }
    singleOf(::GetCharactersRepositoryImpl) { bind<GetCharactersRepository>() }
    singleOf(::CharacterRepositoryImpl) { bind<CharacterRepository>() }
}
```

`repositoryModule` is already included by `dataModule`.

## Verify

```bash
./gradlew :data:core:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Each operation is a `[Verb][Name]Repository` `fun interface` + `internal`
      impl, in one file, returning `DataResult<Model>`.
- [ ] Impl depends on the data source **parent** contract; maps via
      `toDataResult { it.toData() }`.
- [ ] Parent `[Feature]Repository` interface exposes children as `val`s; its
      `internal` impl overrides them as constructor params (same file).
- [ ] Units + parent bound in `repositoryModule` with `singleOf { bind<...>() }`.
- [ ] `:data:core:compileDebugSources` succeeds; tested via `add-repository-test`.
