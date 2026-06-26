plugins {
    alias(libs.plugins.rickandmorty.android.application)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwafula.rickandmorty"

    defaultConfig {
        applicationId = "com.ericwafula.rickandmorty"
    }
}

dependencies {
    implementation(project(":data:core"))
    implementation(project(":data:domain"))
    implementation(project(":features:characters"))
    implementation(project(":ui"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)
}