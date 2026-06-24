package com.ericwafula.rickandmorty.data.domain.di

import org.koin.dsl.module

/**
 * Provides the domain layer's use cases — and nothing else.
 *
 * Kept internal so consumers depend on [domainModule] rather than this directly.
 */
internal val useCaseModule = module {
    // TODO (live): bind use cases here, e.g.
    //  factoryOf(::GetCharacterUseCase)
    // Each use case pulls its repository from the graph aggregated by dataModule.
}
