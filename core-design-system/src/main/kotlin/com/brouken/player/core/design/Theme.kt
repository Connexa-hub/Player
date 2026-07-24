package com.brouken.player.core.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Root theme composable for the whole app. Every screen — player controls, library, settings,
 * equalizer — should be wrapped in this once, at the top of the composition, the same way the
 * legacy app applies one XML style to the whole Activity today.
 *
 * Usage (once the app module adopts Compose — not wired in yet, this module is additive only):
 * ```
 * PlayerTheme(mode = PlayerThemeMode.Dark) {
 *     PlayerControlsScreen(...)
 * }
 * ```
 */
@Composable
fun PlayerTheme(
    mode: PlayerThemeMode = PlayerThemeMode.Dark,
    content: @Composable () -> Unit,
) {
    val colorScheme = playerColorScheme(mode)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PlayerTypography.asMaterialTypography(),
        shapes = PlayerShapes.asMaterialShapes(),
        content = content,
    )
}
