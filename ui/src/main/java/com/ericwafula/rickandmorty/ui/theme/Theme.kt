package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * RickTheme — the app's design system entry point.
 *
 * Wrap your app (or a preview) in `RickTheme { … }`. Inside, read tokens via:
 *   • MaterialTheme.colorScheme.*   — standard Material roles (primary, surface…)
 *   • RickTheme.colors.*            — semantic tokens Material doesn't model
 *                                     (status colors, text emphasis levels, portal variants)
 *   • RickTheme.type.*              — mono text styles (overline, caption, dataKey, code)
 *   • Spacing.* / Dimens.*          — static layout values
 *
 * The design is dark-only by intent; there is no light scheme.
 */

@Immutable
data class RickColors(
    // Surfaces
    val screen: Color,
    val card: Color,
    val elevated: Color,
    val placeholder: Color,
    val shimmerBase: Color,
    val shimmerHighlight: Color,
    // Text emphasis
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val textFaint: Color,
    // Portal accent family
    val portal: Color,
    val portalBright: Color,
    val portalGlow: Color,
    val portalDeep: Color,
    val portalMuted: Color,
    val onPortal: Color,
    // Status
    val statusAlive: Color,
    val statusDead: Color,
    val statusUnknown: Color,
    // Lines
    val hairlineWeak: Color,
    val hairlineSoft: Color,
    val focusRing: Color,
)

private val DarkRickColors = RickColors(
    screen = Ink800,
    card = Surface800,
    elevated = Surface700,
    placeholder = SurfacePlaceholder,
    shimmerBase = Shimmer0,
    shimmerHighlight = Shimmer1,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    textMuted = TextMuted,
    textFaint = TextFaint,
    portal = Portal,
    portalBright = PortalBright,
    portalGlow = PortalGlow,
    portalDeep = PortalDeep,
    portalMuted = PortalMuted,
    onPortal = OnPortal,
    statusAlive = StatusAlive,
    statusDead = StatusDead,
    statusUnknown = StatusUnknown,
    hairlineWeak = HairlineWeak,
    hairlineSoft = HairlineSoft,
    focusRing = FocusRing,
)

/** Material roles mapped onto the palette (dark-only). */
private val RickColorScheme = darkColorScheme(
    primary = Portal,
    onPrimary = OnPortal,
    primaryContainer = Surface700,
    onPrimaryContainer = PortalBright,
    secondary = StatusAlive,
    onSecondary = OnPortal,
    background = Ink800,
    onBackground = TextPrimary,
    surface = Surface800,
    onSurface = TextPrimary,
    surfaceVariant = Surface700,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = HairlineSoft,
    error = StatusDead,
    onError = OnPortal,
)

private val LocalRickColors = staticCompositionLocalOf { DarkRickColors }
private val LocalRickTextStyles = staticCompositionLocalOf { RickTextStyles() }

@Composable
fun RickTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRickColors provides DarkRickColors,
        LocalRickTextStyles provides RickTextStyles(),
    ) {
        MaterialTheme(
            colorScheme = RickColorScheme,
            typography = RickTypography,
            shapes = RickShapes,
            content = content,
        )
    }
}

/** Accessor object — `RickTheme.colors` / `RickTheme.type`. */
object RickTheme {
    val colors: RickColors
        @Composable @ReadOnlyComposable get() = LocalRickColors.current
    val type: RickTextStyles
        @Composable @ReadOnlyComposable get() = LocalRickTextStyles.current
}

/**
 * Map a character's `status` string from the API to its dot color.
 * Values are "Alive" | "Dead" | "unknown" (anything else → unknown gray).
 */
@Composable
@ReadOnlyComposable
fun statusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> RickTheme.colors.statusAlive
    "dead" -> RickTheme.colors.statusDead
    else -> RickTheme.colors.statusUnknown
}
