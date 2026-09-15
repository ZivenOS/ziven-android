package de.ziven.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ZivenColor.Pulse,
    onPrimary = ZivenColor.Bark,
    primaryContainer = ZivenColor.Sap100,
    onPrimaryContainer = ZivenColor.Bark,
    secondary = ZivenColor.Moss500,
    onSecondary = ZivenColor.Cream,
    secondaryContainer = ZivenColor.Moss100,
    onSecondaryContainer = ZivenColor.Moss800,
    tertiary = ZivenColor.Vein,
    onTertiary = ZivenColor.Cream,
    tertiaryContainer = ZivenColor.Mist,
    onTertiaryContainer = ZivenColor.Bark,
    background = ZivenColor.Paper,
    onBackground = ZivenColor.Bark,
    surface = ZivenColor.Cream,
    onSurface = ZivenColor.Bark,
    surfaceVariant = ZivenColor.Mist,
    onSurfaceVariant = ZivenColor.BarkSoft,
    surfaceContainer = ZivenColor.Cream,
    surfaceContainerHigh = ZivenColor.Paper,
    surfaceContainerHighest = ZivenColor.Mist,
    inverseSurface = ZivenColor.Root800,
    inverseOnSurface = ZivenColor.CreamText,
    inversePrimary = ZivenColor.Moss300,
    surfaceTint = ZivenColor.Moss500,
    outline = ZivenColor.BarkSoft,
    outlineVariant = ZivenColor.Mist,
    error = ZivenColor.Ember,
    onError = ZivenColor.Cream,
    errorContainer = ZivenColor.Sap100,
    onErrorContainer = ZivenColor.Ember,
)

private val DarkColors = darkColorScheme(
    primary = ZivenColor.GlowGold,
    onPrimary = ZivenColor.Root950,
    primaryContainer = ZivenColor.Root800,
    onPrimaryContainer = ZivenColor.GlowGold,
    secondary = ZivenColor.GlowMoss,
    onSecondary = ZivenColor.Root950,
    secondaryContainer = ZivenColor.Moss700,
    onSecondaryContainer = ZivenColor.Moss100,
    tertiary = ZivenColor.Vein,
    onTertiary = ZivenColor.CreamText,
    tertiaryContainer = ZivenColor.Root700,
    onTertiaryContainer = ZivenColor.CreamDim,
    background = ZivenColor.Root950,
    onBackground = ZivenColor.CreamText,
    surface = ZivenColor.Root900,
    onSurface = ZivenColor.CreamText,
    surfaceVariant = ZivenColor.Root700,
    onSurfaceVariant = ZivenColor.CreamDim,
    surfaceContainer = ZivenColor.Root900,
    surfaceContainerHigh = ZivenColor.Root800,
    surfaceContainerHighest = ZivenColor.Root700,
    inverseSurface = ZivenColor.CreamText,
    inverseOnSurface = ZivenColor.Root950,
    inversePrimary = ZivenColor.Moss600,
    surfaceTint = ZivenColor.GlowMoss,
    outline = ZivenColor.CreamDim,
    outlineVariant = ZivenColor.Root700,
    error = ZivenColor.Ember,
    onError = ZivenColor.CreamText,
    errorContainer = ZivenColor.Ember.copy(alpha = 0.2f),
    onErrorContainer = ZivenColor.CreamText,
)

@Composable
fun ZivenTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // Brand is fixed; Material You dynamic colors are intentionally disabled so
    // the app keeps the same Living Canopy identity as the web.
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZivenTypography,
        shapes = ZivenShapes,
        content = content,
    )
}
