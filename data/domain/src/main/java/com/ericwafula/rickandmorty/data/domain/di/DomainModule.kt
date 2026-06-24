package com.ericwafula.rickandmorty.data.domain.di

import com.ericwafula.rickandmorty.data.di.dataModule
import org.koin.dsl.module

/**
 * Public entry point for the domain layer's graph.
 *
 * The domain layer's only job is aggregation: it pulls in the data it needs from
 * core (`dataModule`) and exposes its use cases (`useCaseModule`). The app wires
 * only `domainModule` — it never references `dataModule` or a data source.
 */
val domainModule = module {
    includes(dataModule, useCaseModule)
}
