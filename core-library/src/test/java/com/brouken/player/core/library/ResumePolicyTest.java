package com.brouken.player.core.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResumePolicyTest {

    @Test
    public void notResumable_whenBarelyStarted() {
        assertFalse(ResumePolicy.isResumable(2_000L, 100_000L));
    }

    @Test
    public void resumable_wellIntoPlayback() {
        assertTrue(ResumePolicy.isResumable(50_000L, 100_000L));
    }

    @Test
    public void notResumable_whenNearEnd() {
        assertFalse(ResumePolicy.isResumable(98_000L, 100_000L));
    }

    @Test
    public void resumable_whenDurationUnknown_andPastMinimum() {
        assertTrue(ResumePolicy.isResumable(10_000L, 0L));
    }

    @Test
    public void notResumable_whenDurationUnknown_andBelowMinimum() {
        assertFalse(ResumePolicy.isResumable(1_000L, 0L));
    }

    @Test
    public void watchedFraction_midway() {
        assertEquals(0.5f, ResumePolicy.watchedFraction(50_000L, 100_000L), 0.0001f);
    }

    @Test
    public void watchedFraction_clampedAtOne() {
        assertEquals(1f, ResumePolicy.watchedFraction(150_000L, 100_000L), 0.0001f);
    }

    @Test
    public void watchedFraction_zeroDuration_isZero() {
        assertEquals(0f, ResumePolicy.watchedFraction(50_000L, 0L), 0.0001f);
    }
}
