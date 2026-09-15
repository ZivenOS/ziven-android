package de.ziven.android.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Warm "atmosphere" backdrop matching the web globals.css radial gradients.
 * Use behind content; it is decorative only.
 */
@Composable
fun ZivenBackdrop(modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()

    Canvas(modifier = modifier) {
        drawRect(
            brush = if (dark) {
                Brush.verticalGradient(listOf(ZivenColor.Root950, ZivenColor.Root900))
            } else {
                Brush.verticalGradient(listOf(ZivenColor.Paper, ZivenColor.Mist))
            },
        )

        val width = size.width
        val height = size.height
        if (dark) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ZivenColor.Root700.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(width * 0.2f, height * 0.06f),
                    radius = width * 1.4f,
                ),
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ZivenColor.Vein.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(width * 0.82f, height * 0.12f),
                    radius = width * 1.2f,
                ),
            )
        } else {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ZivenColor.Cream.copy(alpha = 0.9f), Color.Transparent),
                    center = Offset(width * 0.2f, height * 0.06f),
                    radius = width * 1.4f,
                ),
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ZivenColor.Mist.copy(alpha = 0.65f), Color.Transparent),
                    center = Offset(width * 0.82f, height * 0.12f),
                    radius = width * 1.2f,
                ),
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ZivenColor.Sap300.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(width * 0.5f, height * 1.08f),
                    radius = width * 1.6f,
                ),
            )
        }
    }
}
