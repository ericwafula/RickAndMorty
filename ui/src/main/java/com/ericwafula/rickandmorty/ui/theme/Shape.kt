package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii from the design.
 *   extraSmall 8  → small chips / clear button
 *   small      11 → buttons, icon buttons
 *   medium     13 → search field / text inputs
 *   large      16 → cards & list rows
 *   extraLarge 28 → bottom sheets / large containers
 *
 * Not in the Material set (use directly):
 *   PillShape   → status badges  (RoundedCornerShape(50))
 *   CircleShape → avatars        (androidx.compose.foundation.shape.CircleShape)
 */
val RickShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(11.dp),
    medium = RoundedCornerShape(13.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Fully-rounded pill — status badges, filter chips. */
val PillShape = RoundedCornerShape(percent = 50)
