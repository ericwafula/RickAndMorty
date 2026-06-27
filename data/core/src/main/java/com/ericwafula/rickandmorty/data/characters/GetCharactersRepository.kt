package com.ericwafula.rickandmorty.data.characters

import com.ericwafula.rickandmorty.data.helpers.DataResult
import com.ericwafula.rickandmorty.data.helpers.toDataResult
import com.ericwafula.rickandmorty.data.mappers.toData
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource

/**
 * Single-unit controller: reads one page of characters as a one-shot list. A
 * `fun interface` contract consumers depend on, plus an `internal` impl that maps
 * the data source's `RemoteResult` (and DTOs) into the data layer's [DataResult]
 * of [Character] models. [page] is null for the first page.
 *
 * The infinite-scroll list uses [GetCharactersPagingRepository] instead; this
 * one-shot read stays for non-paged callers.
 */
fun interface GetCharactersRepository {
    suspend operator fun invoke(page: Int?): DataResult<List<Character>>
}

internal class GetCharactersRepositoryImpl(
    private val remoteDatasource: CharacterRemoteDatasource,
) : GetCharactersRepository {

    override suspend fun invoke(page: Int?): DataResult<List<Character>> =
        remoteDatasource.getCharacters(page, name = null).toDataResult { response ->
            response.results.map { it.toData() }
        }
}
