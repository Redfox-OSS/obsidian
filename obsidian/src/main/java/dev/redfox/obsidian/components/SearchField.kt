package dev.redfox.obsidian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.theme.ObSpacing
import dev.redfox.obsidian.theme.ObsidianTheme

private val FieldHeight = 56.dp

/**
 * A floating search pill: a search glyph, a single-line field showing [placeholder] while empty,
 * and a clear button once there's a [query]. The keyboard shows a search key, which calls
 * [onSearch]. Pass a [focusRequester] to open the keyboard straight away.
 */
@Composable
public fun ObSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {},
    clearLabel: String = "Clear search",
    focusRequester: FocusRequester? = null,
) {
    val colors = ObsidianTheme.colors
    val type = ObsidianTheme.typography
    Row(
        modifier
            .height(FieldHeight)
            .glow(Color.Black, CircleShape, ObGlows.Elevation)
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, colors.borderStrong, CircleShape)
            .padding(start = ObSpacing.L, end = ObSpacing.XS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ObSpacing.S),
    ) {
        Icon(painterResource(R.drawable.ob_ic_search), contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(22.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(placeholder, style = type.body, color = colors.textTertiary, maxLines = 1)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = type.body.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }, onDone = { onSearch() }, onGo = { onSearch() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            )
        }
        if (query.isNotEmpty()) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .pressable(pressedScale = 0.9f, haptic = HapticFeedbackType.ContextClick) { onQueryChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.ob_ic_close), contentDescription = clearLabel, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
            }
        } else {
            // Keeps the text clear of the pill's rounded end.
            Box(Modifier.size(ObSpacing.S))
        }
    }
}
