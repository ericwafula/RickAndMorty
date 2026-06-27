plugins {
    alias(libs.plugins.rickandmorty.android.library)
}

android {
    namespace = "com.ericwafula.rickandmorty.data"
}

dependencies {
    implementation(project(":datasources:remote"))
    // Paging 3 (pure-Kotlin): PagingSource, Pager, PagingData for the list stream.
    implementation(libs.androidx.paging.common)

    testImplementation(libs.kotlinx.coroutines.test)
}
