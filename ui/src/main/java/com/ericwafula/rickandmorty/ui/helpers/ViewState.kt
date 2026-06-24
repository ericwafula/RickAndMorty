package com.ericwafula.rickandmorty.ui.helpers

/**
 * UI state for a single piece of content: in flight ([Loading]), loaded
 * ([Success]), or failed ([Error]).
 */
sealed interface ViewState<out D> {
    data object Loading : ViewState<Nothing>
    data class Success<out D>(val data: D) : ViewState<D>
    data class Error(val message: String) : ViewState<Nothing>
}
