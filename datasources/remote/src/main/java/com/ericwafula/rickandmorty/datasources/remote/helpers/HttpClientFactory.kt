package com.ericwafula.rickandmorty.datasources.remote.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds the configured Ktor [HttpClient] from a supplied [engine].
 *
 * Production passes the real OkHttp engine; tests pass a `MockEngine`. Because
 * the configuration (content negotiation, JSON, `expectSuccess`) is identical,
 * a data-source test exercises the same parsing/error behaviour as the app.
 */
internal fun createHttpClient(engine: HttpClientEngine): HttpClient =
    HttpClient(engine) {
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
