package com.ericwafula.rickandmorty.features.characters.di

import org.koin.dsl.module

/**
 * Provides this feature's ViewModels — and nothing else.
 *
 * Kept internal so consumers depend on [charactersModule] rather than this
 * directly.
 */
internal val viewModelModule = module {
    // TODO (live): bind this feature's ViewModels here, e.g.
    //  viewModelOf(::CharactersViewModel)
    // ViewModels are internal; they resolve use cases / repositories from the
    // graph the app already registers (domainModule + the data layer).
}
