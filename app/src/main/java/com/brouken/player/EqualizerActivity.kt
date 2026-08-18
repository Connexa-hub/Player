package com.brouken.player

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.brouken.player.core.audiodsp.EqualizerPresets
import com.brouken.player.core.audiodsp.GraphicEqualizerBands
import com.brouken.player.core.design.PlayerTheme
import com.brouken.player.core.design.PlayerThemeMode
import com.brouken.player.feature.equalizerui.EqualizerBandUiState
import com.brouken.player.feature.equalizerui.EqualizerPresetUiItem
import com.brouken.player.feature.equalizerui.EqualizerScreen
import com.brouken.player.feature.equalizerui.EqualizerUiState

/**
 * Experimental (see `Prefs.useComposeControls`/`docs/TUTORIAL_COVERAGE.md`): the equalizer
 * settings screen. Reads and persists `Prefs.equalizerEnabled`/`equalizerBandGainsDb`, but does
 * **not** yet affect actual audio output — `core-audio-dsp`'s `GraphicEqualizerAudioProcessor`
 * exists and is tested, but isn't wired into the playback pipeline yet (see that module's
 * class-level doc for exactly why that's a separate, deliberately later step).
 */
class EqualizerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = Prefs(this)

        val presets = listOf(
            EqualizerPresetUiItem("Flat", EqualizerPresets.FLAT),
            EqualizerPresetUiItem("Bass Boost", EqualizerPresets.BASS_BOOST),
            EqualizerPresetUiItem("Vocal", EqualizerPresets.VOCAL),
            EqualizerPresetUiItem("Treble Boost", EqualizerPresets.TREBLE_BOOST),
            EqualizerPresetUiItem("Bass & Treble", EqualizerPresets.BASS_AND_TREBLE),
        )

        setContent {
            PlayerTheme(mode = PlayerThemeMode.Dark) {
                var enabled by remember { mutableStateOf(prefs.equalizerEnabled) }
                var gains by remember { mutableStateOf(prefs.equalizerBandGainsDb.toList()) }

                val uiState = EqualizerUiState(
                    enabled = enabled,
                    bands = gains.mapIndexed { index, gainDb ->
                        EqualizerBandUiState(
                            label = formatFrequencyLabel(GraphicEqualizerBands.FREQUENCIES_HZ[index]),
                            gainDb = gainDb,
                            minGainDb = GraphicEqualizerBands.MIN_GAIN_DB.toFloat(),
                            maxGainDb = GraphicEqualizerBands.MAX_GAIN_DB.toFloat(),
                        )
                    },
                )

                EqualizerScreen(
                    state = uiState,
                    presets = presets,
                    onEnabledChange = { newEnabled ->
                        enabled = newEnabled
                        prefs.updateEqualizer(newEnabled, gains.toFloatArray())
                    },
                    onBandGainChange = { bandIndex, gainDb ->
                        gains = gains.toMutableList().also { it[bandIndex] = gainDb }
                        prefs.updateEqualizer(enabled, gains.toFloatArray())
                    },
                    onPresetSelected = { preset ->
                        gains = preset.gainsDb.toList()
                        prefs.updateEqualizer(enabled, gains.toFloatArray())
                    },
                )
            }
        }
    }

    private fun formatFrequencyLabel(hz: Double): String {
        return if (hz >= 1000.0) {
            val khz = hz / 1000.0
            if (khz == khz.toLong().toDouble()) "${khz.toLong()}kHz" else "${khz}kHz"
        } else {
            "${hz.toLong()}Hz"
        }
    }
}
