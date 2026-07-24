package com.brouken.player.feature.playerui

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerControlsStateTest {

    private fun state(positionMs: Long, durationMs: Long, bufferedMs: Long = 0L) = PlayerControlsState(
        isPlaying = true,
        isBuffering = false,
        positionMs = positionMs,
        durationMs = durationMs,
        bufferedMs = bufferedMs,
        positionText = "",
        durationText = "",
    )

    @Test
    fun progressFraction_midway() {
        assertEquals(0.5f, state(positionMs = 50_000, durationMs = 100_000).progressFraction, 0.0001f)
    }

    @Test
    fun progressFraction_zeroDuration_doesNotDivideByZero() {
        assertEquals(0f, state(positionMs = 0, durationMs = 0).progressFraction, 0.0001f)
    }

    @Test
    fun progressFraction_isClampedToOne_evenIfPositionOvershootsDuration() {
        // Can genuinely happen for a frame or two right at end-of-media before ExoPlayer's
        // position callback catches up to the reported duration.
        assertEquals(1f, state(positionMs = 100_500, durationMs = 100_000).progressFraction, 0.0001f)
    }

    @Test
    fun bufferedFraction_independentOfPosition() {
        val s = state(positionMs = 10_000, durationMs = 100_000, bufferedMs = 40_000)
        assertEquals(0.1f, s.progressFraction, 0.0001f)
        assertEquals(0.4f, s.bufferedFraction, 0.0001f)
    }

    @Test
    fun empty_hasZeroProgress() {
        assertEquals(0f, PlayerControlsState.Empty.progressFraction, 0.0001f)
    }
}
