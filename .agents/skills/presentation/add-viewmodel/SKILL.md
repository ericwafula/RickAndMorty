---
name: add-viewmodel
description: >-
  Add a screen + ViewModel to a feature module using the Action / State / Event
  triad and the Route → Screen → Content structure. Use when asked to "add a
  screen", "add a viewmodel", "wire up a feature's UI", or to connect a feature
  to the domain/data layer.
---

# Add a screen + ViewModel

A feature's UI lives in **its feature module** (`:features:<feature>`, compose
library plugin) — not the app. The app only registers the feature's Koin module
and wires its `Route` into navigation.

## Visibility — only the Route is public

For every feature, exactly one composable is public; everything else is internal.

- **`<Feature>Route`** — `public`. The navigation entry point. Resolves the
  ViewModel via `koinViewModel()`, collects state, observes events (bridging them
  to navigation callbacks), and delegates to the Screen. The **only** public
  composable in the feature.
- **`<Feature>Screen`** — `internal`. Stateless: takes `state` + `onAction`,
  owns layout/scaffolding (top bar, snackbar host).
- **`<Feature>Content`** — `internal`. Pure rendering of the state.
- The **ViewModel** and its **State / Action / Event** are `internal` too.

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

### 4. Route (public) → Screen (internal) → Content (internal)

```kotlin
@Composable
fun CharactersRoute(
    onCharacterClick: (Int) -> Unit,            // nav callbacks hoisted to the nav graph
    viewModel: CharactersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CharactersEvent.NavigateToDetail -> onCharacterClick(event.id)
            is CharactersEvent.ShowMessage -> { /* snackbar */ }
        }
    }

    CharactersScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun CharactersScreen(
    state: CharactersState,
    onAction: (CharactersAction) -> Unit,
) {
    // scaffolding (top bar, snackbar host); delegates to Content
    CharactersContent(state = state, onAction = onAction)
}

@Composable
internal fun CharactersContent(
    state: CharactersState,
    onAction: (CharactersAction) -> Unit,
) {
    // pure rendering of state.characters (a ViewListState), forwarding onAction
}
```

`CharactersRoute` takes **navigation callbacks** (lambdas), so the feature never
depends on the nav graph/library — the app's `NavHost` calls
`CharactersRoute(onCharacterClick = { navController.navigate(...) })`.

## Verify

```bash
./gradlew :features:<feature>:assemble --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Only `<Feature>Route` is `public`; `Screen`, `Content`, the ViewModel, and
      State/Action/Event are all `internal`.
- [ ] `Route` resolves the VM (`koinViewModel()`), collects state, bridges events
      to nav callbacks; `Screen`/`Content` are stateless (`state` + `onAction`).
- [ ] State is one immutable `data class` over `StateFlow`; Action via
      `onAction`; Event via `Channel.receiveAsFlow()` (never in State).
- [ ] Depends on use case / repository **interfaces**, never impls.
- [ ] VM bound with `viewModelOf(::VM)` in the feature's `viewModelModule`.
- [ ] `:features:<feature>:assemble` succeeds.
