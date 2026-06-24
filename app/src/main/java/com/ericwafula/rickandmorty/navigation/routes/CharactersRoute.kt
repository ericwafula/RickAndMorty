package com.ericwafula.rickandmorty.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Registers the characters screen with the nav host. One function per screen
 * keeps each destination's navigation concerns (its key, its arguments, and the
 * navigation callbacks it hands to the screen) in a single place, called from
 * [com.ericwafula.rickandmorty.navigation.MainNavDisplay].
 */
fun EntryProviderScope<NavKey>.charactersRoute() {
    entry<Route.Characters> {
        // TODO (live): wire the feature's public CharactersRoute composable, e.g.
        //  CharactersRoute(
        //      onCharacterClick = { id -> /* backStack.add(Route.CharacterDetail(id)) */ },
        //  )
    }
}
