package helpers

import helpers.models.ExtensionType
import helpers.utils.Versions
import helpers.utils.androidApplication
import helpers.utils.androidLibrary
import org.gradle.api.Project

/**
 * Configures the shared Android/Kotlin knobs (SDK levels, Java version) for the
 * given module type. AGP 9 supplies the Kotlin compilation via built-in Kotlin,
 * so the Kotlin JVM target follows the Java [compileOptions] set here.
 */
fun Project.configureKotlin(extensionType: ExtensionType) {
    when (extensionType) {
        ExtensionType.Application -> configureAndroidApplication()
        ExtensionType.Library -> configureAndroidLibrary()
    }
}

private fun Project.configureAndroidApplication() {
    androidApplication {
        compileSdk {
            version = release(Versions.COMPILE_SDK)
        }

        defaultConfig {
            minSdk = Versions.MIN_SDK
            targetSdk = Versions.TARGET_SDK
            versionCode = Versions.VERSION_CODE
            versionName = Versions.VERSION_NAME
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = Versions.SOURCE_COMPATIBILITY
            targetCompatibility = Versions.SOURCE_COMPATIBILITY
        }
    }
}

private fun Project.configureAndroidLibrary() {
    androidLibrary {
        compileSdk {
            version = release(Versions.COMPILE_SDK)
        }

        defaultConfig {
            minSdk = Versions.MIN_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = Versions.SOURCE_COMPATIBILITY
            targetCompatibility = Versions.SOURCE_COMPATIBILITY
        }
    }
}
