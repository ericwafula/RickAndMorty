package com.ericwafula.rickandmorty.data.characters

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource
import kotlinx.coroutines.flow.Flow

/**
 * Single-unit controller for the infinite-scroll list: exposes a
 * `Flow<PagingData<Character>>`, optionally filtered by [name] for search. Builds
 * a [Pager] whose `pagingSourceFactory` returns a **fresh** [CharacterPagingSource]
 * on every invalidation (refresh / new query). Paged results carry their own
 * errors as `LoadState.Error`, so this path does not wrap in `DataResult`.
 */
fun interface GetCharactersPagingRepository {
    operator fun invoke(name: String?): Flow<PagingData<Character>>
}

internal class GetCharactersPagingRepositoryImpl(
    private val remoteDatasource: CharacterRemoteDatasource,
) : GetCharactersPagingRepository {

    override fun invoke(name: String?): Flow<PagingData<Character>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { CharacterPagingSource(remoteDatasource, name) },
        ).flow

    private companion object {
        // The Rick & Morty API serves a fixed 20 characters per page.
        const val PAGE_SIZE = 20
    }
}
