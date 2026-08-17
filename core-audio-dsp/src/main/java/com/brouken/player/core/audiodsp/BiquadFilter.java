package com.brouken.player.core.audiodsp;

/**
 * A single-channel, stateful biquad filter (Direct Form I). Each audio channel needs its own
 * instance — the filter's history ({@code x1,x2,y1,y2}) must not be shared between channels, or
 * left/right audio would bleed into each other's filter state.
 */
public final class BiquadFilter {

    private BiquadCoefficients coefficients;

    // Filter history: x1/x2 = previous two input samples, y1/y2 = previous two output samples.
    private double x1, x2, y1, y2;

    public BiquadFilter(BiquadCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    /** Swaps the active coefficients (e.g. the user moved an EQ slider) without resetting history. */
    public void setCoefficients(BiquadCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    /** Resets filter history to silence. Call when playback seeks/restarts to avoid a click/pop. */
    public void reset() {
        x1 = x2 = y1 = y2 = 0.0;
    }

    /** Processes one sample and returns the filtered result. */
    public double process(double x0) {
        BiquadCoefficients c = coefficients;
        double y0 = c.b0 * x0 + c.b1 * x1 + c.b2 * x2 - c.a1 * y1 - c.a2 * y2;
        x2 = x1;
        x1 = x0;
        y2 = y1;
        y1 = y0;
        return y0;
    }
}
