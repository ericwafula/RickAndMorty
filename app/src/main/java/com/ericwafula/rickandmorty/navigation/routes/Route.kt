package com.ericwafula.rickandmorty.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation 3 destinations. Each screen the app can navigate to is a
 * [NavKey] here; add new destinations as the app grows. `@Serializable` on the
 * sealed hierarchy lets Navigation 3 persist the back stack across config change
 * and process death.
 */
@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Characters : Route
}
