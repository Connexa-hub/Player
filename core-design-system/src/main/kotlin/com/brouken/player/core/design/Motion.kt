package com.brouken.player.core.design

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Motion language: spring-based by default (matches the "buttery," physical feel the brief asks
 * for — Cash App / Material 3 Expressive both lean on springs rather than fixed-duration eases),
 * with a small set of tween fallbacks for cases that need a fixed, predictable duration
 * (crossfades between video frames/UI states where sync matters more than bounce).
 *
 * These are dp/float specs (`FiniteAnimationSpec<Float>`) usable directly with `animate*AsState`.
 * Frame-rate independence (120Hz vs 60Hz) comes for free from Compose's spring implementation,
 * which is physically time-based rather than frame-count-based — no per-refresh-rate tuning
 * needed here.
 */
object PlayerMotion {

    /** Snappy, low-overshoot — controls appearing/disappearing, button press feedback. */
    fun <T> snappy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** A touch of overshoot for expressive, "alive" transitions — sheet/card entrances. */
    fun <T> expressive(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** Slow, deliberate — brightness/volume overlay fade, PiP enter/exit. */
    fun <T> gentle(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
    )

    /** Fixed-duration fallback for frame-synced crossfades (e.g. subtitle cue in/out). */
    fun <T> crossfade(durationMs: Int = 180): FiniteAnimationSpec<T> = tween(durationMillis = durationMs)

    const val CONTROLS_AUTO_HIDE_DELAY_MS = 3_000L
    const val SCRUB_HAPTIC_DEBOUNCE_MS = 40L
}
