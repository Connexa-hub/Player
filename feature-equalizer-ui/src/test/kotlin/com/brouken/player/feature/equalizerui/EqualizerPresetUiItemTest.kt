package com.brouken.player.feature.equalizerui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class EqualizerPresetUiItemTest {

    @Test
    fun equals_isTrueForSameLabelAndGains() {
        val a = EqualizerPresetUiItem("Bass Boost", floatArrayOf(6f, 5f, 4f, 2f, 0f))
        val b = EqualizerPresetUiItem("Bass Boost", floatArrayOf(6f, 5f, 4f, 2f, 0f))
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun equals_isFalseForDifferentGains() {
        val a = EqualizerPresetUiItem("Bass Boost", floatArrayOf(6f, 5f, 4f, 2f, 0f))
        val b = EqualizerPresetUiItem("Bass Boost", floatArrayOf(6f, 5f, 4f, 2f, 1f))
        assertNotEquals(a, b)
    }

    @Test
    fun equals_isFalseForDifferentLabel() {
        val a = EqualizerPresetUiItem("Bass Boost", floatArrayOf(6f, 5f))
        val b = EqualizerPresetUiItem("Vocal", floatArrayOf(6f, 5f))
        assertNotEquals(a, b)
    }
}
