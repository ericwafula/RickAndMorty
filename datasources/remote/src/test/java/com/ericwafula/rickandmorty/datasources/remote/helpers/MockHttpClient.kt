package com.ericwafula.rickandmorty.datasources.remote.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * A Ktor [HttpClient] backed by a `MockEngine` that always returns [responseBody]
 * with [status]. Reuses the production [createHttpClient], so JSON parsing and
 * `expectSuccess` behave exactly as in the app — letting a single-unit data
 * source be tested in isolation with a canned response.
 */
internal fun mockHttpClient(
    responseBody: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): HttpClient = createHttpClient(
    MockEngine {
        respond(
            content = responseBody,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )
    },
)
