package com.ericwafula.rickandmorty.datasources.remote.di

import com.ericwafula.rickandmorty.datasources.remote.helpers.qualifiers.IODispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/**
 * Provides the coroutine dispatchers used by the remote data sources — and
 * nothing else. The IO dispatcher is exposed under the [IODispatcher] qualifier
 * so consumers resolve it explicitly via `get(IODispatcher)`.
 */
internal val dispatcherModule = module {
    single(IODispatcher) { Dispatchers.IO }
}
