---
name: add-dependency
description: >-
  Add a library, bundle, or plugin dependency via the Gradle version catalog and
  wire it into the right place. Use when asked to "add a dependency", "add a
  library", "pull in <library>", "add <X> to the catalog", or to introduce any
  new artifact (Ktor, Koin, Navigation, etc.) to a module.
---

# Add a dependency

All dependencies go through the version catalog at `gradle/libs.versions.toml` —
never hardcode `group:name:version` in a build file. After cataloguing, wire the
dependency into the **narrowest** place that needs it.

## 1. Add to the version catalog

`gradle/libs.versions.toml` has four sections. Touch the ones you need:

```toml
[versions]
myLib = "1.2.3"                       # camelCase key

[libraries]
my-lib = { group = "com.example", name = "my-lib", version.ref = "myLib" }

[bundles]                             # optional: group artifacts used together
my-stack = ["my-lib", "my-other-lib"]

[plugins]
my-plugin = { id = "com.example.plugin", version.ref = "myLib" }
```

**Accessor rule:** dashes in a catalog key become dots in build scripts.
`androidx-navigation-compose` → `libs.androidx.navigation.compose`;
bundle `ktor` → `libs.bundles.ktor`; plugin `kotlin-serialization` →
`libs.plugins.kotlin.serialization`.

Group related artifacts into a `[bundles]` entry when they're always applied
together (see the existing `ktor` and `koin` bundles).

## 2. Wire it into the narrowest scope

Pick the smallest scope that actually needs the dependency — do **not** default
to a convention plugin.

| Who needs it | Where it goes |
| --- | --- |
| One module only | that module's `build.gradle.kts` `dependencies { }` (e.g. Ktor in `:datasources:remote`) |
| A presentation/app-only concern | `app/build.gradle.kts` (e.g. Navigation Compose — the app owns navigation) |
| Every Android library module | `AndroidLibraryConventionPlugin` (e.g. Koin core) |
| Every Compose library module | `AndroidLibraryComposeConventionPlugin` (e.g. `koin-androidx-compose`) |
| The application | `AndroidApplicationConventionPlugin` |

**Rule of thumb:** putting a dependency in a convention plugin forces it onto
*every* module using that plugin. Only do that when the dependency is genuinely
baseline for all of them. Architectural/layering concerns win over convenience —
keep presentation-only libraries out of shared plugins.

### In a module's `build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.my.lib)
    implementation(libs.bundles.my.stack)
}
```

If it ships a Gradle plugin, also add it to the `plugins { }` block:
`alias(libs.plugins.my.plugin)`.

### In a convention plugin (`build-logic/.../plugins/`)

Convention plugins use the internal helpers, not the `libs` accessor:

```kotlin
import helpers.utils.bundle
import helpers.utils.implementation
import helpers.utils.library

dependencies {
    implementation(library("my-lib"))
    implementation(bundle("my-stack"))
}
```

`library("...")` / `bundle("...")` take the catalog key with **dashes** (not the
dotted accessor). Add the matching `import helpers.utils.*` for whatever you use.

## 3. Verify

```bash
./gradlew :<module>:assemble --console=plain
# confirm it actually resolved onto the classpath:
./gradlew :<module>:dependencies --configuration debugRuntimeClasspath -q | grep <name>
```

(Set `JAVA_HOME` to a JDK 21 — e.g. an SDKMAN `*-jbr` — if the shell can't find
Java.)

## Checklist

- [ ] Version, library (and bundle/plugin if needed) added to
      `gradle/libs.versions.toml`.
- [ ] Wired into the **narrowest** scope that needs it, not a shared plugin by
      default.
- [ ] Module build files use `libs.*`; convention plugins use
      `library(...)` / `bundle(...)`.
- [ ] `:<module>:assemble` succeeds and the artifact appears on the classpath.
