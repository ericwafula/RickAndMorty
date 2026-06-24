---
name: add-di-module
description: >-
  Set up Koin dependency-injection modules inside a Gradle module, following
  this project's single-responsibility + internal-children/public-parent
  convention. Use when asked to "add a di module", "set up koin", "provide a
  dependency via koin", "wire up DI", or to expose a module's graph to the app.
---

# Add a Koin DI module

DI in this project lives in a `di` package inside each Gradle module. The rules
below keep modules composable and their internals hidden from consumers.

## Conventions

- **Package:** `com.ericwafula.rickandmorty.<module-path>.di`.
- **One responsibility per Koin module.** A module provides one cohesive thing
  (e.g. the HTTP client, one data source, one repository). Do not mix unrelated
  bindings into a single `module { }`. If you're tempted to, split it.
- **Children are `internal`, the parent is `public`.** Each responsibility is an
  `internal val xxxModule = module { ... }`. One **public** aggregator module
  per Gradle module uses `includes(...)` to compose the children. Consumers wire
  **only the public parent** into `startKoin`; they never see the children.
- **Naming:** children are `<responsibility>Module` (e.g. `clientModule`); the
  public parent is named after the Gradle module (e.g. `remoteModule`).
- **Qualifiers** for `named(...)` bindings live in
  `helpers/qualifiers/Qualifiers.kt` as `internal val`s, e.g.
  `internal val IODispatcher = named("IODispatcher")`. `named(...)` is a runtime
  call, so it is `val`, never `const val`.

## Reference implementation (the remote module)

- `datasources/remote/.../di/ClientModule.kt` — internal child providing only
  the Ktor `HttpClient`.
- `datasources/remote/.../di/RemoteModule.kt` — public parent that only
  `includes(clientModule)`.
- `datasources/remote/.../helpers/qualifiers/Qualifiers.kt` — qualifier vals.

## Steps

### 1. Add an internal child module

`di/<Responsibility>Module.kt`:

```kotlin
package com.ericwafula.rickandmorty.<module-path>.di

import org.koin.dsl.module

internal val <responsibility>Module = module {
    single { /* construct the one thing this module owns */ }
    // factory { ... } for stateless, single { ... } for shared singletons
}
```

Use a `named(...)` qualifier for ambiguous types (e.g. dispatchers):

```kotlin
single(IODispatcher) { Dispatchers.IO }   // provide
get(IODispatcher)                          // resolve inside another binding
```

### 2. Compose it into the public parent

Create the parent once per Gradle module; afterwards just add an `includes` line.

```kotlin
package com.ericwafula.rickandmorty.<module-path>.di

import org.koin.dsl.module

val <module>Module = module {
    includes(<responsibility>Module)
    // includes(otherModule)  // one line per child as the module grows
}
```

### 3. Wire the public parent into the app

The app's `Application` calls `startKoin { modules(<module>Module) }`. This
requires `:app` to depend on the Gradle module (`implementation(projects...)`)
— add that dependency if it isn't there yet.

## Verify

```bash
./gradlew :<module-path>:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.) For runtime wiring, `startKoin { ... }.checkModules()` in a test confirms
the graph resolves.

## Checklist

- [ ] New binding is an `internal val <responsibility>Module` in `di/`.
- [ ] It provides exactly one responsibility (SRP).
- [ ] Composed into the module's single public parent via `includes(...)`.
- [ ] Parent stays the only `public` module; children stay `internal`.
- [ ] Any `named(...)` qualifier is an `internal val` in `helpers/qualifiers/`.
- [ ] `:<module>:compileDebugSources` succeeds.
