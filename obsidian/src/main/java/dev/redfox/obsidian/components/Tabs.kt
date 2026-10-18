package dev.redfox.obsidian.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme
import kotlin.math.abs
import kotlin.math.roundToInt

private val TabHeight = 44.dp
private val IndicatorHeight = 3.dp
private val IndicatorWidthShare = 0.4f

/**
 * Text tabs of equal width ("Photos", "Albums") with an accent bar under the selected one. The
 * bar travels between tabs on a spring and stretches with its speed, like the nav bar's halo.
 * For two or three short choices inside a form, [SegmentedPill] fits better.
 */
@Composable
public fun ObTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ObsidianTheme.colors
    val position = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        position.animateTo(selectedIndex.toFloat(), spring(dampingRatio = 0.7f, stiffness = 420f))
    }
    Box(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().height(TabHeight)) {
            tabs.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(if (selected) colors.textPrimary else colors.textSecondary, ObMotion.snappy(), label = "tabTint")
                Box(
                    Modifier
                        .weight(1f)
                        .height(TabHeight)
                        .semantics { this.selected = selected }
                        .pressable(pressedScale = 0.94f, role = Role.Tab, haptic = HapticFeedbackType.SegmentTick) { onSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = ObsidianTheme.typography.labelStrong, color = tint, maxLines = 1)
                }
            }
        }
        ObDivider(Modifier.align(Alignment.BottomStart))
        // The bar: sized and placed in layout, stretched in the layer, so a switch never recomposes.
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(IndicatorHeight)
                .layout { measurable, constraints ->
                    val slot = constraints.maxWidth.toFloat() / tabs.size.coerceAtLeast(1)
                    val width = (slot * IndicatorWidthShare).roundToInt()
                    val height = IndicatorHeight.roundToPx()
                    val placeable = measurable.measure(Constraints.fixed(width, height))
                    layout(constraints.maxWidth, height) {
                        val x = (slot * position.value + (slot - width) / 2f).roundToInt()
                        placeable.placeWithLayer(x, 0) {
                            scaleX = 1f + (abs(position.velocity) / 10f).coerceAtMost(0.6f)
                        }
                    }
                }
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(IndicatorHeight)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
        }
    }
}
