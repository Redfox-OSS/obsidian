package dev.redfox.obsidian.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.components.CollapsingHeader
import dev.redfox.obsidian.components.CollapsingHeaderExpandedHeight
import dev.redfox.obsidian.components.HeaderIconButton
import dev.redfox.obsidian.components.PillButton
import dev.redfox.obsidian.components.rememberHeaderCollapse
import dev.redfox.obsidian.effects.Placeholder
import dev.redfox.obsidian.effects.glassSource
import dev.redfox.obsidian.media.MediaTile
import dev.redfox.obsidian.media.ViewerScaffold
import dev.redfox.obsidian.media.ZoomState
import dev.redfox.obsidian.media.Zoomable
import dev.redfox.obsidian.navigation.floatingNavClearance
import dev.redfox.obsidian.theme.ObsidianTheme
import kotlinx.coroutines.launch

private const val PhotoCount = 60

/** Every eleventh tile is still "loading", to show the shimmer. */
private fun isLoading(index: Int) = index % 11 == 10

@Composable
internal fun MediaScreen() {
    val navigator = LocalNavigator.current
    val grid = rememberLazyGridState()
    var selected by remember { mutableStateOf(emptySet<Int>()) }
    val selecting = selected.isNotEmpty()
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxSize().background(ObsidianTheme.colors.background)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = grid,
            modifier = Modifier.glassSource().fillMaxSize(),
            contentPadding = PaddingValues(top = top + CollapsingHeaderExpandedHeight, bottom = floatingNavClearance() + 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(PhotoCount) { i ->
                MediaTile(
                    onClick = {
                        when {
                            selecting -> selected = if (i in selected) selected - i else selected + i
                            !isLoading(i) -> navigator.push(Destination.Viewer(i))
                        }
                    },
                    onLongClick = { selected = selected + i },
                    selectionMode = selecting,
                    selected = i in selected,
                    label = if (i % 7 == 3 && !isLoading(i)) "0:${(i * 7 % 50 + 10)}" else null,
                    contentDescription = "Photo ${i + 1}",
                ) {
                    if (isLoading(i)) Placeholder(Modifier.fillMaxSize(), RectangleShape)
                    else DemoPhoto(i, Modifier.fillMaxSize().sharedPhoto(i))
                }
            }
        }
        CollapsingHeader(
            title = if (selecting) "${selected.size} selected" else "Media",
            collapse = rememberHeaderCollapse(grid),
            actions = {
                AnimatedVisibility(selecting, enter = fadeIn(), exit = fadeOut()) {
                    PillButton("Done", onClick = { selected = emptySet() })
                }
            },
        )
    }
}

@Composable
internal fun ViewerScreen(index: Int) {
    val navigator = LocalNavigator.current
    val snackbars = LocalSnackbars.current
    val scope = rememberCoroutineScope()
    val pager = rememberPagerState(initialPage = index) { PhotoCount }
    val zooms = remember { mutableMapOf<Int, ZoomState>() }
    var chrome by remember { mutableStateOf(true) }
    ViewerScaffold(
        chromeVisible = chrome,
        onBack = { navigator.back() },
        title = "IMG_${2000 + pager.currentPage}",
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                HeaderIconButton(icon = R.drawable.ic_star, contentDescription = "Favourite", tint = Color.White, onClick = {
                    scope.launch { snackbars.show("Added to Favourites") }
                })
                HeaderIconButton(icon = R.drawable.ic_edit_square, contentDescription = "Edit", tint = Color.White, onClick = {})
                HeaderIconButton(icon = R.drawable.ic_delete, contentDescription = "Delete", tint = Color.White, onClick = {
                    scope.launch { snackbars.show("Photo deleted", actionLabel = "Undo") }
                })
            }
        },
    ) {
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxSize(),
            // A zoomed photo pans instead of paging.
            userScrollEnabled = zooms[pager.currentPage]?.isZoomed != true,
            beyondViewportPageCount = 1,
        ) { page ->
            val zoom = remember(page) { zooms.getOrPut(page) { ZoomState() } }
            Zoomable(Modifier.fillMaxSize(), state = zoom, onTap = { chrome = !chrome }) {
                DemoPhoto(page, Modifier.fillMaxWidth().aspectRatio(0.75f).sharedPhoto(page))
            }
        }
    }
}
