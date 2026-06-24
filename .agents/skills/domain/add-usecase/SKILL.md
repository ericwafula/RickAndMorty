---
name: add-usecase
description: >-
  Add one use case to the :data:domain layer — a public functional-interface
  contract with an internal impl that AGGREGATES repositories. Use when an
  operation combines multiple repositories or coordinates a multi-step flow. Do
  NOT use for a one-shot passthrough to a single repository method.
---

# Add a use case

## When to add a use case — and when NOT to

A use case exists **only to aggregate**: combine multiple repositories, coordinate
a multi-step flow, or apply domain logic that spans sources. That is its entire
reason to exist.

- **No aggregation → no use case.** If the operation is a one-shot passthrough to
  a single repository method (`repo.getCharacter(id)` and nothing more), do not
  write a use case — the consumer (a ViewModel, another use case) depends on the
  **repository interface** directly. A use case that just forwards one call is
  pure boilerplate.
- **Add one when** you genuinely combine ≥2 repositories, sequence/parallelize
  several calls, merge results, or enforce a cross-repository rule.

## Dependency inversion

- The use case is a **public `fun interface`** — the contract. Its impl is
  **`internal`**, reachable only through Koin. Consumers depend on the interface.
- The impl depends on **repository interfaces** (public, `:data:core`), never on
  a repository impl (those are `internal` — see `add-repository`).
- A `fun interface` also lets a test substitute a lambda.

## Where things live

- Contract + impl: `:data:domain`, package `...domain.usecase`.
- The aggregated result is a **domain model** in `...domain.model` — aggregation
  produces a new shape, so this is the normal case (not the exception).
- Bound in the internal `useCaseModule`, aggregated by `domainModule`.

## Steps

### 1. Domain model (the aggregated shape)

`...domain.model/CharacterWithEpisodes.kt` — composes core data models:

```kotlin
data class CharacterWithEpisodes(
    val character: Character,      // ...data.model
    val episodes: List<Episode>,  // ...data.model
)
```

### 2. Contract — public functional interface

```kotlin
fun interface GetCharacterWithEpisodesUseCase {
    suspend operator fun invoke(id: Int): DataResult<CharacterWithEpisodes>
}
```

### 3. Impl — internal, real aggregation

Depends on **two** repository interfaces and combines them:

```kotlin
internal class GetCharacterWithEpisodesUseCaseImpl(
    private val characterRepository: CharacterRepository,
    private val episodeRepository: EpisodeRepository,
) : GetCharacterWithEpisodesUseCase {
    override suspend fun invoke(id: Int): DataResult<CharacterWithEpisodes> =
        when (val character = characterRepository.getCharacter(id)) {
            is DataResult.Error -> DataResult.Error(character.message)
            is DataResult.Success ->
                when (val episodes = episodeRepository.getEpisodes(character.data.episodeIds)) {
                    is DataResult.Error -> DataResult.Error(episodes.message)
                    is DataResult.Success ->
                        DataResult.Success(CharacterWithEpisodes(character.data, episodes.data))
                }
        }
}
```

### 4. Bind it in useCaseModule

`data/domain/.../di/UseCaseModule.kt` (replace the TODO); use cases are
stateless → `factory`:

```kotlin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal val useCaseModule = module {
    factoryOf(::GetCharacterWithEpisodesUseCaseImpl) { bind<GetCharacterWithEpisodesUseCase>() }
}
```

`factoryOf(::Impl)` autowires both repository interfaces from the graph —
`domainModule` aggregates `dataModule`, which provides them.

## Verify

```bash
./gradlew :data:domain:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] **Justified by real aggregation** (≥2 repositories or multi-step
      coordination) — not a one-shot repository passthrough.
- [ ] Contract is a **public `fun interface`** with `suspend operator fun invoke`.
- [ ] Impl is `internal`, depends only on repository **interfaces**, never impls.
- [ ] Aggregated result is a domain model in `...domain.model`.
- [ ] Bound with `factoryOf(::Impl) { bind<Contract>() }` in `useCaseModule`.
- [ ] `:data:domain:compileDebugSources` succeeds.
