package com.ericwafula.rickandmorty.data.characters

import com.ericwafula.rickandmorty.data.helpers.DataResult
import com.ericwafula.rickandmorty.data.helpers.toDataResult
import com.ericwafula.rickandmorty.data.mappers.toData
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource

/**
 * Single-unit controller: reads one character by [id] (the details screen's
 * one-shot read — no paging, no use case). Maps the data source's `RemoteResult`
 * into a [DataResult] of the [Character] model.
 */
fun interface GetCharacterRepository {
    suspend operator fun invoke(id: Int): DataResult<Character>
}

internal class GetCharacterRepositoryImpl(
    private val remoteDatasource: CharacterRemoteDatasource,
) : GetCharacterRepository {

    override suspend fun invoke(id: Int): DataResult<Character> =
        remoteDatasource.getCharacter(id).toDataResult { it.toData() }
}
