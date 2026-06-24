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
}
