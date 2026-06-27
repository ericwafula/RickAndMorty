package com.ericwafula.rickandmorty.features.characters.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ericwafula.rickandmorty.data.characters.CharacterRepository
import com.ericwafula.rickandmorty.data.model.Character
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class CharactersState(
    val searchQuery: String = "",
)

internal sealed interface CharactersAction {
    data class SearchChanged(val query: String) : CharactersAction
    data class CharacterClicked(val id: Int) : CharactersAction
}

internal sealed interface CharactersEvent {
    data class NavigateToDetail(val id: Int) : CharactersEvent
}

/**
 * List ViewModel. [state] is the single source of truth for the search query
 * (the field echoes it immediately); [characters] is the paged stream the screen
 * collects via `collectAsLazyPagingItems()`, derived from that same state —
 * debounced so a new `?name=` Pager is built only after typing settles, and
 * cached in [viewModelScope] to survive config changes. Navigation is a one-time
 * [CharactersEvent].
 */
internal class CharactersViewModel(
    characterRepository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharactersEvent>()
    val events = _events.receiveAsFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<Character>> = _state
        .map { it.searchQuery.trim() }
        .debounce { query -> if (query.isBlank()) 0L else SEARCH_DEBOUNCE_MS }
        .distinctUntilChanged()
        .flatMapLatest { query -> characterRepository.getCharactersPaging(query.ifBlank { null }) }
        .cachedIn(viewModelScope)

    fun onAction(action: CharactersAction) {
        when (action) {
            is CharactersAction.SearchChanged ->
                _state.update { it.copy(searchQuery = action.query) }

            is CharactersAction.CharacterClicked ->
                viewModelScope.launch { _events.send(CharactersEvent.NavigateToDetail(action.id)) }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 350L
    }
}
