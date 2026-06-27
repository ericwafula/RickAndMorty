package com.ericwafula.rickandmorty.features.characters.di

import com.ericwafula.rickandmorty.features.characters.detail.CharacterDetailViewModel
import com.ericwafula.rickandmorty.features.characters.list.CharactersViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Provides this feature's ViewModels — and nothing else.
 *
 * Kept internal so consumers depend on [charactersModule] rather than this
 * directly. ViewModels resolve their repositories from the graph the app already
 * registers (domainModule + the data layer). [CharacterDetailViewModel] also
 * takes the character id, injected per-navigation via `parametersOf`.
 */
internal val viewModelModule = module {
    viewModelOf(::CharactersViewModel)
    viewModelOf(::CharacterDetailViewModel)
}
