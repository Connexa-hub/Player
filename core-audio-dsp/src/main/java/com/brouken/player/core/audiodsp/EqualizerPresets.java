package com.brouken.player.core.audiodsp;

/**
 * Built-in presets, one gain-in-dB value per band in {@link GraphicEqualizerBands#FREQUENCIES_HZ}
 * order (31Hz..16kHz). Values are conservative (within +/-6dB) to avoid clipping when combined
 * with the source material's own loudness — a user can always push individual bands further via
 * the manual sliders (once that UI exists; this milestone is the engine, not the screen).
 */
public final class EqualizerPresets {

    private EqualizerPresets() {}

    public static final float[] FLAT = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static final float[] BASS_BOOST = {6, 5, 4, 2, 0, 0, 0, 0, 0, 0};

    public static final float[] VOCAL = {-2, -1, 0, 2, 4, 4, 3, 1, 0, -1};

    public static final float[] TREBLE_BOOST = {0, 0, 0, 0, 0, 0, 1, 3, 5, 6};

    public static final float[] BASS_AND_TREBLE = {5, 4, 2, 0, -1, -1, 0, 2, 4, 5};

    static {
        // Guard against a future edit changing GraphicEqualizerBands.BAND_COUNT without updating
        // every preset here — a length mismatch would silently drop or misalign bands at runtime.
        int n = GraphicEqualizerBands.BAND_COUNT;
        assert FLAT.length == n && BASS_BOOST.length == n && VOCAL.length == n
                && TREBLE_BOOST.length == n && BASS_AND_TREBLE.length == n
                : "Preset length must match GraphicEqualizerBands.BAND_COUNT";
    }
}
