package dev.redfox.obsidian.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/** Ignore tiny deltas so slow drags and jitter don't make the bar flicker. */
private const val ScrollThresholdPx = 3f

/**
 * Whether the [FloatingNavBar] should be compact, worked out from scrolling. Attach
 * [connection] with `Modifier.nestedScroll` on the screen container and pass [compact] to the
 * bar.
 */
@Stable
public class CompactNavState internal constructor(private val state: MutableState<Boolean>) {
    /** True while content moves toward its end (finger swiping up). */
    public val compact: State<Boolean> get() = state

    internal fun expand() {
        state.value = false
    }

    /** Observes every vertical scroll beneath it. Consumes nothing, so lists behave as before. */
    public val connection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Only y is read, so horizontal swipes (filter chips) never flip the bar.
            if (available.y < -ScrollThresholdPx) state.value = true
            else if (available.y > ScrollThresholdPx) state.value = false
            return Offset.Zero
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            // Leftover downward delta = the list is already at its top: make sure it's expanded.
            if (available.y > 0f) state.value = false
            return Offset.Zero
        }
    }
}

/**
 * A [CompactNavState] that turns compact while content scrolls toward its end, and expands
 * when it scrolls back, overscrolls at the top, or [resetKey] (the current destination) changes.
 */
@Composable
public fun rememberCompactNavState(resetKey: Any?): CompactNavState {
    val state = remember { CompactNavState(mutableStateOf(false)) }
    LaunchedEffect(resetKey) { state.expand() }
    return state
}
