# `:app` — The host

The entry point. It owns no business logic — it just **wires the app together**.

## Responsibilities

- **Dependency injection** — `RickAndMortyApp` calls `initKoin()`, which registers
  each layer's **public** module (`domainModule`, `charactersModule`, …). The whole
  graph resolves from those.
- **Navigation** — Navigation 3 (`MainNavDisplay`): one `xxxRoute()` per screen,
  each wiring a feature's public `Route` and hoisting nav callbacks.
- **Theme** — `MainActivity` wraps `MainNavDisplay` in `RickAndMortyTheme` once,
  so every screen is themed; a screen's `Route` never re-applies it.

## The rule

The app depends on **features + domain + data contracts**, but never on a data
source or any `internal` impl. It knows *who* to wire, not *how* they work.

← start over: [the big picture](../README.md)
