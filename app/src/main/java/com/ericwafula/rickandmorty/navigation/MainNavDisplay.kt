package com.ericwafula.rickandmorty.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ericwafula.rickandmorty.navigation.routes.charactersRoute

/**
 * The app's Navigation 3 host. The [entryProvider] simply calls each screen's
 * route function, mirroring the "one function per screen" pattern — adding a
 * destination is one new `xxxRoute()` call here.
 */
@Composable
fun MainNavDisplay(
    backStack: NavBackStack<NavKey>,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider<NavKey> {
            charactersRoute()
        },
    )
}
