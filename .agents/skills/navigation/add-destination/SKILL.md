---
name: add-destination
description: >-
  Add a Navigation 3 destination — a route key, a per-screen route function, and
  one line in the host. Use when asked to "add a screen to navigation", "add a
  destination", "wire a screen into the nav graph", or "navigate to X".
---

# Add a navigation destination

This project uses **Navigation 3**. Navigation lives in the app's `navigation`
package; each screen is one `NavKey` route + one `xxxRoute()` function called
from the host (`MainNavDisplay`). Adding a destination is three small edits.

## Conventions

- **Route keys** live in `navigation/routes/Route.kt` — a sealed `Route : NavKey`
  where each destination is `@Serializable` (a `data object` for no-arg screens,
  a `data class` for screens with arguments).
- **Per-screen function** lives in `navigation/routes/<Screen>Route.kt`: an
  `EntryProviderScope<NavKey>` extension that registers `entry<Route.X> { }` and
  wires the feature's public `<Feature>Route` composable. It takes the
  `NavBackStack<NavKey>` so its callbacks can push/pop. One function per screen
  keeps each destination's navigation concerns in one place.
- **Host** is `navigation/MainNavDisplay.kt` — its `entryProvider` just calls
  each `xxxRoute(backStack)`. Navigate by `backStack.add(Route.X)`; go back with
  `backStack.removeLastOrNull()` (the host's `onBack`).
- **Features stay nav-agnostic.** A feature module never depends on navigation —
  the route function hands `<Feature>Route` plain callback lambdas (see
  `add-viewmodel`). Only the app knows the graph.

## Steps

### 1. Add the route key

`navigation/routes/Route.kt`:

```kotlin
sealed interface Route : NavKey {
    @Serializable data object Characters : Route
    @Serializable data class CharacterDetail(val id: Int) : Route   // args → data class
}
```

### 2. Add the per-screen route function

`navigation/routes/<Screen>Route.kt`. Takes the back stack; wires the feature's
public composable with navigation callbacks:

```kotlin
fun EntryProviderScope<NavKey>.charactersRoute(
    backStack: NavBackStack<NavKey>,
) {
    entry<Route.Characters> {
        CharactersRoute(
            onCharacterClick = { id -> backStack.add(Route.CharacterDetail(id)) },
        )
    }
}
```

### 3. Register it in the host

`navigation/MainNavDisplay.kt` — one call inside `entryProvider`:

```kotlin
entryProvider<NavKey> {
    charactersRoute(backStack)
    // characterDetailRoute(backStack)   // each new screen = one more line
}
```

The back stack itself is created once by the host's caller (e.g. `MainActivity`)
via `rememberNavBackStack(Route.Characters)` and passed into `MainNavDisplay`.

## Verify

```bash
./gradlew :app:compileDebugSources --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Route key is `@Serializable` and in `Route : NavKey` (`data object` no-arg,
      `data class` with args).
- [ ] `xxxRoute(backStack)` registers `entry<Route.X>` and wires the feature's
      `<Feature>Route` with navigation-callback lambdas.
- [ ] One `xxxRoute(backStack)` line added to `MainNavDisplay`'s `entryProvider`.
- [ ] The feature module stays nav-agnostic (no navigation dependency).
- [ ] `:app:compileDebugSources` succeeds.
