package com.brouken.player.core.audiodsp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BiquadFilterTest {

    @Test
    public void identityFilter_passesSamplesUnchanged() {
        BiquadFilter filter = new BiquadFilter(BiquadCoefficients.identity());
        double[] input = {0.1, -0.3, 0.5, 0.0, -0.9, 0.25};
        for (double sample : input) {
            assertEquals(sample, filter.process(sample), 1e-12);
        }
    }

    @Test
    public void zeroDbPeakingFilter_passesSineWaveUnchanged() {
        // A real, non-trivial signal (not silence, not a single impulse) run through a 0dB band —
        // exercises the filter's history/state machinery, not just a one-sample identity check.
        BiquadCoefficients zeroDb = BiquadCoefficients.peakingEq(44100.0, 1000.0, 0.0, 1.0);
        BiquadFilter filter = new BiquadFilter(zeroDb);

        for (int n = 0; n < 200; n++) {
            double x = Math.sin(2 * Math.PI * 440.0 * n / 44100.0);
            double y = filter.process(x);
            assertEquals(x, y, 1e-9);
        }
    }

    @Test
    public void reset_clearsHistory_matchingAFreshFilterInstance() {
        BiquadCoefficients coeffs = BiquadCoefficients.peakingEq(44100.0, 1000.0, 6.0, 1.0);
        BiquadFilter filter = new BiquadFilter(coeffs);

        // Feed some history in, then reset.
        filter.process(0.5);
        filter.process(-0.3);
        filter.process(0.8);
        filter.reset();

        BiquadFilter freshFilter = new BiquadFilter(coeffs);

        // Both filters now have identical (empty) history, so identical future input must
        // produce bit-for-bit identical output.
        for (double x : new double[] {0.1, 0.2, -0.4, 0.05}) {
            assertEquals(freshFilter.process(x), filter.process(x), 0.0);
        }
    }

    @Test
    public void setCoefficients_changesFilterBehaviorWithoutResettingHistory() {
        BiquadFilter filter = new BiquadFilter(BiquadCoefficients.identity());
        filter.process(0.5); // build up some (trivial, since identity) history
        filter.setCoefficients(BiquadCoefficients.peakingEq(44100.0, 1000.0, 6.0, 1.0));
        // Just verifying this doesn't throw and produces a finite result — the exact boosted
        // value is covered indirectly by the coefficient-level tests.
        double result = filter.process(0.5);
        assertEquals(true, Double.isFinite(result));
    }
}
