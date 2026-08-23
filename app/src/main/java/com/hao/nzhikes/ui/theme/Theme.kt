package com.hao.nzhikes.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AlpineGreen, onPrimary = Color(0xFF00391F),
    primaryContainer = DarkForestContainer, onPrimaryContainer = Color(0xFFA9F2C2),
    secondary = DarkLakeTeal, onSecondary = Color(0xFF003737),
    secondaryContainer = DarkLakeContainer, onSecondaryContainer = Color(0xFF9CF1EF),
    tertiary = DarkTussockGold, onTertiary = Color(0xFF492900),
    tertiaryContainer = DarkTussockContainer, onTertiaryContainer = Color(0xFFFFDDB8),
    background = NightBackground, onBackground = NightInk,
    surface = NightSurface, onSurface = NightInk,
    surfaceVariant = NightSurfaceVariant, onSurfaceVariant = NightMutedInk,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen, onPrimary = OnForestGreen,
    primaryContainer = ForestContainer, onPrimaryContainer = OnForestContainer,
    secondary = LakeTeal, onSecondary = Color.White,
    secondaryContainer = LakeContainer, onSecondaryContainer = Color(0xFF002020),
    tertiary = TussockGold, onTertiary = Color.White,
    tertiaryContainer = TussockContainer, onTertiaryContainer = Color(0xFF2D1600),
    background = MistBackground, onBackground = Ink,
    surface = MistSurface, onSurface = Ink,
    surfaceVariant = MistSurfaceVariant, onSurfaceVariant = MutedInk,
    outline = LightOutline
)

// CompositionLocal for theme state
val LocalThemeState = staticCompositionLocalOf { false }

@Composable
fun NZHikesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Define gradient colors based on theme
    val gradientColors = if (darkTheme) {
        listOf(Color(0xFF0C1510), NightBackground, Color(0xFF123127))
    } else {
        listOf(Color(0xFFF9FBF6), MistBackground, Color(0xFFE2F1EA))
    }

    val gradientBrush = Brush.verticalGradient(
        colors = gradientColors,
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    CompositionLocalProvider(LocalThemeState provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            ) {
                content()
            }
        }
    }
}
