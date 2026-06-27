package com.ericwafula.rickandmorty.datasources.remote.helpers

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.coroutineContext

/**
 * Runs a Ktor request on [dispatcher] (off the main thread) and maps the outcome
 * to a [RemoteResult].
 *
 * [execute] performs the call and returns the decoded body, e.g.
 * `safeApiCall(ioDispatcher) { httpClient.get(URL).body<CharacterDto>() }`.
 * Relies on the client's `expectSuccess = true` so non-2xx responses surface as
 * typed exceptions and are turned into [RemoteResult.Error].
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    execute: suspend () -> T,
): RemoteResult<T> = withContext(dispatcher) {
    try {
        RemoteResult.Success(execute())
    } catch (e: UnresolvedAddressException) {
        RemoteResult.Error("No internet connection.")
    } catch (e: ClientRequestException) {
        RemoteResult.Error("Request failed (${e.response.status.value}).")
    } catch (e: ServerResponseException) {
        RemoteResult.Error("Server error (${e.response.status.value}).")
    } catch (e: RedirectResponseException) {
        RemoteResult.Error("Unexpected redirect (${e.response.status.value}).")
    } catch (e: ContentConvertException) {
        // Ktor wraps kotlinx's SerializationException when decoding the body.
        RemoteResult.Error("Failed to parse the response.")
    } catch (e: SerializationException) {
        RemoteResult.Error("Failed to parse the response.")
    } catch (e: Exception) {
        // Never swallow coroutine cancellation.
        coroutineContext.ensureActive()
        RemoteResult.Error(e.message ?: "Something went wrong.")
    }
}
