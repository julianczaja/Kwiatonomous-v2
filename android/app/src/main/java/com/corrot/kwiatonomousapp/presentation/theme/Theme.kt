package com.corrot.kwiatonomousapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = DayPrimary,
    primaryVariant = DayPrimaryVariant,
    secondary = DaySecondary,
    secondaryVariant = DaySecondaryVariant,
    background = DayBackground,
    surface = DaySurface,
    onPrimary = DayOnPrimary,
    onSecondary = DayOnSecondary,
    onBackground = DayOnBackground,
    onSurface = DayOnSurface,
    error = Error,
    onError = onError
)

private val DarkColorPalette = darkColors(
    primary = NightPrimary,
    primaryVariant = NightPrimaryVariant,
    secondary = NightSecondary,
    secondaryVariant = NightSecondaryVariant,
    background = NightBackground,
    surface = NightSurface,
    onPrimary = NightOnPrimary,
    onSecondary = NightOnSecondary,
    onBackground = NightOnBackground,
    onSurface = NightOnSurface,
    error = Error,
    onError = onError
)

@Composable
fun KwiatonomousAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = TypographyRoboto,
        shapes = Shapes,
        content = content
    )
}