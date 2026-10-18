package dev.redfox.obsidian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.motion.popOnSelect
import dev.redfox.obsidian.theme.ObsidianAccent
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * Accent picker: one swatch per accent in [accents]. The selected swatch gets the `Action` glow,
 * a ring, and pops as it becomes selected.
 */
@Composable
public fun AccentSwatchRow(
    selected: ObsidianAccent,
    onSelect: (ObsidianAccent) -> Unit,
    modifier: Modifier = Modifier,
    accents: List<ObsidianAccent> = ObsidianAccent.Presets,
) {
    val ring = ObsidianTheme.colors.textPrimary
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        accents.forEach { accent ->
            val isSelected = accent == selected
            Box(
                Modifier
                    .semantics {
                        contentDescription = accent.name
                        this.selected = isSelected
                    }
                    .popOnSelect(isSelected, amount = 0.12f)
                    .then(if (isSelected) Modifier.glow(accent.main, CircleShape, ObGlows.Action) else Modifier)
                    .pressable(pressedScale = 0.88f, role = Role.RadioButton, haptic = HapticFeedbackType.SegmentTick) {
                        onSelect(accent)
                    }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.swatch)
                    .border(2.dp, if (isSelected) ring else Color.Transparent, CircleShape)
            )
        }
    }
}
