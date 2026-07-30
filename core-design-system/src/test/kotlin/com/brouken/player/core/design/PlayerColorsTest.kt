package com.brouken.player.core.design

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PlayerColorsTest {

    @Test
    fun amoledBlack_isTruePureBlack() {
        // The whole point of an AMOLED theme is pixels-off black, not "very dark gray".
        assertEquals(0xFF000000.toInt(), PlayerColors.AmoledBlack.toArgb())
    }

    @Test
    fun neutral0_isNotAmoledBlack() {
        // Default dark theme intentionally isn't pure black (see Color.kt comment) —
        // this pins that distinction so a future edit can't accidentally merge the two.
        assertNotEquals(PlayerColors.Neutral0.toArgb(), PlayerColors.AmoledBlack.toArgb())
    }

    @Test
    fun accentGradient_usesTwoDistinctBrandColors() {
        assertNotEquals(PlayerColors.AccentViolet.toArgb(), PlayerColors.AccentCyan.toArgb())
    }
}
