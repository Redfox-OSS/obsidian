package dev.redfox.obsidian.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.chrisbanes.haze.rememberHazeState
import dev.redfox.obsidian.components.ObSnackbarHost
import dev.redfox.obsidian.components.ObSnackbarState
import dev.redfox.obsidian.effects.LocalHazeState
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.motion.ObTransitions
import dev.redfox.obsidian.motion.RevealScope
import dev.redfox.obsidian.navigation.FloatingNavBar
import dev.redfox.obsidian.navigation.FloatingNavItem
import dev.redfox.obsidian.navigation.floatingNavClearance
import dev.redfox.obsidian.navigation.rememberCompactNavState
import dev.redfox.obsidian.theme.ObsidianAccent
import dev.redfox.obsidian.theme.ObsidianNeutrals
import dev.redfox.obsidian.theme.ObsidianTheme

/** Snackbars for the whole catalog, shown above the nav bar. */
internal val LocalSnackbars = staticCompositionLocalOf { ObSnackbarState() }

/** The navigator, for screens that push or go back. */
internal val LocalNavigator = staticCompositionLocalOf { CatalogNavigator() }

/** The theme choices the Tokens tab changes. */
internal class ThemeChoice(
    val accent: ObsidianAccent,
    val neutrals: ObsidianNeutrals,
    val onAccent: (ObsidianAccent) -> Unit,
    val onNeutrals: (ObsidianNeutrals) -> Unit,
)

internal val LocalThemeChoice = compositionLocalOf<ThemeChoice> { error("No theme choice") }

/** Lets the media grid and the viewer share a photo across the transition. */
@OptIn(ExperimentalSharedTransitionApi::class)
internal val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CatalogApp(onExit: () -> Unit) {
    var accentIndex by rememberSaveable { mutableIntStateOf(0) }
    var neutralsIndex by rememberSaveable { mutableIntStateOf(0) }
    val accent = ObsidianAccent.Presets[accentIndex]
    val neutrals = ObsidianNeutrals.Presets[neutralsIndex]
    val choice = ThemeChoice(
        accent = accent,
        neutrals = neutrals,
        onAccent = { accentIndex = ObsidianAccent.Presets.indexOf(it).coerceAtLeast(0) },
        onNeutrals = { neutralsIndex = ObsidianNeutrals.Presets.indexOf(it).coerceAtLeast(0) },
    )
    val navigator = remember { CatalogNavigator() }
    val snackbars = remember { ObSnackbarState() }
    val hazeState = rememberHazeState()

    ObsidianTheme(accent = accent, neutrals = neutrals) {
        CompositionLocalProvider(
            LocalHazeState provides hazeState,
            LocalSnackbars provides snackbars,
            LocalNavigator provides navigator,
            LocalThemeChoice provides choice,
        ) {
            val compactNav = rememberCompactNavState(resetKey = navigator.backStack.lastOrNull())
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ObsidianTheme.colors.background)
                    .nestedScroll(compactNav.connection)
            ) {
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        NavDisplay(
                            backStack = navigator.backStack,
                            onBack = { if (!navigator.back()) onExit() },
                            entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                            transitionSpec = {
                                when (val move = navigator.lastMove) {
                                    is Move.TabSwitch -> ObTransitions.tab(move.forward)
                                    else -> ObTransitions.push()
                                }
                            },
                            popTransitionSpec = { ObTransitions.pop() },
                            predictivePopTransitionSpec = { ObTransitions.predictivePop() },
                            entryProvider = entryProvider {
                                entry<Destination.Root> { key ->
                                    Reveal(navigator) {
                                        when (key.tab) {
                                            Tab.Tokens -> TokensScreen()
                                            Tab.Components -> ComponentsScreen()
                                            Tab.Motion -> MotionScreen()
                                            Tab.Media -> MediaScreen()
                                        }
                                    }
                                }
                                entry<Destination.Components> { key -> Reveal(navigator) { CategoryScreen(key.category) } }
                                entry<Destination.PushedDemo> { key -> Reveal(navigator) { PushedDemoScreen(key.depth) } }
                                entry<Destination.Viewer> { key -> ViewerScreen(key.index) }
                            },
                        )
                    }
                }
                AnimatedVisibility(
                    visible = navigator.atRoot,
                    enter = fadeIn(tween(220)) + slideInVertically(ObMotion.gentle()) { it / 2 },
                    exit = fadeOut(tween(120)) + slideOutVertically(ObMotion.gentle()) { it / 2 },
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    FloatingNavBar(
                        items = Tab.entries.map { FloatingNavItem(it.icon, it.label, it.motion) },
                        selectedIndex = navigator.tab.ordinal,
                        onSelect = { navigator.select(Tab.entries[it]) },
                        compact = compactNav.compact,
                    )
                }
                ObSnackbarHost(
                    snackbars,
                    Modifier.align(Alignment.BottomCenter),
                    bottomPadding = if (navigator.atRoot) floatingNavClearance() else 24.dp,
                )
            }
        }
    }
}

/** Entrances play when a screen arrives, not when it's uncovered by going back. */
@Composable
private fun Reveal(navigator: CatalogNavigator, content: @Composable () -> Unit) {
    RevealScope(enabled = navigator.lastMove != Move.Pop, content = content)
}
