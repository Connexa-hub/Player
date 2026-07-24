package com.brouken.player.core.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The floating "glass" card used for transport controls, the seek scrubber pill, and toast-style
 * overlays (brightness/volume) that sit on top of video content. Combines a background blur of
 * whatever's behind it with a low-opacity fill and a hairline stroke, which is what actually
 * sells "glass" — blur alone on a dark video frame just looks like a dark blur.
 *
 * [Modifier.blur] only samples an actual [android.graphics.RenderEffect] on API 31+; on older
 * devices Compose silently skips the blur (see androidx.compose.ui docs), so this composable
 * intentionally leans on [PlayerColors.GlassFillDark] + [PlayerColors.GlassStrokeDark] to still
 * read as a distinct translucent layer even with no blur applied underneath.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = PlayerShapes.large,
    blurRadius: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .blur(radius = blurRadius)
            .background(PlayerColors.GlassFillDark, shape)
            .border(width = 1.dp, color = PlayerColors.GlassStrokeDark, shape = shape),
    ) {
        content()
    }
}
