package com.ericwafula.rickandmorty.datasources.remote.di

import com.ericwafula.rickandmorty.datasources.remote.helpers.createHttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

/**
 * Provides the Ktor [io.ktor.client.HttpClient] for the remote data sources — and
 * nothing else. Builds it from the production OkHttp engine via the shared
 * [createHttpClient] factory; tests reuse that same factory with a MockEngine.
 * Kept internal so consumers depend on [remoteDatasourceModule] rather than this directly.
 */
internal val clientModule = module {
    single { createHttpClient(OkHttp.create()) }
}
