package com.ericwafula.rickandmorty.data.di

import org.koin.dsl.module

/**
 * Provides the data layer's repositories — and nothing else.
 *
 * Kept internal so consumers depend on [dataModule] rather than this directly.
 */
internal val repositoryModule = module {
    // TODO (live): register repositories here, e.g.
    //  singleOf(::CharacterRepositoryImpl) { bind<CharacterRepository>() }
    // Each impl pulls its remote data source + IODispatcher from the graph
    // already exposed by remoteDatasourceModule.
}
