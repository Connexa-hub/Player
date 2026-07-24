package com.brouken.player.core.design

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Brand palette. Deliberately not VLC-orange, not a generic Material blue: a violet→cyan
 * accent gradient on near-black, closer to the Linear/Arc/Cash App register than a traditional
 * media-player skin.
 */
object PlayerColors {
    // Brand accent — the one color that should read as "this app" at a glance.
    val AccentVioletLight = Color(0xFFB39BFF)
    val AccentViolet = Color(0xFF8B6CFF)
    val AccentVioletDeep = Color(0xFF5B3DE8)
    val AccentCyan = Color(0xFF5FE3D0)

    // Dark-first neutrals. Not pure #000 by default — a hair of blue keeps large flat areas
    // from looking like a dead pixel, while AMOLED mode below goes true black on purpose.
    val Neutral0 = Color(0xFF08090C)   // deepest surface (dark theme background)
    val Neutral10 = Color(0xFF111319)
    val Neutral20 = Color(0xFF191C24)
    val Neutral30 = Color(0xFF23262F)
    val Neutral80 = Color(0xFFC7C9D1)
    val Neutral95 = Color(0xFFEDEDF2)
    val Neutral100 = Color(0xFFFFFFFF)

    val AmoledBlack = Color(0xFF000000)

    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFFFC24B)
    val Danger = Color(0xFFFF6B6B)

    /** For glass/translucent surfaces layered over video content. */
    val GlassScrimDark = Color(0x99000000)
    val GlassFillDark = Color(0x1AFFFFFF)
    val GlassStrokeDark = Color(0x33FFFFFF)

    val AccentGradient = Brush.linearGradient(listOf(AccentViolet, AccentCyan))
}

/** Which palette the user has selected — mirrors the brief's Light/Dark/AMOLED/Dynamic/Custom set. */
enum class PlayerThemeMode {
    Dark,
    Amoled,
    Light,
    /** Material You — derived from the device wallpaper, API 31+. Falls back to [Dark]. */
    Dynamic,
}

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = PlayerColors.AccentViolet,
    onPrimary = PlayerColors.Neutral0,
    secondary = PlayerColors.AccentCyan,
    background = PlayerColors.Neutral0,
    onBackground = PlayerColors.Neutral95,
    surface = PlayerColors.Neutral10,
    onSurface = PlayerColors.Neutral95,
    surfaceVariant = PlayerColors.Neutral20,
    error = PlayerColors.Danger,
)

private val AmoledScheme: ColorScheme = DarkScheme.copy(
    background = PlayerColors.AmoledBlack,
    surface = PlayerColors.AmoledBlack,
    surfaceVariant = PlayerColors.Neutral10,
)

private val LightScheme: ColorScheme = lightColorScheme(
    primary = PlayerColors.AccentVioletDeep,
    onPrimary = PlayerColors.Neutral100,
    secondary = PlayerColors.AccentViolet,
    background = PlayerColors.Neutral95,
    onBackground = PlayerColors.Neutral10,
    surface = PlayerColors.Neutral100,
    onSurface = PlayerColors.Neutral10,
    surfaceVariant = PlayerColors.Neutral80,
    error = PlayerColors.Danger,
)

/**
 * Resolves a [PlayerThemeMode] to an actual [ColorScheme]. Composable because [PlayerThemeMode.Dynamic]
 * needs [LocalContext] to read the platform's Material You palette.
 */
@androidx.compose.runtime.Composable
fun playerColorScheme(mode: PlayerThemeMode): ColorScheme {
    val context = LocalContext.current
    return when (mode) {
        PlayerThemeMode.Dark -> DarkScheme
        PlayerThemeMode.Amoled -> AmoledScheme
        PlayerThemeMode.Light -> LightScheme
        PlayerThemeMode.Dynamic -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Respect the system day/night setting for which dynamic variant to use.
            val isDark = (context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            DarkScheme
        }
    }
}
