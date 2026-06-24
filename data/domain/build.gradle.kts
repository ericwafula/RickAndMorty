plugins {
    alias(libs.plugins.rickandmorty.android.library)
}

android {
    namespace = "com.ericwafula.rickandmorty.data.domain"
}

dependencies {
    implementation(project(":data:core"))
}
