---
name: add-datasource-test
description: >-
  Unit-test a remote data source with a Ktor MockEngine. Use when asked to "test
  a data source", "add datasource tests", "test the API call", or to verify a
  [Verb][Name]RemoteDatasource parses success and maps errors correctly.
---

# Test a remote data source

Single-unit data sources (`[Verb][Name]RemoteDatasource`) are built to test in
isolation. A test swaps the production HTTP engine for a Ktor `MockEngine` via
the shared factory, so it exercises the **real** JSON parsing and `expectSuccess`
behaviour against a canned response — no network, no Koin.

## What's already in place

- **`createHttpClient(engine)`** (`...remote.helpers`, main) — builds the client
  from any engine. Production passes `OkHttp.create()`; tests pass a `MockEngine`.
- **`mockHttpClient(body, status)`** (`...remote.helpers`, test) — a client whose
  `MockEngine` returns `body` with `status`, reusing `createHttpClient`.
- **Test deps** on `:datasources:remote`: `ktor-client-mock`,
  `kotlinx-coroutines-test` (JUnit comes from the library convention plugin).

If `mockHttpClient` / the deps are missing in a module, add them the same way
(see `add-dependency`).

## Steps

### 1. Test a single unit

`src/test/java/.../<feature>/GetCharacterRemoteDatasourceImplTest.kt`. Build the impl
with a `mockHttpClient` + a test dispatcher, invoke it, assert on `RemoteResult`:

```kotlin
class GetCharacterRemoteDatasourceImplTest {

    private val ioDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `returns Success with parsed dto on 200`() = runTest {
        val client = mockHttpClient("""{"id":1,"name":"Rick"}""")
        val getCharacter = GetCharacterRemoteDatasourceImpl(client, ioDispatcher)

        val result = getCharacter(1)

        assertTrue(result is RemoteResult.Success)
        assertEquals("Rick", (result as RemoteResult.Success).data.name)
    }

    @Test
    fun `returns Error on a non-2xx response`() = runTest {
        val client = mockHttpClient("Not Found", HttpStatusCode.NotFound)
        val getCharacter = GetCharacterRemoteDatasourceImpl(client, ioDispatcher)

        assertTrue(getCharacter(1) is RemoteResult.Error)
    }
}
```

The `Error` case works because the client sets `expectSuccess = true`, so a 404
throws `ClientRequestException`, which `safeApiCall` turns into
`RemoteResult.Error`.

### 2. Test the parent (no network)

The parent data source is just a holder, and each child is a `fun interface` —
so fake them with lambdas, no `MockEngine` needed:

```kotlin
val datasource = CharacterRemoteDatasourceImpl(
    getCharacter = GetCharacterRemoteDatasource { RemoteResult.Success(characterDto) },
    getCharacters = GetCharactersRemoteDatasource { RemoteResult.Success(charactersDto) },
)
// assert it delegates: datasource.getCharacter(1) === the faked result
```

This is the payoff of the single-unit shape: a unit is mocked with one lambda,
and the parent needs no HTTP at all.

## Verify

```bash
./gradlew :datasources:remote:testDebugUnitTest --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Test lives in `src/test/.../<feature>/`, named `<Impl>Test`.
- [ ] Builds the impl with `mockHttpClient(...)` + a `TestDispatcher`; no Koin.
- [ ] Covers a success (`RemoteResult.Success`, parsed DTO) and a non-2xx
      (`RemoteResult.Error`) case.
- [ ] Parent tested by faking children with `fun interface` lambdas (no engine).
- [ ] `:datasources:remote:testDebugUnitTest` passes.
