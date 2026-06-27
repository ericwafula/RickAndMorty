package com.ericwafula.rickandmorty.datasources.remote.characters

import com.ericwafula.rickandmorty.datasources.remote.characters.dto.CharacterDto
import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult
import com.ericwafula.rickandmorty.datasources.remote.helpers.Routes
import com.ericwafula.rickandmorty.datasources.remote.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Single unit: fetches one character by [id] from `GET /character/{id}`. A
 * `fun interface` behind which an `internal` impl hides the Ktor call — used by
 * the character-details repository (one-shot read, no paging).
 */
fun interface GetCharacterRemoteDatasource {
    suspend operator fun invoke(id: Int): RemoteResult<CharacterDto>
}

internal class DefaultGetCharacterRemoteDatasource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : GetCharacterRemoteDatasource {

    override suspend fun invoke(id: Int): RemoteResult<CharacterDto> =
        safeApiCall(ioDispatcher) {
            httpClient.get(Routes.Character(id).route).body()
        }
}
