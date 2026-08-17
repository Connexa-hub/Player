package com.brouken.player.core.audiodsp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EqualizerPresetsTest {

    @Test
    public void everyPreset_hasOneGainPerBand() {
        int n = GraphicEqualizerBands.BAND_COUNT;
        assertEquals(n, EqualizerPresets.FLAT.length);
        assertEquals(n, EqualizerPresets.BASS_BOOST.length);
        assertEquals(n, EqualizerPresets.VOCAL.length);
        assertEquals(n, EqualizerPresets.TREBLE_BOOST.length);
        assertEquals(n, EqualizerPresets.BASS_AND_TREBLE.length);
    }

    @Test
    public void flatPreset_isAllZero() {
        for (float gain : EqualizerPresets.FLAT) {
            assertEquals(0f, gain, 0f);
        }
    }

    @Test
    public void everyPresetGain_isWithinDeviceBounds() {
        float[][] presets = {
                EqualizerPresets.FLAT, EqualizerPresets.BASS_BOOST, EqualizerPresets.VOCAL,
                EqualizerPresets.TREBLE_BOOST, EqualizerPresets.BASS_AND_TREBLE,
        };
        for (float[] preset : presets) {
            for (float gain : preset) {
                assertEquals(true, gain >= GraphicEqualizerBands.MIN_GAIN_DB && gain <= GraphicEqualizerBands.MAX_GAIN_DB);
            }
        }
    }
}
