---
name: add-paging-source
description: >-
  Add Paging 3 pagination to the :data:core layer — a PagingSource controller that
  loads pages from a remote data source, plus the Pager a repository exposes.
  Use when asked to "add pagination", "paginate <X>", "infinite scroll", "load
  more", or to expose a Flow<PagingData<...>> from a repository.
---

# Add a paging source

## Controllers vs. data sources

The data layer has two roles:

- **Data sources** (`:datasources:*`) — fetch raw data from *one* source
  (network, db). They just fetch; they don't decide anything.
- **Controllers** (`:data:core`) — decide *where* data comes from and orchestrate the
  sources. Repositories, WorkManager workers, and **PagingSources** are all
  controllers; each takes one or more data sources.

A `PagingSource` is the controller a repository uses **when it needs
pagination** — it turns a paged endpoint on a data source into a stream of
pages. Scope this skill to one paging source + the repository wiring around it.

## Prerequisite: the Paging dependency

The `:data:core` module needs `androidx.paging:paging-common` (pure-Kotlin Paging:
`PagingSource`, `Pager`, `PagingData`). The Android UI artifact
(`paging-compose`) belongs in the app, not here. Add it to the catalog and the
`:data:core` module — see the `add-dependency` skill:

```toml
[versions]
paging = "3.3.6"
[libraries]
androidx-paging-common = { group = "androidx.paging", name = "paging-common", version.ref = "paging" }
```

## Steps

### 1. The PagingSource controller

`:data:core`, `com.ericwafula.rickandmorty.data.<feature>`. It takes a data source
(constructor) and maps DTOs to data models (`...data.model`, via `...data.mappers`) per page:

```kotlin
internal class CharacterPagingSource(
    private val remoteDataSource: CharacterRemoteDataSource,
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        return when (val result = remoteDataSource.getCharacters(page)) {
            is RemoteResult.Success -> LoadResult.Page(
                data = result.data.results.map { it.toData() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.data.info.next != null) page + 1 else null,
            )
            // RemoteResult.Error carries a String; bridge it to the Throwable
            // LoadResult.Error expects so Paging surfaces it as a LoadState.Error.
            is RemoteResult.Error -> LoadResult.Error(Exception(result.message))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? =
        state.anchorPosition?.let { anchor ->
            val closest = state.closestPageToPosition(anchor)
            closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
        }
}
```

Note: paginated results flow as `PagingData` (errors ride along as
`LoadState.Error`), so this path does **not** wrap in `DataResult`.

### 2. The repository exposes a Pager

The paginating repository builds a `Pager`; `pagingSourceFactory` must return a
**fresh** PagingSource each invalidation:

```kotlin
override fun getCharacters(): Flow<PagingData<Character>> =
    Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { CharacterPagingSource(remoteDataSource) },
    ).flow
```

### 3. DI

The repository already comes from `repositoryModule` (see `add-repository`) and
constructs the PagingSource itself in the factory lambda — so there is usually
**nothing new to bind**. Only register the PagingSource in Koin if something
other than the repository needs it; if so use `factory { ... }` (never `single`)
so each `Pager` gets a fresh instance.

## Verify

```bash
./gradlew :data:core:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] `androidx.paging:paging-common` added to the catalog + `:data:core`.
- [ ] PagingSource is `internal`, takes a data source, maps DTO → data model.
- [ ] `RemoteResult.Error` bridged to `LoadResult.Error`; success → `LoadResult.Page`.
- [ ] Repository exposes `Flow<PagingData<Domain>>` via `Pager`, with a
      `pagingSourceFactory` that builds a fresh source.
- [ ] No `single` binding for the PagingSource (factory only, if bound at all).
- [ ] `:data:core:compileDebugSources` succeeds.
