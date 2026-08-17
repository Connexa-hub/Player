package com.brouken.player.core.audiodsp;

/**
 * Computes biquad filter coefficients for a peaking (bell) EQ band, using the standard formulas
 * from Robert Bristow-Johnson's "Audio EQ Cookbook" (the de-facto reference used by nearly every
 * software parametric/graphic equalizer — these formulas are public and have been cross-checked
 * against the cookbook's published equations, not derived from scratch).
 *
 * Direct Form I transfer function:
 * <pre>
 *   y[n] = (b0/a0)*x[n] + (b1/a0)*x[n-1] + (b2/a0)*x[n-2]
 *                       - (a1/a0)*y[n-1] - (a2/a0)*y[n-2]
 * </pre>
 * This class returns coefficients already normalized by {@code a0} (so {@code a0 == 1} and can
 * be ignored by the caller), which is what {@link BiquadFilter} expects.
 */
public final class BiquadCoefficients {

    public final double b0, b1, b2, a1, a2;

    private BiquadCoefficients(double b0, double b1, double b2, double a1, double a2) {
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
        this.a1 = a1;
        this.a2 = a2;
    }

    /**
     * @param sampleRateHz  audio sample rate, e.g. 44100 or 48000
     * @param centerFreqHz  band center frequency; must be less than {@code sampleRateHz / 2}
     *                      (the Nyquist frequency) or the filter becomes unstable
     * @param gainDb        boost/cut in decibels; 0 = no change
     * @param q             quality factor (bandwidth); higher = narrower band. 1.0 is a
     *                      reasonable default for a graphic EQ band.
     */
    public static BiquadCoefficients peakingEq(double sampleRateHz, double centerFreqHz, double gainDb, double q) {
        if (centerFreqHz <= 0 || centerFreqHz >= sampleRateHz / 2.0) {
            throw new IllegalArgumentException(
                    "centerFreqHz (" + centerFreqHz + ") must be in (0, sampleRateHz/2=" + (sampleRateHz / 2.0) + ")");
        }
        if (q <= 0) {
            throw new IllegalArgumentException("q must be > 0");
        }

        double a = Math.pow(10.0, gainDb / 40.0);
        double w0 = 2.0 * Math.PI * centerFreqHz / sampleRateHz;
        double cosW0 = Math.cos(w0);
        double sinW0 = Math.sin(w0);
        double alpha = sinW0 / (2.0 * q);

        double rawB0 = 1.0 + alpha * a;
        double rawB1 = -2.0 * cosW0;
        double rawB2 = 1.0 - alpha * a;
        double rawA0 = 1.0 + alpha / a;
        double rawA1 = -2.0 * cosW0;
        double rawA2 = 1.0 - alpha / a;

        // Normalize so a0 = 1
        return new BiquadCoefficients(
                rawB0 / rawA0,
                rawB1 / rawA0,
                rawB2 / rawA0,
                rawA1 / rawA0,
                rawA2 / rawA0);
    }

    /** The identity filter: passes audio through completely unchanged (0 dB, all bands). */
    public static BiquadCoefficients identity() {
        return new BiquadCoefficients(1.0, 0.0, 0.0, 0.0, 0.0);
    }
}
