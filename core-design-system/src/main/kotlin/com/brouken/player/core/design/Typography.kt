package com.brouken.player.core.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type scale tuned for a player UI: a confident, slightly condensed display weight for the
 * clock/time-remaining readout (the thing people glance at most), and a workmanlike body face
 * for menus, titles, and settings. Uses the platform default family (no custom font bundled
 * here) so the module has no font-asset dependency; swap [FontFamily.Default] for a bundled
 * variable font later without touching call sites.
 */
object PlayerTypography {

    private val base = FontFamily.Default

    val display = TextStyle(
        fontFamily = base,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
    )

    val headline = TextStyle(
        fontFamily = base,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
    )

    val title = TextStyle(
        fontFamily = base,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp,
    )

    val body = TextStyle(
        fontFamily = base,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )

    val label = TextStyle(
        fontFamily = base,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    )

    /** Monospaced-leaning numeric readout for the seek/time overlay — digits shouldn't jitter width. */
    val timecode = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )

    fun asMaterialTypography(): Typography = Typography(
        displayLarge = display,
        headlineMedium = headline,
        titleMedium = title,
        bodyMedium = body,
        labelMedium = label,
    )
}
