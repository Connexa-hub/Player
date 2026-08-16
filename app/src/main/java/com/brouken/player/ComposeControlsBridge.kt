package com.brouken.player

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.brouken.player.core.design.PlayerTheme
import com.brouken.player.core.design.PlayerThemeMode
import com.brouken.player.feature.playerui.PlayerControlsActions
import com.brouken.player.feature.playerui.PlayerControlsBar
import com.brouken.player.feature.playerui.PlayerControlsState
import com.brouken.player.feature.playerui.ScrubberTrack

/**
 * Experimental: wires a real [ExoPlayer] to the `feature-player-ui` composables and renders them
 * into [composeView]. Constructed by [PlayerActivity] only when `Prefs.useComposeControls` is
 * true; the legacy XML `exo_player_control_view` / [CustomPlayerView] gesture handling stays
 * exactly as-is and is not affected by this class existing.
 *
 * Scope note: this deliberately does not (yet) cover track selection, subtitle styling, PiP, or
 * gesture overlays — it's the minimum real wiring needed to validate that the new composables
 * work against actual playback state. Feature parity with the legacy controls is later work.
 */
class ComposeControlsBridge(
    private val composeView: ComposeView,
    private val player: ExoPlayer,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var positionPoller: Runnable? = null

    private var state by mutableStateOf(PlayerControlsState.Empty)

    private val actions = object : PlayerControlsActions {
        override fun onPlayPauseClick() {
            if (player.isPlaying) player.pause() else player.play()
        }

        override fun onSkipBackClick() {
            player.seekTo((player.currentPosition - SKIP_MS).coerceAtLeast(0L))
        }

        override fun onSkipForwardClick() {
            val duration = durationOrZero()
            val target = if (duration <= 0L) player.currentPosition + SKIP_MS
                         else (player.currentPosition + SKIP_MS).coerceAtMost(duration)
            player.seekTo(target)
        }

        override fun onSeekStart() {
            stopPositionPolling()
        }

        override fun onSeekChanged(fraction: Float) {
            val duration = durationOrZero()
            val position = (fraction * duration).toLong()
            state = state.copy(positionMs = position, positionText = Utils.formatMilis(position))
        }

        override fun onSeekFinished(fraction: Float) {
            val duration = durationOrZero()
            player.seekTo((fraction * duration).toLong())
            startPositionPolling()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            state = state.copy(isPlaying = isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            state = state.copy(isBuffering = playbackState == Player.STATE_BUFFERING)
            refreshDuration()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            state = PlayerControlsState.Empty.copy(
                title = mediaItem?.mediaMetadata?.title?.toString(),
            )
            refreshDuration()
        }
    }

    /** Call once after the player is prepared. Mirrors the Activity's onStart/onResume lifetime. */
    fun attach() {
        player.addListener(playerListener)
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            PlayerTheme(mode = PlayerThemeMode.Dark) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(composeView.context, LibraryActivity::class.java)
                            composeView.context.startActivity(intent)
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Library",
                            tint = Color.White,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ScrubberTrack(state = state, actions = actions, modifier = Modifier.fillMaxWidth())
                        PlayerControlsBar(state = state, actions = actions)
                    }
                }
            }
        }
        refreshDuration()
        startPositionPolling()
    }

    /** Call from the Activity's teardown path (mirrors releasePlayer()). */
    fun detach() {
        stopPositionPolling()
        player.removeListener(playerListener)
    }

    private fun durationOrZero(): Long {
        val duration = player.duration
        return if (duration == C.TIME_UNSET) 0L else duration
    }

    private fun refreshDuration() {
        val duration = durationOrZero()
        state = state.copy(durationMs = duration, durationText = Utils.formatMilis(duration))
    }

    private fun startPositionPolling() {
        stopPositionPolling()
        val poller = object : Runnable {
            override fun run() {
                state = state.copy(
                    positionMs = player.currentPosition,
                    bufferedMs = player.bufferedPosition,
                    positionText = Utils.formatMilis(player.currentPosition),
                )
                handler.postDelayed(this, POSITION_POLL_INTERVAL_MS)
            }
        }
        positionPoller = poller
        handler.post(poller)
    }

    private fun stopPositionPolling() {
        positionPoller?.let { handler.removeCallbacks(it) }
        positionPoller = null
    }

    private companion object {
        const val SKIP_MS = 10_000L
        const val POSITION_POLL_INTERVAL_MS = 200L
    }
}
