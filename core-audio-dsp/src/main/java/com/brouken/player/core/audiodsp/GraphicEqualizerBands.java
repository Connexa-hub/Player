package com.brouken.player.core.audiodsp;

/**
 * Standard 10-band graphic EQ layout using ISO-standard octave center frequencies — the same
 * band layout found on most hardware and software graphic equalizers, so presets and user
 * expectations transfer.
 */
public final class GraphicEqualizerBands {

    private GraphicEqualizerBands() {}

    public static final double[] FREQUENCIES_HZ = {
            31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000,
    };

    public static final int BAND_COUNT = FREQUENCIES_HZ.length;

    public static final double MIN_GAIN_DB = -12.0;
    public static final double MAX_GAIN_DB = 12.0;

    /** Reasonable default Q (bandwidth) for a graphic-EQ-style band — narrow enough to be musical. */
    public static final double DEFAULT_Q = 1.0;

    public static double clampGainDb(double gainDb) {
        return Math.max(MIN_GAIN_DB, Math.min(MAX_GAIN_DB, gainDb));
    }
}
