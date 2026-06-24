package plugins

import helpers.configureKotlin
import helpers.models.ExtensionType
import helpers.utils.androidTestImplementation
import helpers.utils.bundle
import helpers.utils.implementation
import helpers.utils.library
import helpers.utils.plugin
import helpers.utils.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(plugin("android-library"))
            }

            configureKotlin(ExtensionType.Library)

            dependencies {
                implementation(library("androidx-core-ktx"))
                implementation(bundle("koin"))
                testImplementation(library("junit"))
                androidTestImplementation(library("androidx-junit"))
                androidTestImplementation(library("androidx-espresso-core"))
            }
        }
    }
}
