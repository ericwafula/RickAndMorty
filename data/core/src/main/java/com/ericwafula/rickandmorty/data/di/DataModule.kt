package com.ericwafula.rickandmorty.data.di

import com.ericwafula.rickandmorty.datasources.remote.di.remoteDatasourceModule
import org.koin.dsl.module

/**
 * Public entry point for the data layer's graph.
 *
 * Aggregates the data sources it owns (currently the remote one) so the app
 * registers only `dataModule` via `startKoin` — the app never references a data
 * source module directly.
 */
val dataModule = module {
    includes(remoteDatasourceModule, repositoryModule)
}
