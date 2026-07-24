package com.brouken.player.feature.playerui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.GlassSurface
import com.brouken.player.core.design.PlayerColors
import com.brouken.player.core.design.PlayerShapes
import com.brouken.player.core.design.PlayerTypography

/**
 * The seek scrubber row: current position, drag track, remaining/total duration.
 *
 * Dragging maintains its own local [dragFraction] so the thumb tracks the finger immediately
 * even before the caller's state round-trips back through a player callback — the same reason
 * every seek bar implementation (including ExoPlayer's own `DefaultTimeBar`) does this. Only
 * [PlayerControlsActions.onSeekFinished] actually commits a seek to the player.
 */
@Composable
fun ScrubberTrack(
    state: PlayerControlsState,
    actions: PlayerControlsActions,
    modifier: Modifier = Modifier,
) {
    var isDragging by remember { androidx.compose.runtime.mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val displayedFraction = if (isDragging) dragFraction else state.progressFraction

    GlassSurface(modifier = modifier.fillMaxWidth(), shape = PlayerShapes.large) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = state.positionText,
                style = PlayerTypography.timecode,
                color = Color.White,
            )

            Slider(
                value = displayedFraction,
                onValueChange = { fraction ->
                    if (!isDragging) {
                        isDragging = true
                        actions.onSeekStart()
                    }
                    dragFraction = fraction
                    actions.onSeekChanged(fraction)
                },
                onValueChangeFinished = {
                    actions.onSeekFinished(dragFraction)
                    isDragging = false
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = PlayerColors.AccentViolet,
                    activeTrackColor = PlayerColors.AccentViolet,
                    inactiveTrackColor = PlayerColors.GlassFillDark,
                ),
            )

            Text(
                text = state.durationText,
                style = PlayerTypography.timecode,
                color = PlayerColors.Neutral80,
            )
        }
    }
}
