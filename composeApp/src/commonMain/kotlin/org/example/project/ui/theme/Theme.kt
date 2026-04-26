package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand Colors ──
val AzureBlue = Color(0xFF0061FF)
val AzureDark = Color(0xFF003692)
val AzureLight = Color(0xFF60A5FA)
val SurfaceDark = Color(0xFF1E293B)
val BackgroundDark = Color(0xFF0F172A)

private val DarkColors = darkColorScheme(
    primary = AzureLight,
    secondary = Color(0xFF38BDF8),
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColors = lightColorScheme(
    primary = AzureBlue,
    secondary = Color(0xFF0284C7),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun NotesAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
