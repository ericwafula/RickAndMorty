package com.ericwafula.rickandmorty.ui.helpers

import com.ericwafula.rickandmorty.data.helpers.DataResult

/**
 * Maps a [DataResult] into a single-content [ViewState].
 *
 * There is no `Loading` case here — loading is set by the ViewModel before the
 * call; this maps only the resolved outcome.
 */
fun <D> DataResult<D>.toViewState(): ViewState<D> = when (this) {
    is DataResult.Success -> ViewState.Success(data)
    is DataResult.Error -> ViewState.Error(message)
}

/**
 * Maps a list-bearing [DataResult] into a [ViewListState], collapsing a
 * successful-but-empty result to [ViewListState.Empty].
 */
fun <D> DataResult<List<D>>.toViewListState(): ViewListState<D> = when (this) {
    is DataResult.Success -> if (data.isEmpty()) ViewListState.Empty else ViewListState.Success(data)
    is DataResult.Error -> ViewListState.Error(message)
}
