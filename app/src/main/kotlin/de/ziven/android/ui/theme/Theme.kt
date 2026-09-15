package de.ziven.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = ZivenColor.Moss,
    onPrimary = ZivenColor.Canopy,
    primaryContainer = ZivenColor.Mist,
    onPrimaryContainer = ZivenColor.Ink,
    secondary = ZivenColor.Vein,
    tertiary = ZivenColor.Pulse,
    background = ZivenColor.Canopy,
    onBackground = ZivenColor.Ink,
    surface = ZivenColor.Canopy,
    onSurface = ZivenColor.Ink,
    surfaceVariant = ZivenColor.Mist,
    error = ZivenColor.Ember,
)

private val DarkColors = darkColorScheme(
    primary = ZivenColor.Moss,
    onPrimary = ZivenColor.Canopy,
    primaryContainer = ZivenColor.Vein,
    onPrimaryContainer = ZivenColor.Canopy,
    secondary = ZivenColor.Vein,
    tertiary = ZivenColor.Sap,
    background = ZivenColor.Soil,
    onBackground = ZivenColor.Canopy,
    surface = ZivenColor.Ink,
    onSurface = ZivenColor.Canopy,
    surfaceVariant = ZivenColor.Bark,
    error = ZivenColor.Ember,
)

@Composable
fun ZivenTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZivenTypography,
        content = content,
    )
}
