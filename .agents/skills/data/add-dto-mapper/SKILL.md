---
name: add-dto-mapper
description: >-
  Add a mapper that converts a remote DTO into a data-layer model. Use when
  asked to "map a dto", "add a mapper", "convert <X>Dto to a model", or whenever
  a data source's DTO needs turning into the data layer's own model type.
---

# Add a DTO → model mapper

The mapper is the boundary that decouples the data layer's models from the wire
format. Past the mapper, nothing knows about DTOs — so a JSON/field change on a
DTO ripples no further than its mapper. Scope this skill to one concept's mapper.

## Where things live

| Thing | Module | Package | Notes |
| --- | --- | --- | --- |
| DTO (wire format) | `:datasources:remote` | `...remote.dto` | `@Serializable`, mirrors the JSON |
| Data model | `:data:core` | `...data.model` | plain `data class`, no serialization annotations |
| Mapper | `:data:core` | `...data.mappers` | `internal` extension fun `Dto.toData()` |

`:data:core` depends on `:datasources:remote`, so the mapper can see the DTO; the DTO
never depends on the model (the dependency points one way, inward).

## Steps

### 1. DTO (already exists, or add it)

In `:datasources:remote`, `...remote.dto` — see the `add-remote-datasource`
skill:

```kotlin
@Serializable
data class CharacterDto(val id: Int, val name: String)
```

### 2. Data model

`:data:core`, `...data.model/Character.kt` — the data layer's own type, free of any
wire concerns:

```kotlin
data class Character(val id: Int, val name: String)
```

### 3. Mapper

`:data:core`, `...data.mappers/CharacterMapper.kt` — one extension function per
concept. **Always name it `toData()`** — every DTO → model mapper follows this
one convention, so the call site reads the same everywhere (`dto.toData()`):

```kotlin
internal fun CharacterDto.toData() = Character(
    id = id,
    name = name,
)
```

Keep mapping logic total and pure: provide defaults for nullable/absent DTO
fields here rather than leaking nullability into the model.

### 4. Use it at the result boundary

In the repository, feed the mapper into `toDataResult` (see `add-repository`):

```kotlin
remoteDataSource.getCharacter(id).toDataResult { it.toData() }
```

## Verify

```bash
./gradlew :data:core:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] DTO is in `...remote.dto` and `@Serializable`.
- [ ] Data model is a plain `data class` in `...data.model`.
- [ ] Mapper is an `internal` extension fun named `toData()` in `...data.mappers`,
      one per concept.
- [ ] Mapping is total/pure — no nullable leakage into the model.
- [ ] `:data:core:compileDebugSources` succeeds.
