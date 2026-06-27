package com.ericwafula.rickandmorty.datasources.remote.characters

import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharactersResponseDto
import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult
import com.ericwafula.rickandmorty.datasources.remote.helpers.Routes
import com.ericwafula.rickandmorty.datasources.remote.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Single unit: fetches one page of characters from `GET /character`. A
 * `fun interface` behind which an `internal` impl hides the Ktor call, so it
 * can be tested in isolation against a `MockEngine`.
 */
fun interface GetCharactersRemoteDatasource {
    suspend operator fun invoke(page: Int?): RemoteResult<CharactersResponseDto>
}

internal class DefaultGetCharactersRemoteDatasource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : GetCharactersRemoteDatasource {

    override suspend fun invoke(page: Int?): RemoteResult<CharactersResponseDto> =
        safeApiCall(ioDispatcher) {
            httpClient
                .get(Routes.Characters.route) {
                    page?.let { parameter("page", it) }
                }
                .body()
        }
}
