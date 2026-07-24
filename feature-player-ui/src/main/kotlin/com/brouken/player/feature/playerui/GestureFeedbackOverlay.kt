package com.brouken.player.feature.playerui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.GlassSurface
import com.brouken.player.core.design.PlayerShapes
import com.brouken.player.core.design.PlayerTypography

/**
 * One overlay used for both brightness and volume swipe feedback — the caller picks the icon
 * and label text (e.g. "☀ 18" / "🔊 70%"); this composable only owns the glass pill, fade
 * in/out, and layout. Replaces the legacy `CustomPlayerView#setCustomErrorMessage` plain-text
 * toast with something that matches the rest of the new UI.
 */
@Composable
fun GestureFeedbackOverlay(
    visible: Boolean,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(120)),
        exit = fadeOut(animationSpec = tween(180)),
        modifier = modifier,
    ) {
        GlassSurface(shape = PlayerShapes.large) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = label,
                    style = PlayerTypography.headline,
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
    }
}
