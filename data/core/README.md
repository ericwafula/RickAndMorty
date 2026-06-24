# `:data:core` — Controllers

> **Data has two parts: a controller and a data source.**
> This module is the **controller** half.

A **controller** decides *where* data comes from and orchestrates one or more
**data sources** to get it. It never fetches raw bytes itself — that is the data
source's job ([`:datasources:remote`](../../datasources/remote/README.md)).

## The three controllers

| Controller | Job | Returns |
|---|---|---|
| **Repository** | the default — read one concept | `DataResult<Model>` |
| **WorkManager worker** | deferred / background work | `Result` |
| **PagingSource** | paged reads | `PagingData<Model>` |

All three take data sources; all three are controllers.

## Shape

Each controller is composed of **single units** (`[Verb][Name]Repository` — a
`fun interface` + an `internal` impl) behind a **parent contract**. One operation
per type, so each tests in isolation against a **fake** data source.

## Boundaries

- Maps a data source's DTOs → **data models** (`...data.model`) via `toData()`.
- Exposes **`DataResult`**; the data source's `RemoteResult` never leaks past here.
- Depends on a data source's **parent contract**, never an impl.

→ next slide: [domain (aggregation)](../domain/README.md)
