package com.brouken.player.core.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * A generously-rounded shape scale — floating controls and cards read as soft, tappable
 * surfaces rather than the sharp-cornered panels typical of legacy desktop players.
 */
object PlayerShapes {
    val extraSmall = RoundedCornerShape(8.dp)
    val small = RoundedCornerShape(12.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(24.dp)
    val extraLarge = RoundedCornerShape(32.dp)

    /** Fully round — the floating transport controls (play/pause, skip). */
    val pill = RoundedCornerShape(50)

    fun asMaterialShapes(): Shapes = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
    )
}
