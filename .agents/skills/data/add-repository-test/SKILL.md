---
name: add-repository-test
description: >-
  Unit-test a repository with a hand-written fake data source. Use when asked to
  "test a repository", "add repository tests", or to verify a [Verb][Name]Repository
  maps RemoteResult into DataResult correctly.
---

# Test a repository

A single-unit repository takes a data source, so a test hands it a **fake** that
returns canned `RemoteResult` — no network, no MockEngine, no Koin. We write
fakes by hand (not a mocking framework) so tests stay clear and refactor-proof.

## Prerequisite

`:data:core` needs `kotlinx-coroutines-test` (for `runTest`); JUnit comes from
the library convention plugin. Add via `add-dependency` if missing.

## Steps

### 1. A fake data source

The data source's **parent contract is public** and its children are
`fun interface`s — so a fake just exposes them as overridable `val`s with safe
defaults, and a test stubs only the one it needs:

```kotlin
class FakeCharacterRemoteDatasource(
    override val getCharacter: GetCharacterRemoteDatasource =
        GetCharacterRemoteDatasource { RemoteResult.Error("not stubbed") },
    override val getCharacters: GetCharactersRemoteDatasource =
        GetCharactersRemoteDatasource { RemoteResult.Error("not stubbed") },
) : CharacterRemoteDatasource
```

(The real `CharacterRemoteDatasourceImpl` is `internal` to `:datasources:remote`,
so a consumer module fakes the public interface instead.)

### 2. Test the single-unit repository

`src/test/java/.../<feature>/GetCharacterRepositoryImplTest.kt`. Hand the impl a
fake, invoke it, assert the `RemoteResult` was mapped to `DataResult`:

```kotlin
class GetCharacterRepositoryImplTest {

    @Test
    fun `maps a Success dto to a DataResult Success model`() = runTest {
        val datasource = FakeCharacterRemoteDatasource(
            getCharacter = GetCharacterRemoteDatasource { RemoteResult.Success(CharacterDto(id = 1, name = "Rick")) },
        )
        val getCharacter = GetCharacterRepositoryImpl(datasource)

        val result = getCharacter(1)

        assertTrue(result is DataResult.Success)
        assertEquals("Rick", (result as DataResult.Success).data.name)
    }

    @Test
    fun `maps an Error through to a DataResult Error`() = runTest {
        val datasource = FakeCharacterRemoteDatasource(
            getCharacter = GetCharacterRemoteDatasource { RemoteResult.Error("boom") },
        )
        val getCharacter = GetCharacterRepositoryImpl(datasource)

        assertTrue(getCharacter(1) is DataResult.Error)
    }
}
```

### 3. Test the parent (no data source)

The parent repository is just a holder, and each child is a `fun interface` — so
fake them with lambdas, no data source at all:

```kotlin
val repository = CharacterRepositoryImpl(
    getCharacter = GetCharacterRepository { DataResult.Success(character) },
    getCharacters = GetCharactersRepository { DataResult.Success(characters) },
)
// assert it delegates: repository.getCharacter(1) === the faked result
```

## Verify

```bash
./gradlew :data:core:testDebugUnitTest --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Fake data source implements the public **parent** contract; children are
      `fun interface` lambdas with safe defaults.
- [ ] Single-unit repository tested for Success (mapped model) and Error.
- [ ] Parent tested by faking child repositories with lambdas (no data source).
- [ ] `:data:core:testDebugUnitTest` passes.
