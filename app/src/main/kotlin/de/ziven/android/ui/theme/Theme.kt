package de.ziven.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ZivenColor.Moss500,
    onPrimary = ZivenColor.Cream,
    primaryContainer = ZivenColor.Moss100,
    onPrimaryContainer = ZivenColor.Moss800,
    secondary = ZivenColor.Vein,
    onSecondary = ZivenColor.Cream,
    secondaryContainer = ZivenColor.Mist,
    onSecondaryContainer = ZivenColor.Bark,
    tertiary = ZivenColor.Sap400,
    onTertiary = ZivenColor.Bark,
    tertiaryContainer = ZivenColor.Sap100,
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
    primary = ZivenColor.GlowMoss,
    onPrimary = ZivenColor.Root950,
    primaryContainer = ZivenColor.Moss700,
    onPrimaryContainer = ZivenColor.Moss100,
    secondary = ZivenColor.Vein,
    onSecondary = ZivenColor.CreamText,
    secondaryContainer = ZivenColor.Root700,
    onSecondaryContainer = ZivenColor.CreamDim,
    tertiary = ZivenColor.GlowGold,
    onTertiary = ZivenColor.Root950,
    tertiaryContainer = ZivenColor.Root800,
    onTertiaryContainer = ZivenColor.GlowGold,
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
