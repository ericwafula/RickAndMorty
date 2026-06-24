package helpers

import helpers.utils.debugImplementation
import helpers.utils.implementation
import helpers.utils.library
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Shared Compose dependencies for every Compose-enabled module. */
fun Project.configureCompose() {
    dependencies {
        implementation(platform(library("androidx-compose-bom")))
        implementation(library("androidx-compose-ui"))
        implementation(library("androidx-compose-material3"))
        implementation(library("androidx-compose-ui-graphics"))
        implementation(library("androidx-compose-ui-tooling-preview"))
        debugImplementation(library("androidx-compose-ui-tooling"))
        debugImplementation(library("androidx-compose-ui-test-manifest"))
    }
}
