package dev.redfox.obsidian.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme
import kotlin.math.roundToInt

private val TrackHeight = 4.dp
private val ThumbSize = 20.dp
private val ThumbPressedSize = 26.dp

/**
 * A slider: a hairline `tile` track, an accent fill up to the thumb, and a white thumb that
 * swells while held. Drag or tap anywhere on the track. With [steps] > 0 the value snaps to
 * `steps` evenly spaced points between the ends, with a tick of haptics on each.
 */
@Composable
public fun ObSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val colors = ObsidianTheme.colors
    val haptics = LocalHapticFeedback.current
    val latestChange by rememberUpdatedState(onValueChange)
    val latestFinished by rememberUpdatedState(onValueChangeFinished)
    var held by remember { mutableStateOf(false) }
    val span = valueRange.endInclusive - valueRange.start

    fun snap(fraction: Float): Float {
        val f = fraction.coerceIn(0f, 1f)
        return if (steps > 0) (f * (steps + 1)).roundToInt() / (steps + 1f) else f
    }
    val fraction = if (span == 0f) 0f else ((value - valueRange.start) / span).coerceIn(0f, 1f)
    val shown by animateFloatAsState(fraction, ObMotion.snappy(), label = "sliderValue")
    val thumb by animateDpAsState(if (held) ThumbPressedSize else ThumbSize, ObMotion.bouncy(), label = "sliderThumb")

    fun update(newFraction: Float) {
        val snapped = snap(newFraction)
        if (snapped != fraction) {
            if (steps > 0) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            latestChange(valueRange.start + snapped * span)
        }
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(ThumbPressedSize + 8.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange, steps)
                if (enabled) setProgress { target ->
                    latestChange(target.coerceIn(valueRange.start, valueRange.endInclusive))
                    true
                }
            }
            .then(
                if (enabled) Modifier
                    .pointerInput(steps, span) {
                        detectTapGestures(
                            onPress = { held = true; tryAwaitRelease(); held = false },
                            onTap = { offset ->
                                update(offset.x / size.width)
                                latestFinished?.invoke()
                            },
                        )
                    }
                    .pointerInput(steps, span) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset -> held = true; update(offset.x / size.width) },
                            onDragEnd = { held = false; latestFinished?.invoke() },
                            onDragCancel = { held = false },
                            onHorizontalDrag = { change, _ ->
                                change.consume()
                                update(change.position.x / size.width)
                            },
                        )
                    }
                else Modifier
            ),
    ) {
        val track = colors.tile
        val tick = colors.tileHigh
        val accent = colors.accent
        Canvas(Modifier.fillMaxWidth().height(ThumbPressedSize + 8.dp)) {
            val trackH = TrackHeight.toPx()
            val radius = thumb.toPx() / 2
            // The thumb travels between the track's rounded ends, never past them.
            val inset = ThumbPressedSize.toPx() / 2
            val usable = size.width - inset * 2
            val cx = inset + usable * shown
            val cy = size.height / 2
            val corner = CornerRadius(trackH / 2)
            drawRoundRect(track, Offset(inset, cy - trackH / 2), Size(usable, trackH), corner)
            drawRoundRect(accent, Offset(inset, cy - trackH / 2), Size(cx - inset, trackH), corner)
            if (steps > 0) {
                for (i in 1..steps) {
                    val x = inset + usable * i / (steps + 1f)
                    drawCircle(if (x <= cx) Color.White.copy(alpha = 0.5f) else tick, trackH / 3, Offset(x, cy))
                }
            }
            // Soft accent halo while held, then the thumb.
            if (held) drawCircle(accent.copy(alpha = 0.25f), radius + 6.dp.toPx(), Offset(cx, cy))
            drawCircle(Color.White, radius, Offset(cx, cy))
        }
    }
}
