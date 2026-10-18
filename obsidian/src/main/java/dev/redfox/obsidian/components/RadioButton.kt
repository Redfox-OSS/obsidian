package dev.redfox.obsidian.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.motion.popOnSelect
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * One choice in a set: a 24dp ring that fills with an accent dot when [selected]. The dot
 * bounces in; the ring takes the accent and a soft glow. Pass `onClick = null` when the whole
 * row is the tap target.
 */
@Composable
public fun ObRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = ObsidianTheme.colors
    val haptics = LocalHapticFeedback.current
    val ring by animateColorAsState(if (selected) colors.accent else colors.textDisabled, ObMotion.snappy(), label = "radioRing")
    val dot by animateFloatAsState(if (selected) 1f else 0f, ObMotion.bouncy(), label = "radioDot")
    Box(
        modifier
            .popOnSelect(selected, amount = 0.14f)
            .then(if (selected) Modifier.glow(colors.accent, CircleShape, ObGlows.Checkbox) else Modifier)
            .size(24.dp)
            .clip(CircleShape)
            .border(2.dp, ring, CircleShape)
            .then(
                if (onClick != null) Modifier.selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onClick()
                    },
                ) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(12.dp)
                .scale(dot)
                .clip(CircleShape)
                .background(colors.accent)
        )
    }
}
