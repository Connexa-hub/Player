package com.brouken.player.feature.equalizerui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.GlassSurface
import com.brouken.player.core.design.PlayerColors
import com.brouken.player.core.design.PlayerShapes
import com.brouken.player.core.design.PlayerTypography
import kotlin.math.roundToInt

/**
 * The equalizer screen: master on/off, a row of preset chips, and one labeled slider per band.
 *
 * Deliberately horizontal sliders in a scrollable list, not the classic vertical EQ-bars look —
 * see the module's build notes for why (a rotated-Slider vertical layout has several easy-to-get
 * wrong details that can't be visually verified in this environment; horizontal sliders reuse
 * the exact pattern already proven in `feature-player-ui`'s `ScrubberTrack`).
 */
@Composable
fun EqualizerScreen(
    state: EqualizerUiState,
    presets: List<EqualizerPresetUiItem>,
    onEnabledChange: (Boolean) -> Unit,
    onBandGainChange: (bandIndex: Int, gainDb: Float) -> Unit,
    onPresetSelected: (EqualizerPresetUiItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = PlayerShapes.large,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Equalizer", style = PlayerTypography.headline, color = Color.White)
                Switch(
                    checked = state.enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(checkedTrackColor = PlayerColors.AccentViolet),
                )
            }
        }

        if (presets.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                presets.forEach { preset ->
                    FilterChip(
                        selected = false,
                        onClick = { onPresetSelected(preset) },
                        label = { Text(preset.label, style = PlayerTypography.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = PlayerColors.GlassFillDark,
                            labelColor = PlayerColors.Neutral80,
                        ),
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(state.bands.size) { index ->
                val band = state.bands[index]
                EqualizerBandRow(
                    band = band,
                    enabled = state.enabled,
                    onGainChange = { gain -> onBandGainChange(index, gain) },
                )
            }
        }
    }
}

@Composable
private fun EqualizerBandRow(
    band: EqualizerBandUiState,
    enabled: Boolean,
    onGainChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = band.label,
            style = PlayerTypography.label,
            color = PlayerColors.Neutral80,
            modifier = Modifier.width(56.dp),
        )
        Slider(
            value = band.gainDb,
            onValueChange = onGainChange,
            valueRange = band.minGainDb..band.maxGainDb,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = PlayerColors.AccentViolet,
                activeTrackColor = PlayerColors.AccentViolet,
                inactiveTrackColor = PlayerColors.GlassFillDark,
            ),
        )
        Text(
            text = "${if (band.gainDb >= 0) "+" else ""}${band.gainDb.roundToInt()}",
            style = PlayerTypography.timecode,
            color = Color.White,
            modifier = Modifier.width(40.dp),
        )
    }
}
