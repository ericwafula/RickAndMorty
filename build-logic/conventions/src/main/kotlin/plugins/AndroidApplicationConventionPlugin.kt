package plugins

import helpers.configureCompose
import helpers.configureKotlin
import helpers.models.ExtensionType
import helpers.utils.androidApplication
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

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(plugin("android-application"))
                apply(plugin("kotlin-compose"))
            }

            configureKotlin(ExtensionType.Application)

            androidApplication {
                buildFeatures {
                    compose = true
                }
            }

            configureCompose()

            dependencies {
                implementation(library("androidx-core-ktx"))
                implementation(library("androidx-lifecycle-runtime-ktx"))
                implementation(library("androidx-activity-compose"))
                implementation(bundle("koin"))
                implementation(library("koin-androidx-compose"))
                testImplementation(library("junit"))
                androidTestImplementation(library("androidx-junit"))
                androidTestImplementation(library("androidx-espresso-core"))
                androidTestImplementation(platform(library("androidx-compose-bom")))
                androidTestImplementation(library("androidx-compose-ui-test-junit4"))
            }
        }
    }
}
