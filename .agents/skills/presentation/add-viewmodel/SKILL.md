---
name: add-viewmodel
description: >-
  Add a ViewModel to a feature module using the Action / State / Event triad,
  depending on use case / repository interfaces. Use when asked to "add a
  viewmodel" or to wire a screen's state, actions, and events. The screen's
  composables are a separate skill (add-screen).
---

# Add a ViewModel

A feature's ViewModel lives in **its feature module** (`:features:<feature>`) —
not the app. It's registered in the feature's Koin module and rendered by a
screen (see `add-screen`).

## Visibility

The ViewModel and its State / Action / Event types are all `internal`. The
`Screen` composable registers the VM via `koinViewModel()` within the module, so
nothing outside ever names it. The screen composables (Route → Screen → Content)
are their own concern — see `add-screen`.

## The Action / State / Event triad

- **State** — one immutable `data class` holding all of the screen's state
  (content via `ViewState`/`ViewListState`, plus flags). Exposed as a read-only
  `StateFlow`; updated via `copy`.
- **Action** — a `sealed interface` of UI intents, sent through `onAction(...)`.
- **Event** — a `sealed interface` of one-time side effects via
  `Channel.receiveAsFlow()`. **Never** in State (State replays on
  recomposition/rotation, re-firing them).

## Dependencies (DIP)

- The feature module depends on `:data:core`, `:data:domain`, and `:ui`.
- The ViewModel depends on **use case interfaces** (aggregation) or **repository
  interfaces** (one-shot read). Never an impl.
- The UI-state vocabulary comes from `:ui` (`ui.helpers`): `ViewState` /
  `ViewListState`, `toViewState()` / `toViewListState()`, `ObserveAsEvents`.

## Where things live

- Feature module `:features:<feature>`, package
  `com.ericwafula.rickandmorty.features.<feature>`.
- The ViewModel is bound in the feature's internal `viewModelModule`, aggregated
  by the public `<feature>Module`; the app registers `<feature>Module` in
  `initKoin`. (`koinViewModel()` resolves it within the module, so it stays
  `internal`.)

## Steps

### 1. State, Action, Event (internal)

```kotlin
internal data class CharactersState(
    val characters: ViewListState<Character> = ViewListState.Loading,
)

internal sealed interface CharactersAction {
    data object Refresh : CharactersAction
    data class CharacterClicked(val id: Int) : CharactersAction
}

internal sealed interface CharactersEvent {
    data class NavigateToDetail(val id: Int) : CharactersEvent
    data class ShowMessage(val message: String) : CharactersEvent
}
```

### 2. ViewModel (internal)

```kotlin
internal class CharactersViewModel(
    private val characterRepository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharactersEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CharactersAction) {
        when (action) {
            CharactersAction.Refresh -> loadCharacters()
            is CharactersAction.CharacterClicked ->
                viewModelScope.launch { _events.send(CharactersEvent.NavigateToDetail(action.id)) }
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _state.update { it.copy(characters = ViewListState.Loading) }
            _state.update { it.copy(characters = characterRepository.getCharacters().toViewListState()) }
        }
    }
}
```

### 3. Bind it in the feature's viewModelModule

Replace the TODO in `features/<feature>/.../di/ViewModelModule.kt`:

```kotlin
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModelOf(::CharactersViewModel)
}
```

The public `<feature>Module` already `includes(viewModelModule)`, and the app
already registers `<feature>Module` — no app change needed.

### 4. Render it with a screen

The composables that register this ViewModel and render its state — the public
`Route`, the `internal` `Screen` (which calls `koinViewModel()`, collects
`state`, and runs `ObserveAsEvents(viewModel.events)`), and the `private`
`Content` — are their own skill: see `add-screen`.

## Verify

```bash
./gradlew :features:<feature>:assemble --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] The ViewModel and its State / Action / Event are all `internal`.
- [ ] State is one immutable `data class` over `StateFlow`; Action via
      `onAction`; Event via `Channel.receiveAsFlow()` (never in State).
- [ ] Depends on use case / repository **interfaces**, never impls.
- [ ] VM bound with `viewModelOf(::VM)` in the feature's `viewModelModule`.
- [ ] Rendered by a screen — see `add-screen`.
- [ ] `:features:<feature>:assemble` succeeds.
