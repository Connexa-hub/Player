package com.brouken.player.feature.playerui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.GlassSurface
import com.brouken.player.core.design.PlayerShapes

/**
 * The floating play/pause/skip pill — the single most-touched control in the whole app, so it
 * gets the glass treatment and generous 56dp touch targets (well above the 48dp minimum).
 *
 * [state]/[actions] are the only inputs; this composable has no player reference and no side
 * effects of its own, so it can be previewed and screenshot-tested without a real playback
 * session.
 */
@Composable
fun PlayerControlsBar(
    state: PlayerControlsState,
    actions: PlayerControlsActions,
    modifier: Modifier = Modifier,
) {
    GlassSurface(modifier = modifier, shape = PlayerShapes.pill) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            IconButton(onClick = actions::onSkipBackClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Seek backward",
                    tint = Color.White,
                )
            }
            IconButton(onClick = actions::onPlayPauseClick, modifier = Modifier.size(56.dp)) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
            IconButton(onClick = actions::onSkipForwardClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Seek forward",
                    tint = Color.White,
                )
            }
        }
    }
}
