package plugins

import helpers.configureCompose
import helpers.utils.androidLibrary
import helpers.utils.implementation
import helpers.utils.library
import helpers.utils.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(plugin("rickandmorty-android-library"))
                apply(plugin("kotlin-compose"))
            }

            androidLibrary {
                buildFeatures {
                    compose = true
                }
            }

            configureCompose()

            dependencies {
                implementation(library("koin-androidx-compose"))
            }
        }
    }
}
