---
name: add-screen
description: >-
  Add a screen's composables to a feature module using the Route → Screen →
  Content structure. Use when asked to "add a screen", "build the <X> screen",
  "wire up a screen's UI", or to render a feature's state. Pairs with add-viewmodel.
---

# Add a screen

A screen is **three composables in one file**, each with a strict job and
visibility. The ViewModel + Action/State/Event it talks to come from
`add-viewmodel`; this skill is the UI side.

## The three composables

| Composable | Visibility | Job |
|---|---|---|
| `<Feature>Route` | **public** | the nav entry: takes nav callbacks, delegates to Screen. Keeps the `internal` ViewModel out of its public signature. The only public composable. |
| `<Feature>Screen` | `internal` | registers the ViewModel, collects state + events, delegates to Content. |
| `<Feature>Content` | `private` | the UI — scaffold + `:ui` components, forwards Actions. Never sees the ViewModel. |

`Content` is `private`, so all three live in **one file**. Only the screen's
**State** carries a `ViewState` / `ViewListState`; the components it renders stay
dumb (plain params).

The theme is **not** applied here — `MainActivity` wraps the whole
`MainNavDisplay` in `RickAndMortyTheme` once, so every screen is already themed.

## Structure

```kotlin
@Composable
fun CharactersRoute(
    onCharacterClick: (Int) -> Unit,            // nav callbacks, hoisted to the nav graph
) {
    CharactersScreen(onCharacterClick = onCharacterClick)
}

@Composable
internal fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val characters = viewModel.characters.collectAsLazyPagingItems()   // paged screens only

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CharactersEvent.NavigateToDetail -> onCharacterClick(event.id)
        }
    }

    CharactersContent(
        state = state,
        characters = characters,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CharactersContent(
    state: CharactersState,
    characters: LazyPagingItems<Character>,
    onAction: (CharactersAction) -> Unit,
) {
    // scaffold + LazyColumn of CharacterCard (from :ui); forwards onAction.
    // For non-paged content, branch over a ViewState in `state`.
}
```

## Why this split

- **Route** is the only public surface — the app's nav graph calls it (see
  `add-destination`). It hoists nav callbacks so the feature never depends on the
  nav graph or library, and it keeps the `internal` ViewModel type out of the
  public signature (the `Screen`'s defaulted `viewModel` param). It does **not**
  apply the theme — `MainActivity` themes the whole `MainNavDisplay` once.
- **Screen** is where the ViewModel is registered (`koinViewModel()`) and state +
  events are collected — not the Route. `ObserveAsEvents` (from `:ui`) consumes
  one-time events and bridges them to the Route's nav callbacks.
- **Content** is `private` and pure UI: it composes `:ui` components and
  screen-private sub-composables, branches over the `ViewState` in `state`, and
  forwards every interaction through `onAction`. It never touches the ViewModel.

## Paging

A paged screen collects `viewModel.characters.collectAsLazyPagingItems()` —
needs `androidx.paging:paging-compose` on the feature (see `add-dependency`). A
non-paged screen (e.g. details) drops that line and renders `state`, whose content
is a `ViewState<Character>`.

## Verify

```bash
./gradlew :features:<feature>:assemble --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] `<Feature>Route` is `public` — takes nav callbacks, delegates to Screen.
      It does **not** wrap the theme (`MainActivity` themes `MainNavDisplay` once).
- [ ] `<Feature>Screen` is `internal` — registers the VM, collects state +
      events, delegates to Content.
- [ ] `<Feature>Content` is `private` — pure UI, forwards `onAction`, never sees
      the VM. All three composables are in one file.
- [ ] Events collected with `ObserveAsEvents`, bridged to the Route's nav callbacks.
- [ ] `:features:<feature>:assemble` succeeds.
