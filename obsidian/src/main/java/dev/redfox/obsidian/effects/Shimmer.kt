package dev.redfox.obsidian.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.Offset
import dev.redfox.obsidian.theme.ObShapes
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * A soft band of light sweeping across, for content that's on its way (a photo loading, a list
 * not yet read). Drawn over whatever the element shows; the sweep is read in the draw phase only.
 */
@Composable
public fun Modifier.shimmer(): Modifier {
    val sweep = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "shimmerSweep",
    )
    val light = Color.White.copy(alpha = 0.07f)
    return drawWithContent {
        drawContent()
        val band = size.width * 0.6f
        val x = sweep.value * size.width
        drawRect(
            Brush.linearGradient(
                listOf(Color.Transparent, light, Color.Transparent),
                start = Offset(x - band, 0f),
                end = Offset(x + band, size.height),
            )
        )
    }
}

/** A `tile`-coloured stand-in that shimmers until the real content arrives. Give it a size. */
@Composable
public fun Placeholder(modifier: Modifier = Modifier, shape: Shape = ObShapes.Small) {
    Box(
        modifier
            .clip(shape)
            .background(ObsidianTheme.colors.tile)
            .shimmer()
    )
}
