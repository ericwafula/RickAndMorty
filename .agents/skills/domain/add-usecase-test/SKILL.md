---
name: add-usecase-test
description: >-
  Unit-test a use case with hand-written fake repositories. Use when asked to
  "test a use case", "add use case tests", or to verify a use case's aggregation
  and error-propagation logic.
---

# Test a use case

A use case exists to **aggregate** repositories, so its test hands it **fakes** of
the repository contracts and asserts the aggregation — the combined result and
each error path. No Koin, no Android. (A one-shot passthrough has no use case to
test — see `add-usecase`.)

## Prerequisite

`:data:domain` needs `kotlinx-coroutines-test` (for `runTest`); JUnit comes from
the library convention plugin. Add via `add-dependency` if missing.

## Steps

### 1. Fake the repositories

A repository's **parent contract is public** and its children are `fun interface`s,
so a fake exposes them as overridable `val`s with safe defaults:

```kotlin
class FakeCharacterRepository(
    override val getCharacter: GetCharacterRepository =
        GetCharacterRepository { DataResult.Error("not stubbed") },
    override val getCharacters: GetCharactersRepository =
        GetCharactersRepository { DataResult.Error("not stubbed") },
) : CharacterRepository
```

### 2. Test the aggregation

`src/test/java/.../<feature>/GetCharacterWithEpisodesUseCaseImplTest.kt`:

```kotlin
class GetCharacterWithEpisodesUseCaseImplTest {

    @Test
    fun `combines a character with its episodes`() = runTest {
        val useCase = GetCharacterWithEpisodesUseCaseImpl(
            characterRepository = FakeCharacterRepository(
                getCharacter = GetCharacterRepository { DataResult.Success(character) },
            ),
            episodeRepository = FakeEpisodeRepository(
                getEpisodes = GetEpisodesRepository { DataResult.Success(episodes) },
            ),
        )

        val result = useCase(1)

        assertTrue(result is DataResult.Success)
        assertEquals(episodes, (result as DataResult.Success).data.episodes)
    }

    @Test
    fun `fails fast when a repository errors`() = runTest {
        val useCase = GetCharacterWithEpisodesUseCaseImpl(
            characterRepository = FakeCharacterRepository(
                getCharacter = GetCharacterRepository { DataResult.Error("boom") },
            ),
            episodeRepository = FakeEpisodeRepository(),
        )

        assertTrue(useCase(1) is DataResult.Error)
    }
}
```

The point: a use case earns a test because it has aggregation logic — cover the
combine path and **each** short-circuit/error path. The aggregation is the only
thing worth asserting here.

## Verify

```bash
./gradlew :data:domain:testDebugUnitTest --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Repositories faked via their public **parent** contracts (`fun interface`
      children with safe defaults).
- [ ] The combine path is asserted (the aggregated result).
- [ ] Each error / short-circuit path is asserted.
- [ ] `:data:domain:testDebugUnitTest` passes.
