package dev.redfox.obsidian.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.theme.ObsidianTheme

/**
 * A round avatar on a neutral tile. Shows [image] when given (a photo loaded however the app
 * likes, filling the circle), else the initials of [name], else [fallbackIcon] (a person by
 * default; pass another glyph for senders whose name isn't a person's).
 */
@Composable
public fun ObAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    @DrawableRes fallbackIcon: Int = R.drawable.ob_ic_person,
    image: (@Composable () -> Unit)? = null,
) {
    val colors = ObsidianTheme.colors
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.tile),
        contentAlignment = Alignment.Center
    ) {
        val initials = name?.let(::initials).orEmpty()
        when {
            image != null -> image()
            initials.isNotEmpty() -> Text(
                initials,
                style = ObsidianTheme.typography.labelStrong.copy(fontSize = (size.value * 0.36f).sp),
                color = colors.textSecondary,
                modifier = Modifier.offset(y = (-0.5).dp),
            )
            else -> Icon(
                painterResource(fallbackIcon),
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(size * 0.5f),
            )
        }
    }
}

/**
 * Two overlapping avatars in the space of one (a group): [back] at the top end, [front] at the
 * bottom start, ringed in the background colour so the circles read as separate. Each slot gets
 * the size its avatar should be.
 */
@Composable
public fun ObAvatarPair(
    front: @Composable (Modifier, Dp) -> Unit,
    back: @Composable (Modifier, Dp) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val small = size * 0.72f
    Box(modifier.size(size)) {
        back(Modifier.align(Alignment.TopEnd), small)
        front(
            Modifier
                .align(Alignment.BottomStart)
                .border(2.dp, ObsidianTheme.colors.background, CircleShape),
            small,
        )
    }
}

/** Up to two initials: "Ada Lovelace" → "AL", "jean-luc" → "JL"; empty when there are no letters or digits. */
internal fun initials(name: String): String =
    name.split(' ', '-', '.')
        .filter { it.isNotBlank() && it.first().isLetterOrDigit() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
