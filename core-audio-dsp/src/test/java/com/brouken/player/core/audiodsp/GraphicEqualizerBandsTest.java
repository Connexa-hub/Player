package com.brouken.player.core.audiodsp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GraphicEqualizerBandsTest {

    @Test
    public void tenBands_matchBandCount() {
        assertEquals(10, GraphicEqualizerBands.FREQUENCIES_HZ.length);
        assertEquals(10, GraphicEqualizerBands.BAND_COUNT);
    }

    @Test
    public void frequencies_areInAscendingOrder() {
        double[] freqs = GraphicEqualizerBands.FREQUENCIES_HZ;
        for (int i = 1; i < freqs.length; i++) {
            assertEquals(true, freqs[i] > freqs[i - 1]);
        }
    }

    @Test
    public void clampGainDb_clampsAboveMax() {
        assertEquals(GraphicEqualizerBands.MAX_GAIN_DB, GraphicEqualizerBands.clampGainDb(100.0), 0.0);
    }

    @Test
    public void clampGainDb_clampsBelowMin() {
        assertEquals(GraphicEqualizerBands.MIN_GAIN_DB, GraphicEqualizerBands.clampGainDb(-100.0), 0.0);
    }

    @Test
    public void clampGainDb_passesThroughInRangeValues() {
        assertEquals(3.5, GraphicEqualizerBands.clampGainDb(3.5), 0.0);
    }
}
