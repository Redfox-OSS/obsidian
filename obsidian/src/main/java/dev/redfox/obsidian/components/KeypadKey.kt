package dev.redfox.obsidian.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme

/** The role of a [KeypadKey], which sets its colours. */
public enum class KeypadKeyStyle {
    /** 0–9 and the decimal point: `card` with a light numeral. */
    Digit,
    /** Clear, ±, %, backspace: a raised neutral. */
    Function,
    /** ÷ × − +: an accent container; solid accent while it's the pending operation. */
    Operator,
    /** The one key that finishes (=): solid accent with a glow. One per keypad. */
    Primary,
}

/**
 * A calculator or dial-pad key. Give it a size (keypads are usually a grid of equal circles:
 * `Modifier.weight(1f).aspectRatio(1f)`). Shows [label], or [icon] when given (backspace).
 * An [KeypadKeyStyle.Operator] key with [selected] = true shows the operation waiting for its
 * second number. Each press taps the keyboard haptic.
 */
@Composable
public fun KeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: KeypadKeyStyle = KeypadKeyStyle.Digit,
    selected: Boolean = false,
    @DrawableRes icon: Int? = null,
    contentDescription: String = label,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = CircleShape,
) {
    val colors = ObsidianTheme.colors
    val type = ObsidianTheme.typography
    val solid = style == KeypadKeyStyle.Primary || (style == KeypadKeyStyle.Operator && selected)
    val background by animateColorAsState(
        when {
            solid -> colors.accent
            style == KeypadKeyStyle.Operator -> colors.accentContainerStrong
            style == KeypadKeyStyle.Function -> colors.tileHigh
            else -> colors.card
        },
        ObMotion.snappy(),
        label = "keyBackground",
    )
    val content by animateColorAsState(
        when {
            solid -> colors.onAccent
            style == KeypadKeyStyle.Operator -> colors.accentLight
            else -> colors.textPrimary
        },
        ObMotion.snappy(),
        label = "keyContent",
    )
    Box(
        modifier
            .semantics {
                this.contentDescription = contentDescription
                if (style == KeypadKeyStyle.Operator) this.selected = selected
            }
            .then(if (style == KeypadKeyStyle.Primary) Modifier.glow(colors.accent, shape, ObGlows.Action) else Modifier)
            .pressable(pressedScale = 0.9f, haptic = HapticFeedbackType.KeyboardTap, onLongClick = onLongClick, onClick = onClick)
            .clip(shape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(painterResource(icon), contentDescription = null, tint = content)
        } else {
            Text(
                label,
                style = if (style == KeypadKeyStyle.Function) type.title else type.numericMedium,
                color = content,
                maxLines = 1,
            )
        }
    }
}
