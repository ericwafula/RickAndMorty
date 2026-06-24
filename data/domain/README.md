# `:data:domain` — Aggregation

> **The domain's only role is aggregation.** Nothing more.

A **use case** lives here *only* when it **combines multiple controllers** or
coordinates a multi-step flow. A one-shot passthrough to a single repository is
**not** a use case — presentation talks to that repository directly.

## Shape

A use case is a public `fun interface` (`suspend operator fun invoke`) + an
`internal` impl that aggregates repositories and returns a **domain model**.

```kotlin
fun interface GetCharacterWithEpisodesUseCase {
    suspend operator fun invoke(id: Int): DataResult<CharacterWithEpisodes>
}
// impl: combines CharacterRepository + EpisodeRepository → one result
```

## Boundaries

- Depends on repository **interfaces** ([`:data:core`](../core/README.md)), never
  impls.
- **Optional** — when there's nothing to aggregate, skip it; the layer above goes
  straight to the controller.

→ next slide: [presentation (container + presenter)](../../features/characters/README.md)
