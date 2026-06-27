package com.ericwafula.rickandmorty.features.characters.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ericwafula.rickandmorty.ui.theme.Dimens
import com.ericwafula.rickandmorty.ui.theme.RickTheme
import com.ericwafula.rickandmorty.ui.theme.Spacing
import kotlinx.coroutines.delay

/**
 * The details hero portrait, with a hidden easter egg: triple-tap it and Rick
 * spins out of a green portal and back, shouting "Wubba Lubba Dub Dub!".
 *
 * This is **pure presentation** — the [eggPlaying] flag and the whole animation
 * live here in the composable (an [Animatable] timeline + a [Canvas] portal). It
 * never touches the ViewModel, data, domain, or network layers.
 */
@Composable
internal fun HeroPortrait(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    var eggPlaying by remember { mutableStateOf(false) }
    var taps by remember { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }
    val placeholderColor = RickTheme.colors.placeholder
    val placeholder = remember(placeholderColor) { ColorPainter(placeholderColor) }

    // Triple-tap detector: three taps inside the window arm the egg; otherwise the
    // count resets. Cheap and self-contained — no gesture library needed.
    LaunchedEffect(taps) {
        when {
            taps >= 3 -> {
                taps = 0
                if (!eggPlaying) eggPlaying = true
            }

            taps in 1..2 -> {
                delay(TAP_WINDOW_MS)
                taps = 0
            }
        }
    }

    LaunchedEffect(eggPlaying) {
        if (eggPlaying) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = EGG_DURATION_MS, easing = LinearEasing))
            eggPlaying = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.heroHeight)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { taps++ })
            },
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            modifier = Modifier
                .fillMaxSize()
                .background(RickTheme.colors.placeholder),
        )

        if (eggPlaying) {
            PortalEggOverlay(
                imageUrl = imageUrl,
                progress = { progress.value },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PortalEggOverlay(
    imageUrl: String?,
    progress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val portal = RickTheme.colors.portal
    val portalBright = RickTheme.colors.portalBright
    val portalGlow = RickTheme.colors.portalGlow

    Box(
        modifier = modifier.drawBehind {
            drawRect(Color.Black.copy(alpha = 0.55f * portalVisibility(progress())))
        },
        contentAlignment = Alignment.Center,
    ) {
        // The swirling green portal.
        Canvas(
            modifier = Modifier
                .size(Dimens.heroHeight * 0.7f)
                .graphicsLayer {
                    val p = progress()
                    val s = portalScale(p)
                    scaleX = s
                    scaleY = s
                    rotationZ = p * 1080f
                    alpha = portalVisibility(p)
                },
        ) {
            val r = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(portalGlow, portal, portal.copy(alpha = 0f)),
                    center = center,
                    radius = r,
                ),
                radius = r,
            )
            val ring = listOf(portalBright, portalGlow, portal)
            repeat(6) { i ->
                drawArc(
                    color = ring[i % ring.size].copy(alpha = 0.85f),
                    startAngle = i * 60f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - r * 0.8f, center.y - r * 0.8f),
                    size = Size(r * 1.6f, r * 1.6f),
                    style = Stroke(width = r * 0.06f, cap = StrokeCap.Round),
                )
            }
        }

        // Rick spinning out of the portal and back.
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(RickTheme.colors.elevated),
            error = ColorPainter(RickTheme.colors.elevated),
            fallback = ColorPainter(RickTheme.colors.elevated),
            modifier = Modifier
                .size(Dimens.avatarEgg)
                .graphicsLayer {
                    val p = progress()
                    val s = rickScale(p)
                    scaleX = s
                    scaleY = s
                    rotationZ = p * 720f
                    alpha = rickAlpha(p)
                    translationY = -rickLift(p) * size.height
                }
                .clip(CircleShape),
        )

        // The catchphrase.
        Text(
            text = "Wubba Lubba Dub Dub!",
            style = MaterialTheme.typography.titleLarge,
            color = portalBright,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl)
                .graphicsLayer { alpha = textAlpha(progress()) },
        )
    }
}

// ── Timeline: every value is a pure function of progress (0f..1f) ────────────
private const val EGG_DURATION_MS = 2400
private const val TAP_WINDOW_MS = 600L

/** Fraction of progress between [start] and [end], clamped to 0..1. */
private fun seg(p: Float, start: Float, end: Float): Float =
    ((p - start) / (end - start)).coerceIn(0f, 1f)

private fun ease(x: Float): Float = FastOutSlowInEasing.transform(x)

private fun portalVisibility(p: Float): Float = seg(p, 0f, 0.12f) * (1f - seg(p, 0.9f, 1f))

private fun portalScale(p: Float): Float = ease(seg(p, 0f, 0.2f)) * (1f - 0.85f * seg(p, 0.85f, 1f))

private fun rickScale(p: Float): Float = ease(seg(p, 0.15f, 0.45f)) * (1f - seg(p, 0.82f, 0.96f))

private fun rickAlpha(p: Float): Float = seg(p, 0.15f, 0.25f) * (1f - seg(p, 0.85f, 0.95f))

private fun rickLift(p: Float): Float = ease(seg(p, 0.15f, 0.5f)) * 0.25f * (1f - seg(p, 0.8f, 1f))

private fun textAlpha(p: Float): Float = seg(p, 0.45f, 0.6f) * (1f - seg(p, 0.85f, 0.97f))
