package com.brouken.player.core.gestures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SeekGestureCalculatorTest {

    private static final long TIME_UNSET = Long.MIN_VALUE + 1; // mirrors media3 C.TIME_UNSET
    private static final long SEEK_STEP = 1000;

    @Test
    public void distanceDiff_clampsToMinimum() {
        assertEquals(0.5f, SeekGestureCalculator.distanceDiff(0.1f), 0.0001f);
    }

    @Test
    public void distanceDiff_clampsToMaximum() {
        assertEquals(10f, SeekGestureCalculator.distanceDiff(1000f), 0.0001f);
    }

    @Test
    public void distanceDiff_isSymmetricForNegativeDistance() {
        assertEquals(SeekGestureCalculator.distanceDiff(40f), SeekGestureCalculator.distanceDiff(-40f), 0.0001f);
    }

    @Test
    public void backward_appliesSeek_whenWithinBounds() {
        SeekGestureCalculator.Seek result = SeekGestureCalculator.backward(60_000L, 0L, 1f, SEEK_STEP);
        assertTrue(result.applied);
        assertEquals(-1000L, result.seekChange);
        assertEquals(59_000L, result.position);
    }

    @Test
    public void backward_refusesToSeekBeforeZero() {
        SeekGestureCalculator.Seek result = SeekGestureCalculator.backward(500L, 0L, 1f, SEEK_STEP);
        assertFalse(result.applied);
        assertEquals(0L, result.seekChange);
    }

    @Test
    public void forward_appliesSeek_whenDurationUnknown() {
        SeekGestureCalculator.Seek result = SeekGestureCalculator.forward(60_000L, 0L, 1f, SEEK_STEP, TIME_UNSET, TIME_UNSET);
        assertTrue(result.applied);
        assertEquals(1000L, result.seekChange);
        assertEquals(61_000L, result.position);
    }

    @Test
    public void forward_appliesSeek_whenWithinKnownDuration() {
        SeekGestureCalculator.Seek result = SeekGestureCalculator.forward(60_000L, 0L, 1f, SEEK_STEP, 120_000L, TIME_UNSET);
        assertTrue(result.applied);
        assertEquals(61_000L, result.position);
    }

    @Test
    public void forward_refusesToSeekPastDuration() {
        SeekGestureCalculator.Seek result = SeekGestureCalculator.forward(119_500L, 0L, 1f, SEEK_STEP, 120_000L, TIME_UNSET);
        assertFalse(result.applied);
        assertEquals(0L, result.seekChange);
    }
}
