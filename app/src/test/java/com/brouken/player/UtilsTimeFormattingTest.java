package com.brouken.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Covers {@link Utils#formatMilis(long)} / {@link Utils#formatMilisSign(long)} — the
 * time-formatting used everywhere from the seek bar to the swipe-seek overlay
 * ({@code CustomPlayerView#onScroll} via {@link com.brouken.player.core.gestures.SeekGestureCalculator}).
 * Pure logic, no Android dependency, so this runs as a plain JVM unit test.
 */
public class UtilsTimeFormattingTest {

    @Test
    public void formatMilis_belowOneHour_omitsHours() {
        assertEquals("01:05", Utils.formatMilis(65_000L));
    }

    @Test
    public void formatMilis_oneHourOrMore_includesHours() {
        assertEquals("1:00:05", Utils.formatMilis(3_605_000L));
    }

    @Test
    public void formatMilis_zero() {
        assertEquals("00:00", Utils.formatMilis(0L));
    }

    @Test
    public void formatMilis_negative_usesAbsoluteValue() {
        assertEquals(Utils.formatMilis(65_000L), Utils.formatMilis(-65_000L));
    }

    @Test
    public void formatMilisSign_smallMagnitude_hasNoSign() {
        // Legacy behavior: values within (-1000, 1000) ms print without +/- (avoids "+00:00" flicker)
        assertEquals("00:00", Utils.formatMilisSign(500L));
    }

    @Test
    public void formatMilisSign_positive_hasPlusSign() {
        assertEquals("+00:05", Utils.formatMilisSign(5_000L));
    }

    @Test
    public void formatMilisSign_negative_hasMinusSign() {
        assertEquals("\u221200:05", Utils.formatMilisSign(-5_000L));
    }
}
