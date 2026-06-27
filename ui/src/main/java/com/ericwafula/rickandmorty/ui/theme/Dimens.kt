package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Spacing scale (4dp base) and fixed component dimensions taken from the design.
 * These are static — no theming needed — so reference them directly:
 * `Modifier.padding(Spacing.lg)`, `Modifier.size(Dimens.iconButton)`.
 */
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}

object Dimens {
    // Layout
    val screenPaddingH = 18.dp   // horizontal screen gutter (app bar)
    val listPaddingH = 14.dp     // list content gutter
    val cardPadding = 12.dp      // inside a list row / card
    val listGap = 10.dp          // vertical gap between rows
    val sectionGap = 18.dp       // between empty/error blocks

    // Components
    val iconButton = 38.dp       // search / back icon button
    val avatarList = 54.dp       // circular portrait in a list row
    val avatarEgg = 96.dp        // portal easter-egg portrait
    val heroHeight = 268.dp      // details hero portrait
    val statusDot = 7.dp         // status dot diameter
    val statusDotLegend = 9.dp
    val rowDivider = 1.dp        // hairline thickness
    val focusRing = 3.dp         // search-field focus halo width
    val statusGap = 7.dp         // gap between a status dot and its label
    val avatarBorder = 1.5.dp    // ring around the list avatar
    val iconSmall = 16.dp        // inline icons (search, clear)
    val chevron = 20.dp          // row chevron / back arrow glyph
    val rowPaddingVertical = 10.dp        // list-row vertical padding
    val fieldPaddingHorizontal = 13.dp    // search field horizontal padding
    val fieldGap = 9.dp                    // search field icon ↔ text gap
    val badgePaddingHorizontal = 11.dp    // status badge pill padding
    val badgePaddingVertical = 5.dp

    // Accessibility
    val minTouchTarget = 48.dp   // never go below this for tappable rows/icons
}
