package com.brouken.player.core.gestures;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BrightnessCurveTest {

    @Test
    public void nextLevel_increasesWithinRange() {
        assertEquals(15, BrightnessCurve.nextLevel(14, true, false));
    }

    @Test
    public void nextLevel_decreasesWithinRange() {
        assertEquals(13, BrightnessCurve.nextLevel(14, false, false));
    }

    @Test
    public void nextLevel_dropsToAuto_whenAllowedAndBelowMin() {
        assertEquals(BrightnessCurve.LEVEL_AUTO, BrightnessCurve.nextLevel(0, false, true));
    }

    @Test
    public void nextLevel_staysAtMin_whenAutoNotAllowed() {
        assertEquals(0, BrightnessCurve.nextLevel(0, false, false));
    }

    @Test
    public void nextLevel_staysAtMax_whenAlreadyAtMax() {
        assertEquals(BrightnessCurve.LEVEL_MAX, BrightnessCurve.nextLevel(BrightnessCurve.LEVEL_MAX, true, false));
    }

    @Test
    public void nextLevel_fromAuto_increasingGoesToZero() {
        assertEquals(0, BrightnessCurve.nextLevel(BrightnessCurve.LEVEL_AUTO, true, true));
    }

    @Test
    public void levelToBrightness_isMonotonicallyIncreasing() {
        float prev = BrightnessCurve.levelToBrightness(0);
        for (int level = 1; level <= 30; level++) {
            float next = BrightnessCurve.levelToBrightness(level);
            assertEquals(true, next > prev);
            prev = next;
        }
    }

    @Test
    public void levelToBrightness_minAndMaxMatchLegacyCurve() {
        // d = 0.064 + 0.936/30*level; brightness = d*d
        assertEquals((float) Math.pow(0.064, 2), BrightnessCurve.levelToBrightness(0), 0.0001f);
        assertEquals(1.0f, BrightnessCurve.levelToBrightness(30), 0.0001f);
    }
}
