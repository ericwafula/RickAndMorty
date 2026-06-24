package com.ericwafula.rickandmorty.ui.helpers

/**
 * UI state for a list screen: a successful-but-[Empty] result is distinct from
 * a [Success] with items, alongside the in-flight ([Loading]) and failed
 * ([Error]) states.
 */
sealed interface ViewListState<out D> {
    data object Empty : ViewListState<Nothing>
    data object Loading : ViewListState<Nothing>
    data class Success<out D>(val items: List<D>) : ViewListState<D>
    data class Error(val message: String) : ViewListState<Nothing>
}
