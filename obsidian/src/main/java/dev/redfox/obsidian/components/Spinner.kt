package dev.redfox.obsidian.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * For work of unknown length: an accent arc that spins while its length breathes, over a faint
 * ring. For work with a known amount, use [ObProgressBar]. Animation runs in the draw phase
 * only, so it never recomposes.
 */
@Composable
public fun ObSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.5.dp,
    contentDescription: String = "Loading",
) {
    val colors = ObsidianTheme.colors
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing)),
        label = "spinnerTurn",
    )
    val sweep = transition.animateFloat(
        initialValue = 40f,
        targetValue = 250f,
        animationSpec = infiniteRepeatable(tween(900, easing = ObMotion.EaseOutEmphasized), RepeatMode.Reverse),
        label = "spinnerSweep",
    )
    val ring = colors.tile
    val arc = colors.accent
    Canvas(
        modifier
            .size(size)
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            }
    ) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        drawCircle(ring, radius = (this.size.minDimension - stroke.width) / 2, style = Stroke(stroke.width))
        rotate(rotation.value) {
            drawArc(
                arc,
                startAngle = -90f,
                sweepAngle = sweep.value,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(stroke.width / 2, stroke.width / 2),
                size = androidx.compose.ui.geometry.Size(this.size.width - stroke.width, this.size.height - stroke.width),
                style = stroke,
            )
        }
    }
}
