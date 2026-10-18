package dev.redfox.obsidian.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.theme.ObSpacing
import dev.redfox.obsidian.theme.ObsidianTheme
import kotlinx.coroutines.delay

/** How long the pill says it's copied before showing the value again. */
private const val COPIED_MILLIS = 1800L

/**
 * "Copy 482913": one tap hands [value] to [onCopy] (put it on the clipboard there, for example
 * with [dev.redfox.obsidian.platform.copyToClipboard]), then the pill confirms with
 * [copiedLabel] for a moment. Made for one-time codes, but works for any short value.
 */
@Composable
public fun CopyPill(
    value: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Copy",
    copiedLabel: String = "Copied",
) {
    val colors = ObsidianTheme.colors
    var copied by remember(value) { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_MILLIS)
            copied = false
        }
    }
    Row(
        modifier
            .semantics { contentDescription = if (copied) copiedLabel else "$label $value" }
            .pressable(pressedScale = 0.96f, haptic = HapticFeedbackType.Confirm) {
                onCopy(value)
                copied = true
            }
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, if (copied) colors.accentBorder else colors.borderStrong, CircleShape)
            .padding(horizontal = ObSpacing.M, vertical = ObSpacing.S),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ObSpacing.S),
    ) {
        AnimatedContent(
            targetState = copied,
            transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(120)) },
            label = "copied",
        ) { done ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(ObSpacing.S)) {
                Icon(
                    painterResource(if (done) R.drawable.ob_ic_check_circle else R.drawable.ob_ic_copy),
                    contentDescription = null,
                    tint = colors.accentLight,
                    modifier = Modifier.size(16.dp),
                )
                if (done) {
                    Text(copiedLabel, style = ObsidianTheme.typography.labelStrong, color = colors.textPrimary)
                } else {
                    Text(label, style = ObsidianTheme.typography.labelStrong, color = colors.textSecondary)
                    Text(value, style = ObsidianTheme.typography.labelStrong, color = colors.textPrimary)
                }
            }
        }
    }
}
