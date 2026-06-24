package helpers.utils

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

private val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.androidApplication(action: ApplicationExtension.() -> Unit) {
    extensions.configure(action)
}

internal fun Project.androidLibrary(action: LibraryExtension.() -> Unit) {
    extensions.configure(action)
}

internal fun Project.plugin(alias: String): String =
    libs.findPlugin(alias).get().get().pluginId

internal fun Project.library(alias: String): Provider<MinimalExternalModuleDependency> =
    libs.findLibrary(alias).get()

internal fun Project.bundle(alias: String): Provider<ExternalModuleDependencyBundle> =
    libs.findBundle(alias).get()
