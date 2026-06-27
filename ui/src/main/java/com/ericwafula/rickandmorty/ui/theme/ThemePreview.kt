package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview

/**
 * Smoke-test preview for the design system. Confirms [RickTheme] wires up the
 * Material color scheme + typography + shapes, and that the extra tokens
 * ([RickTheme.colors], [RickTheme.type], [statusColor], [Spacing], [Dimens])
 * resolve inside a composition. Not a shipped component — lives here so the
 * theme layer is verifiable on its own.
 */
@Preview(name = "RickTheme tokens", showBackground = false, widthDp = 360)
@Composable
private fun RickThemePreview() {
    RickTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text("Characters", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "CITADEL OF RICKS",
                    style = RickTheme.type.overline,
                    color = RickTheme.colors.textTertiary,
                )
                Text(
                    "Rick Sanchez",
                    style = MaterialTheme.typography.titleMedium,
                    color = RickTheme.colors.textPrimary,
                )

                listOf("Alive", "Dead", "unknown").forEach { status ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(Dimens.statusDot)
                                .clip(CircleShape)
                                .background(statusColor(status)),
                        )
                        Text(
                            "  $status",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RickTheme.colors.textSecondary,
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "RETRY",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    )
                }
            }
        }
    }
}
