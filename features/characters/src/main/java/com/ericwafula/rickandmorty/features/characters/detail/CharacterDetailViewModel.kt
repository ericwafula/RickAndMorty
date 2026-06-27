package com.ericwafula.rickandmorty.features.characters.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ericwafula.rickandmorty.data.characters.CharacterRepository
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.ui.helpers.ViewState
import com.ericwafula.rickandmorty.ui.helpers.toViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class CharacterDetailState(
    val character: ViewState<Character> = ViewState.Loading,
)

internal sealed interface CharacterDetailAction {
    data object Retry : CharacterDetailAction
    data object BackClicked : CharacterDetailAction
}

internal sealed interface CharacterDetailEvent {
    data object NavigateBack : CharacterDetailEvent
}

/**
 * Details ViewModel — a one-shot read straight from the [CharacterRepository]
 * (no use case, no paging). The character [id] is handed in by the nav graph via
 * Koin's `parametersOf`. Maps the repository's `DataResult` into a
 * [ViewState] of [Character]; navigation back is a one-time [CharacterDetailEvent].
 */
internal class CharacterDetailViewModel(
    private val id: Int,
    private val characterRepository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharacterDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun onAction(action: CharacterDetailAction) {
        when (action) {
            CharacterDetailAction.Retry -> load()
            CharacterDetailAction.BackClicked ->
                viewModelScope.launch { _events.send(CharacterDetailEvent.NavigateBack) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(character = ViewState.Loading) }
            _state.update { it.copy(character = characterRepository.getCharacter(id).toViewState()) }
        }
    }
}
