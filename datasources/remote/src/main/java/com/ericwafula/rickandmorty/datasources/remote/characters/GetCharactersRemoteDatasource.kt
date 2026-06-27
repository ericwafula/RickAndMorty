package com.ericwafula.rickandmorty.datasources.remote.characters

import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharactersResponseDto
import com.ericwafula.rickandmorty.datasources.remote.characters.dto.PageInfoDto
import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult
import com.ericwafula.rickandmorty.datasources.remote.helpers.Routes
import com.ericwafula.rickandmorty.datasources.remote.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Single unit: fetches one page of characters from `GET /character`, optionally
 * filtered by [name] (`?name=`) for search. A `fun interface` behind which an
 * `internal` impl hides the Ktor call, so it can be tested in isolation against
 * a `MockEngine`.
 */
fun interface GetCharactersRemoteDatasource {
    suspend operator fun invoke(page: Int?, name: String?): RemoteResult<CharactersResponseDto>
}

internal class DefaultGetCharactersRemoteDatasource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : GetCharactersRemoteDatasource {

    override suspend fun invoke(page: Int?, name: String?): RemoteResult<CharactersResponseDto> =
        safeApiCall(ioDispatcher) {
            try {
                httpClient
                    .get(Routes.Characters.route) {
                        page?.let { parameter("page", it) }
                        name?.let { parameter("name", it) }
                    }
                    .body<CharactersResponseDto>()
            } catch (e: ClientRequestException) {
                // The Rick & Morty API answers a no-match filter with 404 (not an
                // empty list), so treat 404 as an empty page rather than an error —
                // the UI then shows the empty state, not the error state.
                if (e.response.status == HttpStatusCode.NotFound) {
                    CharactersResponseDto(
                        info = PageInfoDto(count = 0, pages = 0, next = null, prev = null),
                        results = emptyList(),
                    )
                } else {
                    throw e
                }
            }
        }
}
