package dev.redfox.obsidian.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * A square cell in a photo grid. [content] draws the image (any loader), filling the tile.
 * [label] goes in the bottom corner on a soft scrim (a video's "0:42"). Long-press usually
 * starts [selectionMode]: each tile shows a ring, and a [selected] tile shrinks inside its cell
 * with a check, so a selection is easy to see at a glance.
 */
@Composable
public fun MediaTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onLongClick: (() -> Unit)? = null,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    label: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = ObsidianTheme.colors
    val inset by animateFloatAsState(if (selected) 0.86f else 1f, ObMotion.snappy(), label = "tileInset")
    val corner by animateDpAsState(if (selected) 14.dp else 4.dp, ObMotion.snappy(), label = "tileCorner")
    Box(
        modifier
            .aspectRatio(1f)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
                if (selectionMode) this.selected = selected
            }
            .pressable(pressedScale = 0.96f, onLongClick = onLongClick, onClick = onClick)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = inset
                    scaleY = inset
                }
                .clip(RoundedCornerShape(corner))
                .background(colors.tile),
            content = content,
        )
        if (label != null) {
            Text(
                label,
                style = ObsidianTheme.typography.labelStrong,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        AnimatedVisibility(
            visible = selectionMode,
            enter = scaleIn(ObMotion.bouncy(), initialScale = 0.5f) + fadeIn(),
            exit = scaleOut(targetScale = 0.5f) + fadeOut(),
            modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
        ) {
            SelectionMark(selected)
        }
    }
}

/** Ring when not selected, an accent disc with a check when selected. */
@Composable
private fun SelectionMark(selected: Boolean) {
    val colors = ObsidianTheme.colors
    val fill by animateColorAsState(if (selected) colors.accent else Color.Black.copy(alpha = 0.25f), ObMotion.snappy(), label = "markFill")
    Box(
        Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(fill)
            .border(1.5.dp, if (selected) colors.accent else Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(selected, enter = scaleIn(ObMotion.bouncy(), initialScale = 0.3f) + fadeIn(), exit = scaleOut() + fadeOut()) {
            Icon(painterResource(R.drawable.ob_ic_check_bold), contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(16.dp))
        }
    }
}
