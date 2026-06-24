package com.ericwafula.rickandmorty.datasources.remote.di

import org.koin.dsl.module

/**
 * Public entry point for the remote data source graph.
 *
 * Aggregates the module's internal Koin modules so consumers (e.g. the data
 * layer) wire only this one via `includes(remoteDatasourceModule)`, without
 * knowing the internals.
 */
val remoteDatasourceModule = module {
    includes(clientModule, dispatcherModule)
}
