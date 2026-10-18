package dev.redfox.obsidian.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.glassEffect
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.effects.pressable
import dev.redfox.obsidian.motion.ObDurations
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.theme.ObSpacing
import dev.redfox.obsidian.theme.ObsidianTheme
import dev.redfox.obsidian.theme.ScreenGutter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/** How a snackbar went away. */
public enum class SnackbarResult { ActionPerformed, Dismissed }

/** What's on screen now: the message, its optional action, and how to answer it. */
@Stable
public class SnackbarMessage internal constructor(
    public val message: String,
    public val actionLabel: String?,
    private val result: CompletableDeferred<SnackbarResult>,
) {
    internal suspend fun await(): SnackbarResult = result.await()

    /** The action was tapped. */
    public fun performAction() {
        result.complete(SnackbarResult.ActionPerformed)
    }

    /** Gone without the action. */
    public fun dismiss() {
        result.complete(SnackbarResult.Dismissed)
    }
}

/**
 * Shows one snackbar at a time; later calls to [show] wait their turn. Hold it at the screen's
 * root (`remember { ObSnackbarState() }`) and place an [ObSnackbarHost] for it.
 */
@Stable
public class ObSnackbarState {
    private val mutex = Mutex()

    /** The snackbar being shown, or null. */
    public var current: SnackbarMessage? by mutableStateOf(null)
        private set

    /**
     * Shows [message] (with an [actionLabel] such as "Undo", if given) and suspends until it's
     * dismissed: tapped, or timed out after [durationMillis]. Without an action it's
     * a toast: a passing note.
     */
    public suspend fun show(
        message: String,
        actionLabel: String? = null,
        durationMillis: Long = if (actionLabel == null) 2_500L else 5_000L,
    ): SnackbarResult = mutex.withLock {
        val shown = SnackbarMessage(message, actionLabel, CompletableDeferred())
        current = shown
        try {
            withTimeoutOrNull(durationMillis) { shown.await() } ?: SnackbarResult.Dismissed
        } finally {
            current = null
        }
    }
}

/**
 * Where [state]'s snackbars appear: a floating frosted pill, centred near the bottom and lifted
 * by [bottomPadding] (pass `floatingNavClearance()` on tab screens). It rises and pops in; a tap
 * on the message dismisses it, a tap on the action performs it.
 */
@Composable
public fun ObSnackbarHost(
    state: ObSnackbarState,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(start = ScreenGutter, end = ScreenGutter, bottom = bottomPadding + ObSpacing.M),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedContent(
            targetState = state.current,
            transitionSpec = {
                (slideInVertically(ObMotion.gentle()) { it / 2 } + scaleIn(ObMotion.bouncy(), initialScale = 0.9f) + fadeIn(tween(ObDurations.Enter))) togetherWith
                    (slideOutVertically(ObMotion.gentle()) { it / 3 } + scaleOut(targetScale = 0.95f) + fadeOut(tween(ObDurations.Exit)))
            },
            contentAlignment = Alignment.BottomCenter,
            label = "snackbar",
        ) { shown ->
            if (shown != null) Snackbar(shown)
        }
    }
}

@Composable
private fun Snackbar(shown: SnackbarMessage) {
    val colors = ObsidianTheme.colors
    Row(
        Modifier
            .widthIn(max = 480.dp)
            .glow(Color.Black, CircleShape, ObGlows.Elevation)
            .clip(CircleShape)
            .glassEffect(colors.navScrim)
            .border(1.dp, colors.borderStrong, CircleShape)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .pressable(pressedScale = 0.98f, role = null) { shown.dismiss() }
            .padding(start = ObSpacing.XL, end = if (shown.actionLabel != null) ObSpacing.S else ObSpacing.XL, top = ObSpacing.S, bottom = ObSpacing.S),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ObSpacing.M),
    ) {
        Text(
            shown.message,
            style = ObsidianTheme.typography.body,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f, fill = false).padding(vertical = ObSpacing.S),
        )
        val action = shown.actionLabel
        if (action != null) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .pressable(pressedScale = 0.93f, haptic = HapticFeedbackType.ContextClick) { shown.performAction() }
                    .padding(horizontal = ObSpacing.M, vertical = ObSpacing.S),
            ) {
                Text(action, style = ObsidianTheme.typography.labelStrong, color = colors.accentLight)
            }
        }
    }
}
