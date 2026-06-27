package com.ericwafula.rickandmorty.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ericwafula.rickandmorty.features.characters.detail.CharacterDetailRoute

/**
 * Registers the character details screen with the nav host. Reads the character
 * id from the [Route.CharacterDetail] key and hands the feature's nav-agnostic
 * composable a back callback that pops the stack.
 */
fun EntryProviderScope<NavKey>.characterDetailRoute(
    backStack: NavBackStack<NavKey>,
) {
    entry<Route.CharacterDetail> { key ->
        CharacterDetailRoute(
            id = key.id,
            onBack = { backStack.removeLastOrNull() },
        )
    }
}
