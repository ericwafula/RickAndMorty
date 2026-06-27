plugins {
    alias(libs.plugins.rickandmorty.android.library.compose)
}

android {
    namespace = "com.ericwafula.rickandmorty.features.characters"
}

dependencies {
    implementation(project(":data:core"))
    implementation(project(":data:domain"))
    implementation(project(":ui"))

    // Paging 3 Compose: collectAsLazyPagingItems / LazyPagingItems for the list.
    implementation(libs.androidx.paging.compose)
    // Lifecycle-aware state collection (collectAsStateWithLifecycle).
    implementation(libs.androidx.lifecycle.runtime.compose)
    // ViewModel coroutines (StateFlow, Channel, viewModelScope).
    implementation(libs.kotlinx.coroutines.core)
    // Coil 3 — the details hero portrait (AsyncImage).
    implementation(libs.coil.compose)

    testImplementation(libs.kotlinx.coroutines.test)
}
