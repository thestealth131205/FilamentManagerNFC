package de.letheapp.filamentmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FilamentOrange = Color(0xFFFF8A3D)

private val LightColors = lightColorScheme(primary = FilamentOrange)
private val DarkColors = darkColorScheme(primary = FilamentOrange)

@Composable
fun FilamentManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
