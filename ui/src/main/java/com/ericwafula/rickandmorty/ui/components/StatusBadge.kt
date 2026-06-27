package com.ericwafula.rickandmorty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.PillShape
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing
import com.ericwafula.rickandmorty.ui.theme.statusColor

/**
 * A pill-shaped status badge — a [StatusDot] plus the capitalized status label,
 * both driven by [statusColor][com.ericwafula.rickandmorty.ui.theme.statusColor].
 * Used on the character-details hero ("Alive" / "Dead" / "unknown").
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
) {
    val color = statusColor(status)
    Row(
        modifier
            .clip(PillShape)
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.40f), PillShape)
            .padding(horizontal = Dimens.badgePaddingHorizontal, vertical = Dimens.badgePaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.statusGap),
    ) {
        StatusDot(status = status, size = Dimens.statusDot, glow = true)
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
    }
}

@Preview(name = "StatusBadge", widthDp = 220)
@Composable
private fun StatusBadgePreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                StatusBadge(status = "Alive")
                StatusBadge(status = "Dead")
                StatusBadge(status = "unknown")
            }
        }
    }
}
