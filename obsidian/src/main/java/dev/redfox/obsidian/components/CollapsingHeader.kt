package dev.redfox.obsidian.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.redfox.obsidian.effects.glassEffect
import dev.redfox.obsidian.theme.ObsidianTheme
import dev.redfox.obsidian.theme.ScreenGutter
import kotlin.math.roundToInt

/** Height of a [CollapsingHeader] at rest, below the status bar: pad scrolling content by this. */
public val CollapsingHeaderExpandedHeight: Dp = 112.dp

/** The big title's size relative to [LargeHeader]'s. */
private const val ExpandedTitleScale = 1.6f
private val TitleBottomGap = 12.dp
private val CollapseRange = CollapsingHeaderExpandedHeight - LargeHeaderHeight

/**
 * A frosted header with a big title that shrinks into a [LargeHeader] as the page scrolls
 * ("Photos", "Albums"). [collapse] reports 0 (expanded) to 1 (collapsed); get it from the
 * page's scroll state with [rememberHeaderCollapse]. Pad the page's content by status bar +
 * [CollapsingHeaderExpandedHeight] and mark it `glassSource()`; it scrolls up under the header
 * exactly as the header collapses.
 *
 * Height, title size and title position are applied in layout, so collapsing never
 * recomposes the header or re-measures the page.
 */
@Composable
public fun CollapsingHeader(
    title: String,
    collapse: () -> Float,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = ObsidianTheme.colors
    Layout(
        content = {
            Text(
                title,
                style = ObsidianTheme.typography.screenTitle,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(content = actions)
        },
        modifier = modifier
            .fillMaxWidth()
            .glassEffect(colors.headerScrim)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) { measurables, constraints ->
        val c = collapse().coerceIn(0f, 1f)
        val width = constraints.maxWidth
        val gutter = ScreenGutter.roundToPx()
        val bar = LargeHeaderHeight.roundToPx()
        val height = lerp(CollapsingHeaderExpandedHeight.toPx(), LargeHeaderHeight.toPx(), c).roundToInt()
        val actionsPlaceable = measurables[1].measure(Constraints(maxWidth = width / 2, maxHeight = bar))
        // The title may use the full width while big, and must clear the actions once small.
        val titleRoom = lerp(
            (width - gutter * 2) / ExpandedTitleScale,
            (width - gutter * 2 - actionsPlaceable.width).toFloat(),
            c,
        ).roundToInt().coerceAtLeast(0)
        val titlePlaceable = measurables[0].measure(Constraints(maxWidth = titleRoom))
        layout(width, height) {
            val scale = lerp(ExpandedTitleScale, 1f, c)
            val expandedY = height - TitleBottomGap.toPx() - titlePlaceable.height * ExpandedTitleScale
            val collapsedY = (bar - titlePlaceable.height) / 2f
            titlePlaceable.placeWithLayer(gutter, lerp(expandedY, collapsedY, c).roundToInt()) {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
            actionsPlaceable.place(width - (ScreenGutter - 8.dp).roundToPx() - actionsPlaceable.width, (bar - actionsPlaceable.height) / 2)
        }
    }
}

/** How collapsed a [CollapsingHeader] over this list should be. */
@Composable
public fun rememberHeaderCollapse(state: LazyListState): () -> Float {
    val range = with(LocalDensity.current) { CollapseRange.toPx() }
    return remember(state, range) {
        { if (state.firstVisibleItemIndex > 0) 1f else state.firstVisibleItemScrollOffset / range }
    }
}

/** How collapsed a [CollapsingHeader] over this grid should be. */
@Composable
public fun rememberHeaderCollapse(state: LazyGridState): () -> Float {
    val range = with(LocalDensity.current) { CollapseRange.toPx() }
    return remember(state, range) {
        { if (state.firstVisibleItemIndex > 0) 1f else state.firstVisibleItemScrollOffset / range }
    }
}

/** How collapsed a [CollapsingHeader] over this scrolling column should be. */
@Composable
public fun rememberHeaderCollapse(state: ScrollState): () -> Float {
    val range = with(LocalDensity.current) { CollapseRange.toPx() }
    return remember(state, range) { { state.value / range } }
}
