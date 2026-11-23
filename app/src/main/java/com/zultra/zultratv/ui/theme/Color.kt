package com.zultra.zultratv.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String) {
    AMERALD_GREEN("Emerald Glass"),
    PURPLE_NEON("Cyber Purple")
}

// Liquid Glass Palette
val GlassPanelBg = Color(0x33101828)
val GlassPanelHover = Color(0x551E293B)
val GlassBorder = Color(0x40FFFFFF)
val GlassBorderFocused = Color(0x9900C8FF)
val GlassShine = Color(0x1AFFFFFF)

// Typography & Accents
val PrimeTextWhite = Color(0xFFF8FAFC)
val PrimeTextMuted = Color(0xFF94A3B8)
val PrimeRatingYellow = Color(0xFFFBBF24)

// Backward compatible aliases

val NetflixTextWhite = PrimeTextWhite
val NetflixTextMuted = PrimeTextMuted
val NetflixLightGrey = PrimeTextMuted
val NetflixWhite = PrimeTextWhite
