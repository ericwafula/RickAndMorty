package com.ericwafula.rickandmorty.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ericwafula.rickandmorty.navigation.routes.characterDetailRoute
import com.ericwafula.rickandmorty.navigation.routes.charactersRoute

/**
 * The app's Navigation 3 host. The [entryProvider] simply calls each screen's
 * route function, mirroring the "one function per screen" pattern — adding a
 * destination is one new `xxxRoute()` call here.
 *
 * The decorators scope per-entry state (scene setup is applied by NavDisplay
 * automatically):
 * - `rememberSaveableStateHolderNavEntryDecorator` preserves `rememberSaveable`
 *   UI state per entry.
 * - `rememberViewModelStoreNavEntryDecorator` scopes ViewModels (and their
 *   `SavedStateHandle`) to the entry, so `koinViewModel()` survives recomposition
 *   and is cleared when the entry is popped.
 */
@Composable
fun MainNavDisplay(
    backStack: NavBackStack<NavKey>,
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider<NavKey> {
            charactersRoute(backStack)
            characterDetailRoute(backStack)
        },
    )
}
