package com.ericwafula.rickandmorty.features.characters.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.ui.components.CharacterRow
import com.ericwafula.rickandmorty.ui.components.CharacterRowSkeleton
import com.ericwafula.rickandmorty.ui.components.EmptyState
import com.ericwafula.rickandmorty.ui.components.ErrorState
import com.ericwafula.rickandmorty.ui.components.SearchField
import com.ericwafula.rickandmorty.ui.helpers.ObserveAsEvents
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel

/**
 * Public nav entry for the characters list. Hoists the navigation callback so the
 * feature stays nav-agnostic, and keeps the internal ViewModel out of its
 * signature. Does not apply the theme — MainActivity themes the nav host once.
 */
@Composable
fun CharactersRoute(
    onCharacterClick: (Int) -> Unit,
) {
    CharactersScreen(onCharacterClick = onCharacterClick)
}

@Composable
internal fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val characters = viewModel.characters.collectAsLazyPagingItems()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CharactersEvent.NavigateToDetail -> onCharacterClick(event.id)
        }
    }

    CharactersContent(
        state = state,
        characters = characters,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CharactersContent(
    state: CharactersState,
    characters: LazyPagingItems<Character>,
    onAction: (CharactersAction) -> Unit,
) {
    Scaffold(containerColor = RickTheme.colors.screen) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CharactersHeader(
                query = state.searchQuery,
                resultCount = characters.itemCount,
                countExact = characters.loadState.append.endOfPaginationReached,
            )

            SearchField(
                query = state.searchQuery,
                onQueryChange = { onAction(CharactersAction.SearchChanged(it)) },
                modifier = Modifier
                    .padding(horizontal = Dimens.listPaddingH)
                    .padding(bottom = Spacing.md),
            )

            when (val refresh = characters.loadState.refresh) {
                is LoadState.Loading -> SkeletonList()

                is LoadState.Error -> ErrorState(
                    onRetry = { characters.retry() },
                    message = refresh.error.message
                        ?: "Couldn't reach the network. Check your connection and try again.",
                    code = "GET /character",
                )

                else -> if (characters.itemCount == 0) {
                    EmptyState(
                        message = if (state.searchQuery.isBlank()) {
                            "No characters to show right now."
                        } else {
                            "Nothing matched “${state.searchQuery.trim()}”. Try another name or check the spelling."
                        },
                    )
                } else {
                    CharacterList(characters = characters, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun CharactersHeader(
    query: String,
    resultCount: Int,
    countExact: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(
            horizontal = Dimens.screenPaddingH,
            vertical = Spacing.md,
        ),
    ) {
        Text(
            text = "Characters",
            style = MaterialTheme.typography.headlineMedium,
            color = RickTheme.colors.textPrimary,
        )
        if (query.isNotBlank()) {
            // itemCount is what's loaded so far; "+" until paging hits the end so
            // the number never overstates the true match count.
            val count = if (countExact) "$resultCount" else "$resultCount+"
            Text(
                text = "?name=${query.trim()} · $count results",
                style = RickTheme.type.overline,
                color = RickTheme.colors.portalMuted,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        } else {
            Text(
                text = "CITADEL OF RICKS",
                style = RickTheme.type.overline,
                color = RickTheme.colors.textMuted,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun CharacterList(
    characters: LazyPagingItems<Character>,
    onAction: (CharactersAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Dimens.listPaddingH, vertical = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Dimens.listGap),
    ) {
        items(
            count = characters.itemCount,
            key = characters.itemKey { it.id },
        ) { index ->
            val character = characters[index]
            if (character != null) {
                CharacterRow(
                    name = character.name,
                    status = character.status,
                    species = character.species,
                    location = character.location,
                    imageUrl = character.image,
                    onClick = { onAction(CharactersAction.CharacterClicked(character.id)) },
                )
            } else {
                CharacterRowSkeleton()
            }
        }

        when (characters.loadState.append) {
            is LoadState.Loading -> item { AppendLoading() }
            is LoadState.Error -> item { AppendError(onRetry = { characters.retry() }) }
            else -> Unit
        }
    }
}

@Composable
private fun SkeletonList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = Dimens.listPaddingH),
        verticalArrangement = Arrangement.spacedBy(Dimens.listGap),
    ) {
        repeat(SKELETON_COUNT) { CharacterRowSkeleton() }
    }
}

@Composable
private fun AppendLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = RickTheme.colors.portal,
            strokeWidth = 2.dp,
            modifier = Modifier.padding(Spacing.xs),
        )
    }
}

@Composable
private fun AppendError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Couldn't load more — tap to retry.",
        style = MaterialTheme.typography.bodySmall,
        color = RickTheme.colors.statusDead,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onRetry)
            .padding(Spacing.lg),
    )
}

private const val SKELETON_COUNT = 6
