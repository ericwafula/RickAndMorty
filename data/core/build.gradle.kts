plugins {
    alias(libs.plugins.rickandmorty.android.library)
}

android {
    namespace = "com.ericwafula.rickandmorty.data"
}

dependencies {
    implementation(project(":datasources:remote"))

    testImplementation(libs.kotlinx.coroutines.test)
}
