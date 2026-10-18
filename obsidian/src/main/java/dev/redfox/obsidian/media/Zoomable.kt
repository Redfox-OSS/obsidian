package dev.redfox.obsidian.media

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

/**
 * The zoom and pan of a [Zoomable]. [scale] is 1 at rest; [offset] is how far the content has
 * been moved from centre, in pixels. Hoist it to reset zoom when the page changes.
 */
@Stable
public class ZoomState(
    /** How far a pinch may enlarge the content. */
    public val maxScale: Float = 5f,
    /** Where a double tap zooms to. */
    public val doubleTapScale: Float = 2.5f,
) {
    public var scale: Float by mutableFloatStateOf(1f)
        private set
    public var offset: Offset by mutableStateOf(Offset.Zero)
        private set

    /** True when enlarged, so a pager should leave horizontal drags to the content. */
    public val isZoomed: Boolean get() = scale > 1.01f

    internal var size: Size = Size.Zero

    /** Applies one pinch/pan step, keeping the point under the fingers ([centroid]) in place. */
    internal fun transform(zoom: Float, pan: Offset, centroid: Offset) {
        val newScale = (scale * zoom).coerceIn(1f, maxScale)
        val center = Offset(size.width / 2, size.height / 2)
        val anchor = centroid - center
        val newOffset = anchor + pan - (anchor - offset) * (newScale / scale)
        scale = newScale
        offset = clamp(newOffset, newScale)
    }

    private fun clamp(value: Offset, atScale: Float): Offset {
        val maxX = size.width * (atScale - 1f) / 2f
        val maxY = size.height * (atScale - 1f) / 2f
        return Offset(value.x.coerceIn(-maxX, maxX), value.y.coerceIn(-maxY, maxY))
    }

    /** Animates back to 1×, centred. */
    public suspend fun reset() {
        animateTo(1f, Offset.Zero)
    }

    /** Double tap: zoom in on [tap], or back out if already zoomed. */
    internal suspend fun toggle(tap: Offset) {
        if (isZoomed) {
            reset()
        } else {
            val anchor = tap - Offset(size.width / 2, size.height / 2)
            animateTo(doubleTapScale, clamp(anchor - anchor * doubleTapScale, doubleTapScale))
        }
    }

    private suspend fun animateTo(targetScale: Float, targetOffset: Offset) {
        val startScale = scale
        val startOffset = offset
        animate(0f, 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 380f)) { t, _ ->
            scale = startScale + (targetScale - startScale) * t
            offset = startOffset + (targetOffset - startOffset) * t
        }
    }
}

/** A [ZoomState] kept across recompositions. */
@Composable
public fun rememberZoomState(maxScale: Float = 5f, doubleTapScale: Float = 2.5f): ZoomState =
    remember(maxScale, doubleTapScale) { ZoomState(maxScale, doubleTapScale) }

/**
 * Pinch to zoom, drag to pan while zoomed, double tap to zoom in on a spot (and back out). A
 * single-finger drag at 1× is left alone, so a pager around it still swipes between photos.
 * [onTap] gets single taps (toggle the viewer's chrome, for instance).
 */
@Composable
public fun Zoomable(
    modifier: Modifier = Modifier,
    state: ZoomState = rememberZoomState(),
    onTap: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier
            .clipToBounds()
            .pointerInput(state) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val fingers = event.changes.count { it.pressed }
                        if (fingers >= 2 || state.isZoomed) {
                            state.size = size.toSize()
                            state.transform(event.calculateZoom(), event.calculatePan(), event.calculateCentroid(useCurrent = true))
                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
            .pointerInput(state, onTap) {
                detectTapGestures(
                    onDoubleTap = { tap ->
                        state.size = size.toSize()
                        scope.launch { state.toggle(tap) }
                    },
                    onTap = { onTap?.invoke() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.graphicsLayer {
                scaleX = state.scale
                scaleY = state.scale
                translationX = state.offset.x
                translationY = state.offset.y
            },
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}
