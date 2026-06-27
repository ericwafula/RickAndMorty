plugins {
    alias(libs.plugins.rickandmorty.android.library.compose)
}

android {
    namespace = "com.ericwafula.rickandmorty.ui"
}

dependencies {
    // ui combines the design system with shared UI state, so it maps the data
    // layer's DataResult into ViewState/ViewListState.
    implementation(project(":data:core"))
    // ObserveAsEvents: lifecycle-aware collection of one-time events.
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
    // Downloadable Google Fonts (Space Grotesk + IBM Plex Mono) for RickTheme.
    implementation(libs.androidx.compose.ui.text.google.fonts)
    // Material icons used by shared components (search, chevron, retry, etc.).
    // `api` so feature screens can use the same icon set through the design system.
    api(libs.androidx.compose.material.icons.extended)
    // Coil 3 — circular character portraits in CharacterRow (AsyncImage).
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
