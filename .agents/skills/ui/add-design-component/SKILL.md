---
name: add-design-component
description: >-
  Add a reusable design-system Composable to the :ui module — stateless, themed
  via RickAndMortyTheme tokens, with a preview. Use when asked to "add a button",
  "add a shared component", "add a card/loader/empty state", or to build any
  reusable UI primitive shared across screens.
---

# Add a design-system component

`:ui` is the design system: reusable, presentation-agnostic Composables shared
across screens. Scope this skill to one component.

## What belongs in :ui (and what doesn't)

- **In:** reusable primitives — buttons, cards, loaders, error/empty states,
  shared layout scaffolding, the theme, and the `ui.helpers` toolkit (`ViewState`
  / `ViewListState`, their `DataResult` mappers `toViewState()` /
  `toViewListState()`, and `ObserveAsEvents`).
- **Out:** screen-specific UI (lives with the feature/app).
- This is a combined design-system + UI module, so `:ui` depends on `:data:core`
  for `DataResult` (the state mappers). Even so, **components stay stateless and
  dumb** — they take plain UI parameters and event lambdas, never a `DataResult`
  or a data/domain model. Mapping `DataResult` → `ViewState` happens in
  `ui.helpers`/the ViewModel, not inside a component.

## Conventions

- Package `com.ericwafula.rickandmorty.ui.components` (group into subpackages as
  it grows).
- **Stateless and dumb** — a component holds **no** state. State lives in the
  screen; it's passed down as plain values and changes come back up as event
  lambdas. No ViewModel, no `ViewState`, no domain/data types inside a component.
- **Theme tokens only** — colors, type and shapes come from `MaterialTheme`
  (`colorScheme`, `typography`, `shapes`), never hardcoded. The theme is
  `RickAndMortyTheme` (`...ui.theme`).
- A `modifier: Modifier = Modifier` parameter, placed as the first optional
  param, so callers control layout.
- Ship a `@Preview` wrapped in `RickAndMortyTheme`.

## Steps

### 1. The component

`...ui.components/PrimaryButton.kt`:

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
```

### 2. The preview

Wrap in `RickAndMortyTheme` so it renders with the design system; keep it
`private`:

```kotlin
@Preview
@Composable
private fun PrimaryButtonPreview() {
    RickAndMortyTheme {
        PrimaryButton(text = "Continue", onClick = {})
    }
}
```

Preview tooling (`ui-tooling-preview` + debug `ui-tooling`) comes from the
compose library convention plugin — no extra dependency.

## Verify

```bash
./gradlew :ui:assemble --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Lives in `:ui`, `...ui.components`, and is **stateless** (state hoisted).
- [ ] Component params are plain UI types — no `DataResult` or data/domain
      models passed into a component (map to UI state/params at the screen).
- [ ] Themed via `MaterialTheme` tokens; no hardcoded colors/sizes.
- [ ] Has a `modifier: Modifier = Modifier` param (first optional param).
- [ ] Ships a `@Preview` wrapped in `RickAndMortyTheme`.
- [ ] `:ui:assemble` succeeds.
