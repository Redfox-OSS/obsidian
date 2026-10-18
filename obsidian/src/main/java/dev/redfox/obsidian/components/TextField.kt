package dev.redfox.obsidian.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.motion.CollapseExit
import dev.redfox.obsidian.motion.ExpandEnter
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObShapes
import dev.redfox.obsidian.theme.ObSpacing
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * Text input on an inset `cardInner` surface. The hairline border lights up to the accent
 * while focused, or turns `error` when [isError]; [supportingText] sits underneath (a hint, or
 * what's wrong). Single-line fields are pills; multi-line ones are rounded cards that grow.
 */
@Composable
public fun ObTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    label: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    @DrawableRes leadingIcon: Int? = null,
    trailing: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = ObsidianTheme.colors
    val type = ObsidianTheme.typography
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val border by animateColorAsState(
        when {
            isError -> colors.error
            focused -> colors.accentBorder
            else -> colors.border
        },
        ObMotion.snappy(),
        label = "fieldBorder",
    )
    val shape = if (singleLine) ObShapes.Pill else ObShapes.Card
    Column(modifier.alpha(if (enabled) 1f else 0.4f), verticalArrangement = Arrangement.spacedBy(ObSpacing.XS)) {
        if (label != null) {
            Text(label, style = type.labelStrong, color = colors.textSecondary, modifier = Modifier.padding(start = ObSpacing.L))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 52.dp)
                .clip(shape)
                .background(colors.cardInner)
                .border(1.dp, border, shape)
                .padding(horizontal = ObSpacing.L, vertical = ObSpacing.M),
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(ObSpacing.M),
        ) {
            if (leadingIcon != null) {
                Icon(painterResource(leadingIcon), contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
            }
            Box(Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder != null) {
                    Text(placeholder, style = type.body, color = colors.textTertiary, maxLines = if (singleLine) 1 else Int.MAX_VALUE)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = singleLine,
                    textStyle = type.body.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(if (isError) colors.error else colors.accent),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                    interactionSource = interaction,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            trailing?.invoke()
        }
        AnimatedVisibility(visible = supportingText != null, enter = ExpandEnter, exit = CollapseExit) {
            Text(
                supportingText.orEmpty(),
                style = type.label,
                color = if (isError) colors.error else colors.textTertiary,
                modifier = Modifier.padding(start = ObSpacing.L),
            )
        }
    }
}
