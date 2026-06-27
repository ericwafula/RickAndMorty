package com.ericwafula.rickandmorty.data.characters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ericwafula.rickandmorty.data.mappers.toData
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.datasources.remote.characters.CharacterRemoteDatasource
import com.ericwafula.rickandmorty.datasources.remote.helpers.RemoteResult

/**
 * Paging 3 controller that turns the paged `/character` endpoint into a stream of
 * pages. Takes the remote data source (controllers orchestrate data sources) plus
 * an optional [name] filter for search, and maps DTOs to data models per page.
 *
 * Page keys are the API's 1-based page numbers; the next key exists only while
 * the response's `info.next` is non-null. A `RemoteResult.Error` is bridged to
 * `LoadResult.Error` so Paging surfaces it as a `LoadState.Error`.
 */
internal class CharacterPagingSource(
    private val remoteDatasource: CharacterRemoteDatasource,
    private val name: String?,
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        return when (val result = remoteDatasource.getCharacters(page, name)) {
            is RemoteResult.Success -> LoadResult.Page(
                data = result.data.results.map { it.toData() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.data.info.next != null) page + 1 else null,
            )

            is RemoteResult.Error -> LoadResult.Error(Exception(result.message))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? =
        state.anchorPosition?.let { anchor ->
            val closest = state.closestPageToPosition(anchor)
            closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
        }
}
