package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = SuccessGreen,
    tertiary = PureWhite,
    background = DarkCharcoal,
    surface = DarkGray,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = DarkCharcoal,
    onBackground = SoftGray,
    onSurface = SoftGray,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightText
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = SuccessGreen,
    tertiary = DarkCharcoal,
    background = PureWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = PureWhite,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal,
    surfaceVariant = SoftGray,
    onSurfaceVariant = LightText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
