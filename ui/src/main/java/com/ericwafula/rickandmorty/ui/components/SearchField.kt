package com.ericwafula.rickandmorty.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing

/**
 * A single-line search input themed for the design system: leading search icon,
 * placeholder, a clear (✕) affordance once there's a query, and a portal-green
 * focus ring (1px portal edge + a soft 3dp halo) when focused.
 *
 * The focus colors animate via [animateColorAsState] but are read inside
 * `drawWithCache`'s draw lambda, so the focus transition repaints without
 * recomposing the field each frame. Stateless — [query] is owned by the caller.
 */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search characters",
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = MaterialTheme.shapes.medium

    val ring = animateColorAsState(
        targetValue = if (focused) RickTheme.colors.focusRing else Color.Transparent,
        label = "searchFocusRing",
    )
    val edge = animateColorAsState(
        targetValue = if (focused) RickTheme.colors.portal.copy(alpha = 0.5f) else RickTheme.colors.hairlineSoft,
        label = "searchEdge",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.minTouchTarget)
            .clip(shape)
            .background(RickTheme.colors.card)
            .drawWithCache {
                val outline = shape.createOutline(size, layoutDirection, this)
                val ringWidth = Dimens.focusRing.toPx()
                val edgeWidth = 1.dp.toPx()
                onDrawBehind {
                    // Reads of ring/edge happen here (draw phase) → no recomposition.
                    drawOutline(outline, color = ring.value, style = Stroke(width = ringWidth))
                    drawOutline(outline, color = edge.value, style = Stroke(width = edgeWidth))
                }
            }
            .padding(horizontal = Dimens.fieldPaddingHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.fieldGap),
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = if (focused) RickTheme.colors.portal else RickTheme.colors.textMuted,
            modifier = Modifier.size(Dimens.iconSmall),
        )
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RickTheme.colors.textMuted,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = RickTheme.colors.textPrimary),
                cursorBrush = SolidColor(RickTheme.colors.portal),
                interactionSource = interaction,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotEmpty()) {
            // 48dp touch target around the 16dp glyph (a11y).
            Box(
                modifier = Modifier
                    .size(Dimens.minTouchTarget)
                    .clip(CircleShape)
                    .clickable { onQueryChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear search",
                    tint = RickTheme.colors.textMuted,
                    modifier = Modifier.size(Dimens.iconSmall),
                )
            }
        }
    }
}

@Preview(name = "SearchField", widthDp = 360)
@Composable
private fun SearchFieldPreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            Box(modifier = Modifier.padding(Spacing.lg)) {
                SearchField(query = "rick", onQueryChange = {})
            }
        }
    }
}
