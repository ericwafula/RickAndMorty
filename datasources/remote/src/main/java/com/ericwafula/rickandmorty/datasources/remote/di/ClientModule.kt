package com.ericwafula.rickandmorty.datasources.remote.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Provides the Ktor [HttpClient] for the remote data sources — and nothing else.
 * Kept internal so consumers depend on [remoteDatasourceModule] rather than this directly.
 */
internal val clientModule = module {
    single {
        HttpClient(OkHttp) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}
