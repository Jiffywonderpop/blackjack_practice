package com.geoffrey.blackjackpractice.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Casino palette shared by every screen. Like most top-rated blackjack apps, the app always
 * renders on a dark felt/gold look rather than following the system light theme.
 */
object CasinoColors {
    val FeltLight = Color(0xFF1E8C4E)
    val Felt = Color(0xFF0F6B3A)
    val FeltDark = Color(0xFF07401F)
    val FeltEdge = Color(0xFF042814)

    val Gold = Color(0xFFE2B84A)
    val GoldLight = Color(0xFFF7DE8A)
    val GoldDark = Color(0xFF9C7420)

    val Rail = Color(0xFF3B1F12)
    val Panel = Color(0xCC0B1A10)
    val PanelSolid = Color(0xFF10201A)

    val CardFace = Color(0xFFFFFDF7)
    val CardBorder = Color(0xFFD5D0C4)
    val CardRed = Color(0xFFC62828)
    val CardBlack = Color(0xFF1B1B1B)
    val CardBack = Color(0xFF8E1B23)
    val CardBackDark = Color(0xFF5E0E15)

    val Win = Color(0xFF43C66B)
    val Lose = Color(0xFFE5484D)
    val Push = Color(0xFFB0B7BE)

    // Action-button colours follow the common Hit = green, Stand = red, Double = orange, Split = blue scheme.
    val HitTop = Color(0xFF43B857)
    val HitBottom = Color(0xFF237A35)
    val StandTop = Color(0xFFE5484D)
    val StandBottom = Color(0xFF9E1F23)
    val DoubleTop = Color(0xFFF5A524)
    val DoubleBottom = Color(0xFFB86E06)
    val SplitTop = Color(0xFF3E8EF0)
    val SplitBottom = Color(0xFF1C56A8)
    val SurrenderTop = Color(0xFF8A939C)
    val SurrenderBottom = Color(0xFF4E565E)
}

private val CasinoColorScheme = darkColorScheme(
    primary = CasinoColors.Gold,
    onPrimary = Color(0xFF2A1E00),
    primaryContainer = CasinoColors.GoldDark,
    onPrimaryContainer = CasinoColors.GoldLight,
    secondary = CasinoColors.FeltLight,
    onSecondary = Color.White,
    secondaryContainer = CasinoColors.Felt,
    onSecondaryContainer = Color.White,
    tertiary = CasinoColors.CardRed,
    background = Color(0xFF07150D),
    onBackground = Color(0xFFEDEFE9),
    surface = Color(0xFF0B1C12),
    onSurface = Color(0xFFEDEFE9),
    surfaceVariant = Color(0xFF15301F),
    onSurfaceVariant = Color(0xFFC3CCC0),
    surfaceContainer = Color(0xFF112619),
    surfaceContainerHigh = Color(0xFF16301F),
    surfaceContainerHighest = Color(0xFF1C3A26),
    surfaceContainerLow = Color(0xFF0D2015),
    outline = CasinoColors.GoldDark,
    outlineVariant = Color(0xFF2C4A36),
    error = CasinoColors.Lose
)

private val CasinoTypography = Typography().let { base ->
    base.copy(
        displaySmall = base.displaySmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black),
        headlineLarge = base.headlineLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
        headlineMedium = base.headlineMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
        headlineSmall = base.headlineSmall.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
        titleLarge = base.titleLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun BlackjackPracticeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CasinoColorScheme,
        typography = CasinoTypography,
        content = content
    )
}
