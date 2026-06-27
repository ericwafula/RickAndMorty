package com.ericwafula.rickandmorty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme

/**
 * One character in the list: circular portrait, name, a status line
 * ("status · species") with a colored [StatusDot], a mono "last known location"
 * sub-line, and a trailing chevron. Stateless — the caller owns the data and the
 * tap.
 */
@Composable
fun CharacterRow(
    name: String,
    status: String,
    species: String,
    location: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholderColor = RickTheme.colors.placeholder
    val placeholder = remember(placeholderColor) { ColorPainter(placeholderColor) }
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(RickTheme.colors.card)
            .border(Dimens.rowDivider, RickTheme.colors.hairlineWeak, MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .heightIn(min = Dimens.minTouchTarget)
            .padding(horizontal = Dimens.cardPadding, vertical = Dimens.rowPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.cardPadding),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            modifier = Modifier
                .size(Dimens.avatarList)
                .clip(CircleShape)
                .background(RickTheme.colors.placeholder)
                .border(Dimens.avatarBorder, RickTheme.colors.hairlineSoft, CircleShape),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = RickTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.statusGap),
            ) {
                StatusDot(status = status)
                Text(
                    text = "$status · $species",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RickTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = location,
                style = RickTheme.type.caption,
                color = RickTheme.colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = RickTheme.colors.textFaint,
            modifier = Modifier.size(Dimens.chevron),
        )
    }
}

@Preview(name = "CharacterRow", widthDp = 360)
@Composable
private fun CharacterRowPreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            Column(
                modifier = Modifier.padding(Dimens.listPaddingH),
                verticalArrangement = Arrangement.spacedBy(Dimens.listGap),
            ) {
                CharacterRow(
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    location = "Citadel of Ricks",
                    imageUrl = null,
                    onClick = {},
                )
                CharacterRow(
                    name = "Birdperson Of A Very Long Name Indeed",
                    status = "Dead",
                    species = "Bird-Person",
                    location = "Bird World",
                    imageUrl = null,
                    onClick = {},
                )
                CharacterRow(
                    name = "Mr. Poopybutthole",
                    status = "unknown",
                    species = "Cromulon",
                    location = "Interdimensional",
                    imageUrl = null,
                    onClick = {},
                )
            }
        }
    }
}
