package dev.redfox.obsidian.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.components.DetailHeaderHeight
import dev.redfox.obsidian.components.HeaderIconButton
import dev.redfox.obsidian.motion.ObDurations
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObsidianTheme
import dev.redfox.obsidian.theme.ScreenGutter

/**
 * Full-screen viewer for photos and videos: [content] on pure black, with a top bar (back,
 * [title], [actions]) and a [bottomBar] floating over soft gradients. Both slide away while
 * [chromeVisible] is false, so the photo has the screen; toggle it from a tap (see [Zoomable]).
 */
@Composable
public fun ViewerScaffold(
    chromeVisible: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize().background(Color.Black)) {
        content()
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(tween(ObDurations.Enter)) + slideInVertically(ObMotion.gentle()) { -it / 3 },
            exit = fadeOut(tween(ObDurations.Short)) + slideOutVertically(ObMotion.gentle()) { -it / 3 },
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(DetailHeaderHeight)
                        .padding(start = ScreenGutter - 12.dp, end = ScreenGutter - 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderIconButton(icon = R.drawable.ob_ic_back, contentDescription = "Back", onClick = onBack, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        title.orEmpty(),
                        style = ObsidianTheme.typography.headerTitle,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, content = actions)
                }
            }
        }
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(tween(ObDurations.Enter)) + slideInVertically(ObMotion.gentle()) { it / 3 },
            exit = fadeOut(tween(ObDurations.Short)) + slideOutVertically(ObMotion.gentle()) { it / 3 },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                    .padding(top = 24.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                bottomBar()
            }
        }
    }
}
