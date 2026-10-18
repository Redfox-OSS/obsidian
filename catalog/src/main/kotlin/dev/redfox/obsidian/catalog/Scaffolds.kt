package dev.redfox.obsidian.catalog

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dev.redfox.obsidian.components.CollapsingHeader
import dev.redfox.obsidian.components.CollapsingHeaderExpandedHeight
import dev.redfox.obsidian.components.DetailPage
import dev.redfox.obsidian.components.rememberHeaderCollapse
import dev.redfox.obsidian.effects.glassSource
import dev.redfox.obsidian.motion.ObTransitions
import dev.redfox.obsidian.motion.RevealColumn
import dev.redfox.obsidian.navigation.floatingNavClearance
import dev.redfox.obsidian.theme.ObsidianTheme
import dev.redfox.obsidian.theme.ScreenGutter

/** A tab's root: a collapsing header over a scrolling column whose sections stagger in. */
@Composable
internal fun TabScreen(title: String, content: @Composable () -> Unit) {
    val scroll = rememberScrollState()
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxSize().background(ObsidianTheme.colors.background)) {
        RevealColumn(
            modifier = Modifier
                .glassSource()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = ScreenGutter)
                .padding(top = top + CollapsingHeaderExpandedHeight + 8.dp, bottom = floatingNavClearance() + 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            content = content,
        )
        CollapsingHeader(title = title, collapse = rememberHeaderCollapse(scroll))
    }
}

/** A pushed page: a detail header with back over a scrolling column. */
@Composable
internal fun PushedScreen(title: String, content: @Composable () -> Unit) {
    val navigator = LocalNavigator.current
    DetailPage(title = title, onBack = { navigator.back() }) { padding ->
        RevealColumn(
            modifier = Modifier
                .glassSource()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = ScreenGutter),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            content = content,
        )
    }
}

/** Marks a demo photo so it travels between the media grid and the viewer. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun Modifier.sharedPhoto(index: Int): Modifier {
    val shared = LocalSharedTransitionScope.current ?: return this
    val animated = LocalNavAnimatedContentScope.current
    return with(shared) {
        this@sharedPhoto.sharedElement(
            sharedContentState = rememberSharedContentState("photo-$index"),
            animatedVisibilityScope = animated,
            boundsTransform = ObTransitions.sharedBounds,
        )
    }
}
