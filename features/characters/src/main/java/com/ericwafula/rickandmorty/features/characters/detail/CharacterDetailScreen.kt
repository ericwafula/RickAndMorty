package com.ericwafula.rickandmorty.features.characters.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwafula.rickandmorty.data.model.Character
import com.ericwafula.rickandmorty.ui.components.StatusBadge
import com.ericwafula.rickandmorty.ui.components.ErrorState
import com.ericwafula.rickandmorty.ui.helpers.ObserveAsEvents
import com.ericwafula.rickandmorty.ui.helpers.ViewState
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Public nav entry for the character details screen. Takes the [id] to load and a
 * back callback, both hoisted to the nav graph; keeps the internal ViewModel out
 * of its signature.
 */
@Composable
fun CharacterDetailRoute(
    id: Int,
    onBack: () -> Unit,
) {
    CharacterDetailScreen(id = id, onBack = onBack)
}

@Composable
internal fun CharacterDetailScreen(
    id: Int,
    onBack: () -> Unit,
    viewModel: CharacterDetailViewModel = koinViewModel { parametersOf(id) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CharacterDetailEvent.NavigateBack -> onBack()
        }
    }

    CharacterDetailContent(id = id, state = state, onAction = viewModel::onAction)
}

@Composable
private fun CharacterDetailContent(
    id: Int,
    state: CharacterDetailState,
    onAction: (CharacterDetailAction) -> Unit,
) {
    Scaffold(containerColor = RickTheme.colors.screen) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val character = state.character) {
                ViewState.Loading -> DetailLoading()

                is ViewState.Error -> ErrorState(
                    onRetry = { onAction(CharacterDetailAction.Retry) },
                    message = character.message,
                    code = "GET /character/$id",
                    modifier = Modifier.align(Alignment.Center),
                )

                is ViewState.Success -> CharacterDetail(character = character.data)
            }

            BackButton(
                onClick = { onAction(CharacterDetailAction.BackClicked) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Dimens.screenPaddingH),
            )
        }
    }
}

@Composable
private fun CharacterDetail(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HeroPortrait(
            imageUrl = character.image,
            contentDescription = character.name,
        )

        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.screenPaddingH,
                vertical = Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = character.name,
                style = MaterialTheme.typography.headlineMedium,
                color = RickTheme.colors.textPrimary,
            )
            StatusBadge(status = character.status)

            Text(
                text = "INFO",
                style = RickTheme.type.overline,
                color = RickTheme.colors.textMuted,
                modifier = Modifier.padding(top = Spacing.sm),
            )
            InfoRow(label = "SPECIES", value = character.species)
            InfoRow(label = "GENDER", value = character.gender)
            InfoRow(label = "ORIGIN", value = character.origin)
            InfoRow(label = "LOCATION", value = character.location)
            InfoRow(label = "EPISODES", value = character.episodeCount.toString(), divider = false)
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    divider: Boolean = true,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = RickTheme.type.dataKey,
                color = RickTheme.colors.textMuted,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = RickTheme.colors.textPrimary,
            )
        }
        if (divider) {
            HorizontalDivider(thickness = Dimens.rowDivider, color = RickTheme.colors.hairlineWeak)
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 48dp touch target (a11y) wrapping the 38dp visual circle.
    Box(
        modifier = modifier
            .size(Dimens.minTouchTarget)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconButton)
                .clip(CircleShape)
                .background(RickTheme.colors.elevated.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = RickTheme.colors.textPrimary,
                modifier = Modifier.size(Dimens.chevron),
            )
        }
    }
}

@Composable
private fun DetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = RickTheme.colors.portal, strokeWidth = 2.dp)
    }
}

@Preview(name = "CharacterDetail", widthDp = 360, heightDp = 720)
@Composable
private fun CharacterDetailPreview() {
    RickTheme {
        CharacterDetailContent(
            id = 1,
            state = CharacterDetailState(
                character = ViewState.Success(
                    Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = "Alive",
                        species = "Human",
                        type = "",
                        gender = "Male",
                        image = "",
                        origin = "Earth (C-137)",
                        location = "Citadel of Ricks",
                        episodeCount = 51,
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
