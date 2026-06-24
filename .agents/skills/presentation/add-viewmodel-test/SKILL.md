---
name: add-viewmodel-test
description: >-
  Unit-test a ViewModel's Action → State → Event behaviour with fakes. Use when
  asked to "test a viewmodel", "add viewmodel tests", or to verify a screen's
  state transitions and one-time events.
---

# Test a ViewModel

Test a ViewModel by faking its use case / repository, dispatching **actions**, and
asserting the resulting **state** and **events**. `viewModelScope` runs on
`Dispatchers.Main`, so a test must swap Main for a `TestDispatcher`.

## Prerequisite

`:features:<feature>` needs `kotlinx-coroutines-test`; JUnit comes from the
library convention plugin. Add via `add-dependency` if missing.

## A reusable Main-dispatcher rule

`viewModelScope` dispatches on `Dispatchers.Main`; redirect it for tests:

```kotlin
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) { Dispatchers.setMain(dispatcher) }
    override fun finished(description: Description) { Dispatchers.resetMain() }
}
```

(Keep it in the feature's test source set, or a shared test module once one
exists.)

## Steps

### 1. Test state produced by an action

```kotlin
class CharactersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Refresh loads characters into a Success list state`() = runTest {
        val repository = FakeCharacterRepository(
            getCharacters = GetCharactersRepository { DataResult.Success(listOf(character)) },
        )
        val viewModel = CharactersViewModel(repository)

        viewModel.onAction(CharactersAction.Refresh)

        assertTrue(viewModel.state.value.characters is ViewListState.Success)
    }
```

### 2. Test a one-time event

Events come from a `Channel`, so collect them on `runTest`'s `backgroundScope`
(auto-cancelled) before dispatching the action:

```kotlin
    @Test
    fun `CharacterClicked emits NavigateToDetail`() = runTest {
        val viewModel = CharactersViewModel(FakeCharacterRepository())
        val events = mutableListOf<CharactersEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.toList(events)
        }

        viewModel.onAction(CharactersAction.CharacterClicked(id = 1))

        assertEquals(CharactersEvent.NavigateToDetail(1), events.first())
    }
}
```

The ViewModel is `internal`, but the test is in the same module, so it can
construct it directly — no Koin.

## Verify

```bash
./gradlew :features:<feature>:testDebugUnitTest --console=plain
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] `MainDispatcherRule` redirects `Dispatchers.Main` to a `TestDispatcher`.
- [ ] Use case / repository faked via its public contract.
- [ ] State asserted via `state.value` after dispatching an action.
- [ ] Events collected on `backgroundScope` and asserted.
- [ ] `:features:<feature>:testDebugUnitTest` passes.
