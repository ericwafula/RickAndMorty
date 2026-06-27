package com.ericwafula.rickandmorty.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing

/**
 * Shared centered "message" scaffold for full-screen feedback states: an icon, a
 * title, a supporting line, and an optional action/detail slot. Private — callers
 * use [EmptyState] / [ErrorState].
 */
@Composable
private fun MessageState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    extras: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RickTheme.colors.textMuted,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = RickTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = RickTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        extras()
    }
}

/**
 * Empty state — e.g. a search that matched nothing. Title/message are caller-set
 * so the screen can echo the query ("Nothing matched "zxqv"…").
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "No characters found",
) {
    MessageState(
        icon = Icons.Rounded.SearchOff,
        title = title,
        message = message,
        modifier = modifier,
    )
}

/**
 * Error state with a portal-green Retry button and an optional mono [code] detail
 * line ("IOException · GET /character").
 */
@Composable
fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
    message: String = "Couldn't reach the network. Check your connection and try again.",
    code: String? = null,
) {
    MessageState(
        icon = Icons.Rounded.CloudOff,
        title = title,
        message = message,
        modifier = modifier,
    ) {
        Spacer(Modifier.size(Spacing.xs))
        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = RickTheme.colors.portal,
                contentColor = RickTheme.colors.onPortal,
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(text = "Retry", style = MaterialTheme.typography.labelLarge)
        }
        if (code != null) {
            Text(
                text = code,
                style = RickTheme.type.code,
                color = RickTheme.colors.textFaint,
            )
        }
    }
}

@Preview(name = "EmptyState", widthDp = 360, heightDp = 320)
@Composable
private fun EmptyStatePreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            EmptyState(message = "Nothing matched “zxqv”. Try another name or check the spelling.")
        }
    }
}

@Preview(name = "ErrorState", widthDp = 360, heightDp = 360)
@Composable
private fun ErrorStatePreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            ErrorState(onRetry = {}, code = "IOException · GET /character")
        }
    }
}
