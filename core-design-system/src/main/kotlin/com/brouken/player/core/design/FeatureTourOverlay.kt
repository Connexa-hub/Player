package com.brouken.player.core.design

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * One step of a [FeatureTourOverlay] sequence: a short title + description explaining a single
 * piece of UI. Keep these to one idea each — a tour with ten dense steps doesn't get read.
 */
data class TourStep(
    val title: String,
    val description: String,
)

/**
 * A first-run, dismissible walkthrough shown as a centered glass card over a dimmed scrim.
 *
 * This is the **standard mechanism for introducing any new feature** in this app — every new
 * user-facing surface should get a [TourStep] list here and a one-time "have they seen it"
 * flag in `Prefs` (mirroring `Prefs.firstRun`/`markFirstRun()`), the same way the legacy XML UI
 * already does for its own onboarding via `TapTargetView`. Do not invent a new tutorial
 * mechanism per feature — extend this one, so the experience stays consistent as more features
 * are added.
 *
 * @param visible whether the tour is currently showing (caller controls this from a Prefs flag)
 * @param steps the ordered steps to walk through
 * @param onFinished called once, either when the user completes the last step or taps "Skip" —
 *   the caller should persist "seen" state here (e.g. `Prefs.markComposeControlsTourSeen()`)
 */
@Composable
fun FeatureTourOverlay(
    visible: Boolean,
    steps: List<TourStep>,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible || steps.isEmpty()) return

    var stepIndex by remember { mutableIntStateOf(0) }
    val isLastStep = stepIndex == steps.lastIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlayerColors.GlassScrimDark),
        contentAlignment = Alignment.Center,
    ) {
        GlassSurface(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = PlayerShapes.extraLarge,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                AnimatedContent(
                    targetState = stepIndex,
                    transitionSpec = {
                        (fadeIn(tween(180))) togetherWith (fadeOut(tween(120)))
                    },
                    label = "tour_step",
                ) { index ->
                    val step = steps[index]
                    Column {
                        Text(text = step.title, style = PlayerTypography.headline, color = Color.White)
                        Text(
                            text = step.description,
                            style = PlayerTypography.body,
                            color = PlayerColors.Neutral80,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StepDots(count = steps.size, currentIndex = stepIndex)

                    Row {
                        if (!isLastStep) {
                            TextButton(onClick = onFinished) {
                                Text("Skip", color = PlayerColors.Neutral80)
                            }
                        }
                        TextButton(onClick = {
                            if (isLastStep) onFinished() else stepIndex += 1
                        }) {
                            Text(
                                text = if (isLastStep) "Got it" else "Next",
                                color = PlayerColors.AccentViolet,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepDots(count: Int, currentIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == currentIndex) 8.dp else 6.dp)
                    .background(
                        color = if (i == currentIndex) PlayerColors.AccentViolet else PlayerColors.Neutral30,
                        shape = CircleShape,
                    ),
            )
        }
    }
}
