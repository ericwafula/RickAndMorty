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
}
