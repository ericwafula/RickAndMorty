package com.ericwafula.rickandmorty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing
import com.ericwafula.rickandmorty.ui.theme.statusColor

/**
 * A small lifecycle status dot, colored by [status] via
 * [statusColor][com.ericwafula.rickandmorty.ui.theme.statusColor]. When [glow]
 * is on it paints a soft portal-style halo behind the dot — the design's
 * `box-shadow` glow, which has no 1:1 Compose primitive.
 */
@Composable
fun StatusDot(
    status: String,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.statusDot,
    glow: Boolean = true,
) {
    val color = statusColor(status)
    Box(
        modifier
            .size(size)
            .then(
                if (glow) {
                    Modifier.drawBehind {
                        val r = this.size.minDimension * 1.4f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                                center = center,
                                radius = r,
                            ),
                            radius = r,
                        )
                    }
                } else {
                    Modifier
                },
            )
            .clip(CircleShape)
            .background(color),
    )
}

@Preview(name = "StatusDot", widthDp = 160)
@Composable
private fun StatusDotPreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusDot(status = "Alive", size = 12.dp)
                StatusDot(status = "Dead", size = 12.dp)
                StatusDot(status = "unknown", size = 12.dp)
            }
        }
    }
}
