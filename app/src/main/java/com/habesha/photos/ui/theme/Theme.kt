package com.habesha.photos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Bg = Color(0xFF0A0C0E)
val BgElevated = Color(0xFF14171A)
val BgBar = Color(0xEB0A0C0E)
val TextPrimary = Color(0xFFF5F6F7)
val TextSecondary = Color(0xFFA0A8B0)
val TextMuted = Color(0xFF6B737C)
val Accent = Color(0xFFB07BB5)
val AccentSoft = Color(0x2EB07BB5)
val AccentStrong = Color(0xFFD4A0D8)
val FavPink = Color(0xFFF0A0C8)

private val DarkScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    background = Bg,
    onBackground = TextPrimary,
    surface = BgElevated,
    onSurface = TextPrimary,
    surfaceVariant = BgElevated,
    onSurfaceVariant = TextSecondary
)

@Composable
fun PhotosHabeshaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content
    )
}
