plugins {
    alias(libs.plugins.rickandmorty.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwafula.rickandmorty.datasources.remote"
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}
