package helpers.utils

import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

@JvmName("libraryImplementation")
internal fun DependencyHandlerScope.implementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("implementation", provider)
}

@JvmName("bundleImplementation")
internal fun DependencyHandlerScope.implementation(provider: Provider<ExternalModuleDependencyBundle>) {
    add("implementation", provider)
}

@JvmName("projectImplementation")
internal fun DependencyHandlerScope.implementation(project: ProjectDependency) {
    add("implementation", project)
}

@JvmName("debugImplementation")
internal fun DependencyHandlerScope.debugImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("debugImplementation", provider)
}

@JvmName("libraryTestImplementation")
internal fun DependencyHandlerScope.testImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("testImplementation", provider)
}

@JvmName("projectTestImplementation")
internal fun DependencyHandlerScope.testImplementation(project: ProjectDependency) {
    add("testImplementation", project)
}

@JvmName("libraryAndroidTestImplementation")
internal fun DependencyHandlerScope.androidTestImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", provider)
}

@JvmName("projectAndroidTestImplementation")
internal fun DependencyHandlerScope.androidTestImplementation(project: ProjectDependency) {
    add("androidTestImplementation", project)
}
