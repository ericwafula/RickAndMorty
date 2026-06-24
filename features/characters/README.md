# `:features:characters` — Presentation

> **Presentation has two parts: a container and a presenter.**

| Part | On Android | Role |
|---|---|---|
| **Container** | the **ViewModel** | holds State, handles Actions, emits Events |
| **Presenter** | the **Composable** | renders State, forwards Actions |

## The container's vocabulary

- **State** — one immutable holder, exposed as a `StateFlow`.
- **Action** — a UI intent, in through `onAction(...)`.
- **Event** — a one-time side effect (navigate, snackbar), out through a `Channel`.

## The presenter's surface

```
CharactersRoute   ← public: the only entry point; the app wires it into navigation
└── CharactersScreen    ← internal: stateless layout
    └── CharactersContent   ← internal: pure rendering
```

Only **`CharactersRoute`** is public. The ViewModel, Screen, Content, and the
State/Action/Event types are all `internal` — the feature exposes one composable
and nothing else.

## Depends on

[domain](../../data/domain/README.md) (use cases) ·
[data](../../data/core/README.md) (repositories) ·
[ui](../../ui/README.md) (components + UI state).
