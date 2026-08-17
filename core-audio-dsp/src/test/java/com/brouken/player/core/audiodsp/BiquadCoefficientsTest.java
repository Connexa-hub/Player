package com.brouken.player.core.audiodsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class BiquadCoefficientsTest {

    private static final double SAMPLE_RATE = 44100.0;

    @Test
    public void identity_isExactPassthrough() {
        BiquadCoefficients c = BiquadCoefficients.identity();
        assertEquals(1.0, c.b0, 0.0);
        assertEquals(0.0, c.b1, 0.0);
        assertEquals(0.0, c.b2, 0.0);
        assertEquals(0.0, c.a1, 0.0);
        assertEquals(0.0, c.a2, 0.0);
    }

    /**
     * At 0dB gain, a peaking-EQ filter is mathematically an exact identity: A = 10^(0/40) = 1,
     * which makes the raw (pre-normalization) b0/b1/b2 coefficients exactly equal to a0/a1/a2,
     * so after normalizing by a0 every coefficient collapses to the identity filter
     * (b0=1, b1=a1, b2=a2). This is a real derivable property of the RBJ cookbook formula, not
     * an approximation — so this test pins exact equality, not "close enough".
     */
    @Test
    public void peakingEq_atZeroGain_collapsesToIdentity() {
        BiquadCoefficients c = BiquadCoefficients.peakingEq(SAMPLE_RATE, 1000.0, 0.0, 1.0);
        assertEquals(1.0, c.b0, 1e-12);
        assertEquals(c.a1, c.b1, 1e-12);
        assertEquals(c.a2, c.b2, 1e-12);
    }

    @Test
    public void peakingEq_positiveGain_boostsB0AboveOne() {
        // A boost should make the filter's DC-ish gain coefficient exceed the identity value.
        BiquadCoefficients boosted = BiquadCoefficients.peakingEq(SAMPLE_RATE, 1000.0, 6.0, 1.0);
        assertEquals(true, boosted.b0 > 1.0);
    }

    @Test
    public void peakingEq_negativeGain_cutsB0BelowOne() {
        BiquadCoefficients cut = BiquadCoefficients.peakingEq(SAMPLE_RATE, 1000.0, -6.0, 1.0);
        assertEquals(true, cut.b0 < 1.0);
    }

    @Test
    public void peakingEq_rejectsFrequencyAtOrAboveNyquist() {
        assertThrows(IllegalArgumentException.class,
                () -> BiquadCoefficients.peakingEq(SAMPLE_RATE, SAMPLE_RATE / 2.0, 3.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> BiquadCoefficients.peakingEq(SAMPLE_RATE, SAMPLE_RATE, 3.0, 1.0));
    }

    @Test
    public void peakingEq_rejectsNonPositiveFrequencyOrQ() {
        assertThrows(IllegalArgumentException.class,
                () -> BiquadCoefficients.peakingEq(SAMPLE_RATE, 0.0, 3.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> BiquadCoefficients.peakingEq(SAMPLE_RATE, 1000.0, 3.0, 0.0));
    }
}
