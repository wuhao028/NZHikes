package com.hao.nzhikes.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.hao.ui.theme.ThemeManager

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Transparent,
    surface = Color.Transparent,
    surfaceVariant = Color.Transparent
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.Transparent,
    surface = Color.Transparent,
    surfaceVariant = Color.Transparent
)

@Composable
fun NZHikesTheme(
    darkTheme: Boolean = ThemeManager.isDarkMode,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Override background colors to be transparent so gradient shows through
    val colorScheme = baseColorScheme.copy(
        background = Color.Transparent,
        surface = Color.Transparent,
        surfaceVariant = Color.Transparent
    )

    // Define gradient colors based on theme
    val gradientColors = if (darkTheme) {
        listOf(
            Color(0xFF0B1426), // Very dark navy
            Color(0xFF1E3A8A), // Dark blue
            Color(0xFF3730A3), // Indigo
            Color(0xFF581C87)  // Purple
        )
    } else {
        listOf(
            Color(0xFFE0F2FE), // Very light blue
            Color(0xFFBAE6FD), // Light blue
            Color(0xFF7DD3FC), // Medium blue
            Color(0xFF38BDF8)  // Soft blue
        )
    }

    val gradientBrush = Brush.verticalGradient(
        colors = gradientColors,
        startY = 0f,
        endY = 1000f
    )

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