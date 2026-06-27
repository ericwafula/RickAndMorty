package com.ericwafula.rickandmorty.navigation.routes

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ericwafula.rickandmorty.features.characters.list.CharactersRoute

/**
 * Registers the characters list screen with the nav host. One function per screen
 * keeps each destination's navigation concerns (its key and the callbacks it
 * hands to the feature's nav-agnostic composable) in a single place, called from
 * [com.ericwafula.rickandmorty.navigation.MainNavDisplay].
 */
fun EntryProviderScope<NavKey>.charactersRoute(
    backStack: NavBackStack<NavKey>,
) {
    entry<Route.Characters> {
        CharactersRoute(
            onCharacterClick = { id -> backStack.add(Route.CharacterDetail(id)) },
        )
    }
}
