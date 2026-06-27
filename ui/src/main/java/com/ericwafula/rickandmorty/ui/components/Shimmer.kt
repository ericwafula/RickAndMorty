package com.ericwafula.rickandmorty.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing

/**
 * Drives the shimmer sweep. One shared `InfiniteTransition` per skeleton; the
 * animated value is read in the DRAW phase (see [shimmer]) so the sweep repaints
 * each frame without ever recomposing.
 */
@Composable
internal fun rememberShimmerProgress(): State<Float> {
    val transition = rememberInfiniteTransition(label = "shimmer")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )
}

/**
 * Paints an animated shimmer gradient behind this node. [progress] is read inside
 * `drawWithCache`'s draw lambda — the draw phase — so animation frames repaint
 * without triggering recomposition or relayout.
 */
internal fun Modifier.shimmer(
    progress: State<Float>,
    base: Color,
    highlight: Color,
): Modifier = drawWithCache {
    onDrawBehind {
        drawRect(
            Brush.linearGradient(
                colors = listOf(base, highlight, base),
                start = Offset(progress.value - 300f, 0f),
                end = Offset(progress.value, 0f),
            ),
        )
    }
}

/**
 * Loading placeholder that mirrors [CharacterRow]'s layout: a circular avatar
 * block plus three text bars, all shimmering from one shared animation.
 */
@Composable
fun CharacterRowSkeleton(modifier: Modifier = Modifier) {
    val progress = rememberShimmerProgress()
    val base = RickTheme.colors.shimmerBase
    val highlight = RickTheme.colors.shimmerHighlight
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(RickTheme.colors.card)
            .border(Dimens.rowDivider, RickTheme.colors.hairlineWeak, MaterialTheme.shapes.large)
            .heightIn(min = Dimens.minTouchTarget)
            .padding(horizontal = Dimens.cardPadding, vertical = Dimens.rowPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.cardPadding),
    ) {
        Box(
            Modifier
                .size(Dimens.avatarList)
                .clip(CircleShape)
                .shimmer(progress, base, highlight),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBar(progress, base, highlight, widthFraction = 0.55f, height = 13.dp)
            ShimmerBar(progress, base, highlight, widthFraction = 0.40f, height = 11.dp)
            ShimmerBar(progress, base, highlight, widthFraction = 0.50f, height = 10.dp)
        }
    }
}

@Composable
private fun ShimmerBar(
    progress: State<Float>,
    base: Color,
    highlight: Color,
    widthFraction: Float,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(MaterialTheme.shapes.small)
            .shimmer(progress, base, highlight),
    )
}

@Preview(name = "CharacterRowSkeleton", widthDp = 360)
@Composable
private fun CharacterRowSkeletonPreview() {
    RickTheme {
        Surface(color = RickTheme.colors.screen) {
            Column(
                modifier = Modifier.padding(Dimens.listPaddingH),
                verticalArrangement = Arrangement.spacedBy(Dimens.listGap),
            ) {
                repeat(4) { CharacterRowSkeleton() }
            }
        }
    }
}
