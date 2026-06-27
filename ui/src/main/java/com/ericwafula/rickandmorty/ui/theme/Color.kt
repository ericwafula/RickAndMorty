package com.ericwafula.rickandmorty.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw palette — the literal values pulled from the Rick & Morty UI flow design.
 * Prefer the semantic tokens in [RickColors] (Theme.kt) over these in feature code;
 * these exist so the semantic layer has one source of truth.
 *
 * Aesthetic: dark, cool near-black surfaces + a single vivid "portal green" accent.
 */

// ── Backgrounds & surfaces ───────────────────────────────────────────────
val Ink900            = Color(0xFF050607) // deepest — device screen edge / status pill
val Ink800            = Color(0xFF0B0E10) // app background (screen)
val Surface800        = Color(0xFF14181B) // cards, list rows, search field
val Surface700        = Color(0xFF1B2024) // elevated surface / pressed
val SurfacePlaceholder = Color(0xFF1D2327) // image placeholder behind AsyncImage
val Shimmer0          = Color(0xFF161B1E) // skeleton base
val Shimmer1          = Color(0xFF202829) // skeleton highlight

// ── Text (high → low emphasis) ───────────────────────────────────────────
val TextPrimary   = Color(0xFFF4F7F5) // titles, names, values
val TextSecondary = Color(0xFF9AA4A0) // status line, supporting copy
val TextTertiary  = Color(0xFF646D69) // mono sub-lines (location)
val TextMuted     = Color(0xFF5A635F) // mono overlines, counts
val TextFaint     = Color(0xFF4A534F) // error codes, lowest emphasis

// ── Portal green (brand / interactive accent) ────────────────────────────
val Portal        = Color(0xFF9BF83A) // primary accent, focus, CTAs, portal core
val PortalBright  = Color(0xFFBCFF66) // text on dark, badge label
val PortalGlow    = Color(0xFFD6FF9E) // inner glow / particle highlight
val PortalDeep    = Color(0xFF2C6B14) // accent text on light backdrops
val PortalMuted   = Color(0xFF5A8C2F) // query result captions
val OnPortal      = Color(0xFF0B0E10) // foreground ON a portal-green fill

// ── Status (character lifecycle) ─────────────────────────────────────────
val StatusAlive   = Color(0xFF5FD06A) // green dot · "Alive"
val StatusDead    = Color(0xFFFF5D6C) // red dot · "Dead"
val StatusUnknown = Color(0xFF7D8A86) // gray dot · "unknown"

// ── Hairlines / outlines (apply over surfaces) ───────────────────────────
val HairlineWeak = Color(0x0FFFFFFF) // ~6% white — row borders
val HairlineSoft = Color(0x14FFFFFF) // ~8% white — input borders
val FocusRing    = Color(0x1F9BF83A) // ~12% portal — search focus halo
