---
name: add-gradle-plugin
description: >-
  Add a new Gradle convention plugin to this project's build-logic. Use when
  asked to "create a convention plugin", "add a build-logic plugin", "add a
  gradle plugin", or to introduce a reusable module configuration (e.g. a
  feature/data/network/test convention) that modules apply by alias.
---

# Add a Gradle convention plugin

This project centralizes module configuration in **`build-logic/`** convention
plugins. Modules stay tiny — they apply one plugin by alias and declare only
what is unique to them (namespace, extra deps). Adding a new convention plugin
is always the same four steps, in order.

## Conventions in this project

- Plugin classes live in
  `build-logic/conventions/src/main/kotlin/plugins/` and are named
  `XxxConventionPlugin.kt`.
- Shared logic (SDK levels, Compose deps, Kotlin config) lives in
  `build-logic/conventions/src/main/kotlin/helpers/`. Reuse it — do not inline
  values that already exist in `helpers/utils/Versions.kt`.
- Plugin ids use the `rickandmorty.*` namespace (e.g.
  `rickandmorty.android.library`).
- Modules reference plugins through the version catalog alias
  (`libs.plugins.rickandmorty.*`), never by raw id.
- Convention plugins apply other plugins via the `plugin("alias")` helper, which
  resolves the id from the catalog.

## Steps

### 1. Write the plugin class

Create `build-logic/conventions/src/main/kotlin/plugins/<Name>ConventionPlugin.kt`.
Compose existing convention plugins and helpers rather than re-deriving config.

```kotlin
package plugins

import helpers.utils.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class <Name>ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // Build on an existing convention instead of repeating it:
                apply(plugin("rickandmorty-android-library"))
            }

            dependencies {
                // implementation(library("..."))
                // implementation(project(":..."))  // see existing convention plugins for helpers
            }
        }
    }
}
```

Reference patterns already in the repo:
- `plugins/AndroidLibraryConventionPlugin.kt` — base Android library config.
- `plugins/AndroidLibraryComposeConventionPlugin.kt` — layers Compose on top of
  another convention plugin (the composition pattern to copy).
- Helpers: `helpers/Kotlin.kt`, `helpers/Compose.kt`,
  `helpers/utils/Project.kt` (`androidLibrary`, `plugin`, `library`, `bundle`),
  `helpers/utils/Dependencies.kt` (`implementation`, `testImplementation`, …).

### 2. Register the plugin

In `build-logic/conventions/build.gradle.kts`, add a `createPlugin(...)` call
inside the `gradlePlugin { plugins { } }` block:

```kotlin
createPlugin(
    name = "RickAndMorty<Name>",
    id = "rickandmorty.<dotted.name>",
    implementationClass = "plugins.<Name>ConventionPlugin"
)
```

### 3. Add the catalog alias

In `gradle/libs.versions.toml`, under `[plugins]` in the
"locally defined (build-logic convention plugins)" section:

```toml
rickandmorty-<dashed-name> = { id = "rickandmorty.<dotted.name>", version = "unspecified" }
```

The alias dashes become dots in module DSL: `rickandmorty-foo-bar` →
`libs.plugins.rickandmorty.foo.bar`.

### 4. Apply it in a module

```kotlin
plugins {
    alias(libs.plugins.rickandmorty.<dotted.name>)
}
```

## Verify

Run a build for a module that applies the new plugin (set `JAVA_HOME` to a JDK
21 if the shell can't find Java — e.g. an SDKMAN `*-jbr` install):

```bash
./gradlew :<module-path>:assemble --console=plain
```

A clean exit confirms the plugin is registered, resolvable via the catalog, and
applies without error.

## Checklist

- [ ] Plugin class in `plugins/` named `<Name>ConventionPlugin`.
- [ ] Composes existing convention plugins / helpers (no duplicated SDK or dep
      config).
- [ ] Registered in `build-logic/conventions/build.gradle.kts`.
- [ ] Alias added under `[plugins]` in `gradle/libs.versions.toml`.
- [ ] A module applies it and `:<module>:assemble` succeeds.
