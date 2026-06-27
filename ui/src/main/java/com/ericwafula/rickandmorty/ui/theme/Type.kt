package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.ericwafula.rickandmorty.ui.R

/**
 * Type system — two families:
 *   • Space Grotesk — all UI text (titles, names, body, buttons, badges)
 *   • IBM Plex Mono — data/labels (overlines, counts, locations, error codes, eyebrows)
 *
 * Fonts are pulled at runtime via Google Fonts. If you'd rather bundle them,
 * drop the .ttf files in res/font and swap the FontFamily definitions for
 * Font(R.font.space_grotesk_bold, FontWeight.Bold), etc.
 *
 * Requires (downloadable fonts):
 *   res/values/font_certs.xml  →  com_google_android_gms_fonts_certs
 *   AndroidManifest: <meta-data android:name="preloaded_fonts" .../> (optional)
 */

private val googleProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private fun grotesk(weight: FontWeight) =
    Font(GoogleFont("Space Grotesk"), googleProvider, weight, FontStyle.Normal)

private fun mono(weight: FontWeight) =
    Font(GoogleFont("IBM Plex Mono"), googleProvider, weight, FontStyle.Normal)

val SpaceGrotesk = FontFamily(
    grotesk(FontWeight.Normal),
    grotesk(FontWeight.Medium),
    grotesk(FontWeight.SemiBold),
    grotesk(FontWeight.Bold),
)

val IbmPlexMono = FontFamily(
    mono(FontWeight.Normal),
    mono(FontWeight.Medium),
    mono(FontWeight.SemiBold),
)

/**
 * Material 3 Typography. Roles are mapped to the design's actual usages:
 *   headlineMedium → screen / hero title ("Characters", "Rick Sanchez")
 *   titleMedium    → list item name
 *   bodyMedium     → status line, empty/error copy
 *   labelLarge     → status badge & buttons
 */
val RickTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.6).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 18.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 14.sp,
    ),
)

/**
 * Mono styles that don't map to a Material role. Access via the theme:
 * `RickTheme.type.overline`, `RickTheme.type.caption`, etc. (see Theme.kt).
 */
data class RickTextStyles(
    /** Section eyebrows: "INFO", "826 CHARACTERS · 42 PAGES". */
    val overline: TextStyle = TextStyle(
        fontFamily = IbmPlexMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.5.sp,
        letterSpacing = 1.8.sp,
    ),
    /** Data sub-lines: location, "?name=rick · 12 results". */
    val caption: TextStyle = TextStyle(
        fontFamily = IbmPlexMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.2.sp,
    ),
    /** Info-panel keys: "SPECIES", "ORIGIN". */
    val dataKey: TextStyle = TextStyle(
        fontFamily = IbmPlexMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        letterSpacing = 0.8.sp,
    ),
    /** Error codes: "IOException · GET /character". */
    val code: TextStyle = TextStyle(
        fontFamily = IbmPlexMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.3.sp,
    ),
)
