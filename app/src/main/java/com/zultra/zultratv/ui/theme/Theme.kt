package com.zultra.zultratv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val PrimeVideoColorScheme = darkColorScheme(
    primary = PrimeElectricBlue,
    onPrimary = PrimeBackground,
    primaryContainer = PrimeSurfaceElevated,
    onPrimaryContainer = PrimeTextWhite,
    secondary = PrimeBrightCyan,
    onSecondary = PrimeBackground,
    background = PrimeBackground,
    onBackground = PrimeTextWhite,
    surface = PrimeSurfaceCard,
    onSurface = PrimeTextWhite,
    surfaceVariant = PrimeSurfaceElevated,
    onSurfaceVariant = PrimeTextMuted
)

@Composable
fun ZultraTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PrimeVideoColorScheme,
        typography = ZultraTypography,
        content = content
    )
}
