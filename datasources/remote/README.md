# `:datasources:remote` — Remote data source

> **Data has two parts: a controller and a data source.**
> This module is the **data source** half.

A **data source** fetches raw data from **one** source — here, the network, over
Ktor. It just fetches; it decides nothing. Controllers
([`:data:core`](../../data/core/README.md)) orchestrate it.

## Shape

Composed of **single units** (`[Verb][Name]RemoteDatasource` — a `fun interface` +
an `internal` impl) behind a **parent contract**. One network operation per type,
so each tests in isolation against a Ktor **`MockEngine`**.

```
CharacterRemoteDatasource            ← parent contract (what controllers depend on)
├── GetCharacterRemoteDatasource     ← single unit: one call
└── GetCharactersRemoteDatasource    ← single unit: one call
```

## Inside

- **`HttpClient`** built by a shared factory — production passes OkHttp, tests pass
  a `MockEngine`, so tests exercise the real config.
- **`Routes`** — typed endpoints (`base URL + path`); no hand-written URLs.
- **`safeApiCall`** → `RemoteResult<Dto>`; DTOs are `@Serializable`.

→ next slide: [data · controllers](../../data/core/README.md)
