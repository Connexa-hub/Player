package com.brouken.player.feature.equalizerui

/**
 * What the equalizer screen needs to render, decoupled from `core-audio-dsp`'s
 * `GraphicEqualizerAudioProcessor` — same pattern as `PlayerControlsState` and `LibraryItemUi`
 * in the other feature-*-ui modules. The app module maps to/from the actual DSP engine and
 * `Prefs`.
 */
data class EqualizerUiState(
    val enabled: Boolean,
    /** One entry per band, in low-to-high frequency order. */
    val bands: List<EqualizerBandUiState>,
)

data class EqualizerBandUiState(
    /** Display label, e.g. "31Hz" or "16kHz" — formatting stays in the app module. */
    val label: String,
    val gainDb: Float,
    val minGainDb: Float,
    val maxGainDb: Float,
)

/** A named preset the user can apply in one tap — gains only, label is shown as-is. */
data class EqualizerPresetUiItem(
    val label: String,
    val gainsDb: FloatArray,
) {
    // FloatArray doesn't have useful equals()/hashCode() by default; data classes require
    // overriding both when they contain array properties, or IDE/lint (correctly) flags it.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EqualizerPresetUiItem) return false
        return label == other.label && gainsDb.contentEquals(other.gainsDb)
    }

    override fun hashCode(): Int {
        return 31 * label.hashCode() + gainsDb.contentHashCode()
    }
}
