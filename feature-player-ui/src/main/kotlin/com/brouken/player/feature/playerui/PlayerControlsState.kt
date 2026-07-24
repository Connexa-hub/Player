package com.brouken.player.feature.playerui

/**
 * Everything the floating control surface needs to render, decoupled from ExoPlayer/Media3.
 * The caller (eventually `PlayerActivity`, or a ViewModel wrapping [com.brouken.player.core.playback.PlaybackEngine])
 * is responsible for keeping this up to date from player callbacks; this module never touches
 * Media3 directly, which is what makes it independently previewable and testable.
 */
data class PlayerControlsState(
    val isPlaying: Boolean,
    val isBuffering: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val bufferedMs: Long,
    /** Pre-formatted so this module doesn't need to duplicate `Utils.formatMilis` locale logic. */
    val positionText: String,
    val durationText: String,
    val isControllerVisible: Boolean = true,
    val title: String? = null,
) {
    /** 0f..1f, clamped — safe to feed directly into a Slider/Canvas progress fraction. */
    val progressFraction: Float
        get() = if (durationMs <= 0L) 0f else (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    val bufferedFraction: Float
        get() = if (durationMs <= 0L) 0f else (bufferedMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    companion object {
        val Empty = PlayerControlsState(
            isPlaying = false,
            isBuffering = false,
            positionMs = 0L,
            durationMs = 0L,
            bufferedMs = 0L,
            positionText = "00:00",
            durationText = "00:00",
            isControllerVisible = true,
            title = null,
        )
    }
}

/** Callbacks the control surface fires; wiring these to the real player is the caller's job. */
interface PlayerControlsActions {
    fun onPlayPauseClick()
    fun onSkipBackClick()
    fun onSkipForwardClick()
    fun onSeekStart()
    /** Called continuously while the user drags the scrubber; [fraction] is 0f..1f. */
    fun onSeekChanged(fraction: Float)
    fun onSeekFinished(fraction: Float)
}
