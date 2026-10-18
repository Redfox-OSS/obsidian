package dev.redfox.obsidian.motion

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset

/**
 * Screen-to-screen motion. Quiet on purpose: the new screen drifts a short way while it fades
 * in, the old one simply fades, so moving around feels quick rather than theatrical. The
 * movement is a spring, so predictive back can scrub it and a fast tap-back interrupts cleanly.
 *
 * Each is a [ContentTransform], so it plugs into Navigation 3's `NavDisplay`
 * (`transitionSpec = { ObTransitions.push() }`), Navigation Compose, or any `AnimatedContent`.
 */
public object ObTransitions {

    /** How far a screen drifts, as a share of the width. */
    private const val PushDrift = 12
    /** Tabs are siblings, so they drift less than a pushed screen. */
    private const val TabDrift = 24

    private val drift = spring(
        dampingRatio = 0.9f,
        stiffness = 240f,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    )

    /** Going deeper: the new screen drifts in from the end; the old one fades out. */
    public fun push(): ContentTransform =
        (slideInHorizontally(drift) { it / PushDrift } + fadeIn(tween(ObDurations.Enter))) togetherWith
            fadeOut(tween(ObDurations.Exit))

    /** Going back: the parent fades in while the closing screen drifts toward the end and fades. */
    public fun pop(): ContentTransform =
        fadeIn(tween(ObDurations.Enter)) togetherWith
            (slideOutHorizontally(drift) { it / PushDrift } + fadeOut(tween(ObDurations.Short)))

    /** The back gesture, scrubbed by the finger: the same motion as [pop]. */
    public fun predictivePop(): ContentTransform = pop()

    /**
     * Switching tabs: the new tab drifts in from the side it's on ([forward] = it's to the
     * right of the old one) while the old tab fades out.
     */
    public fun tab(forward: Boolean): ContentTransform {
        val sign = if (forward) 1 else -1
        return (slideInHorizontally(drift) { sign * it / TabDrift } + fadeIn(tween(ObDurations.Enter))) togetherWith
            fadeOut(tween(ObDurations.Exit))
    }

    /**
     * Shared-element bounds (a thumbnail growing into a full-screen photo): a soft spring with no
     * overshoot, so the image lands without wobbling. Pass to `Modifier.sharedBounds` /
     * `sharedElement` as `boundsTransform`.
     */
    @ExperimentalSharedTransitionApi
    public val sharedBounds: BoundsTransform = BoundsTransform { _, _ ->
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = 380f,
            visibilityThreshold = Rect.VisibilityThreshold,
        )
    }
}
