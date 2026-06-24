# `:ui` — Design system + UI state

Reusable, presentation-agnostic building blocks shared by every feature's
**presenter**.

## Two things

- **Design system** — the theme (`RickAndMortyTheme`) and stateless **components**
  (buttons, cards, loaders), themed via `MaterialTheme` tokens — never raw colors.
- **UI state** (`ui.helpers`) — `ViewState` / `ViewListState`, the `DataResult` →
  UI-state mappers (`toViewState()` / `toViewListState()`), and `ObserveAsEvents`.

## The dumb-component rule

A component takes plain UI parameters and event lambdas — never a `DataResult` or a
data model. Mapping `DataResult` → `ViewState` happens in `ui.helpers` / the
container, not inside a component.

Used by every [feature](../features/characters/README.md); the design system stays
free of feature logic.
