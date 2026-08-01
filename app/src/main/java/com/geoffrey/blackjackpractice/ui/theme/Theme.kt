package com.geoffrey.blackjackpractice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TableGreen = Color(0xFF0B6B3A)
private val TableGreenDark = Color(0xFF063D21)
private val Gold = Color(0xFFC9A227)

private val DarkColors = darkColorScheme(
    primary = Gold,
    secondary = TableGreen,
    background = Color(0xFF101410),
    surface = Color(0xFF171C17)
)

private val LightColors = lightColorScheme(
    primary = TableGreenDark,
    secondary = Gold,
    background = Color(0xFFF6F7F5),
    surface = Color.White
)

@Composable
fun BlackjackPracticeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
