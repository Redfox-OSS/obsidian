package dev.redfox.obsidian.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Color tokens of the Obsidian design system. Always dark: neutrals come from an
 * [ObsidianNeutrals] ramp, accent roles from an [ObsidianAccent]. Build with [obsidianColors].
 */
@Immutable
public data class ObsidianColors(
    // Surfaces
    val background: Color,
    /** Primary card surface (cards, settings sections). */
    val card: Color,
    /** Translucent card surface used over blurred/scrolling content. */
    val cardGlass: Color,
    /** Inset rows inside a card and standalone form rows. */
    val cardInner: Color,
    /** Icon tiles, inactive tiles. */
    val tile: Color,
    /** Highest neutral — selection pill backgrounds, pressed states. */
    val tileHigh: Color,
    val border: Color,
    val borderStrong: Color,
    val divider: Color,
    /** Tint painted over the blurred backdrop of a top header. */
    val headerScrim: Color,
    /** Tint painted over the blurred backdrop of the floating nav bar. */
    val navScrim: Color,

    // Content
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,

    // Accent
    val accent: Color,
    val accentLight: Color,
    /** Content on [accent] (primary CTA label). */
    val onAccent: Color,
    /** Content on [accentLight]. */
    val onAccentLight: Color,
    val accentContainer: Color,
    val accentContainerStrong: Color,
    val accentBorder: Color,
    val accentGlow: Color,

    // Controls & status
    val switchTrackOff: Color,
    val switchThumbOff: Color,
    val warning: Color,
    val error: Color,
)

/**
 * An accent: a vivid [main] tone (CTAs, switch tracks, glows), a pale [light] tone (selected
 * text, icons on containers), and readable content colors on each. [swatch] is the representative
 * hue to show in an accent picker.
 */
@Immutable
public data class ObsidianAccent(
    val name: String,
    val main: Color,
    val light: Color,
    val onMain: Color,
    val onLight: Color,
    val swatch: Color = main,
) {
    public companion object {
        public val Crimson: ObsidianAccent = ObsidianAccent("Crimson", Color(0xFFFF535A), Color(0xFFFFB3B0), Color(0xFF5B000D), Color(0xFF680010), Color(0xFFFF2A42))
        public val Electric: ObsidianAccent = ObsidianAccent("Electric", Color(0xFF3D9BFF), Color(0xFFA9CEFF), Color(0xFF002A55), Color(0xFF00315F), Color(0xFF0084FF))
        public val Solar: ObsidianAccent = ObsidianAccent("Solar", Color(0xFFFFB800), Color(0xFFFFD983), Color(0xFF3F2B00), Color(0xFF402D00), Color(0xFFFFB800))
        public val Emerald: ObsidianAccent = ObsidianAccent("Emerald", Color(0xFF21D07A), Color(0xFF8DF3B7), Color(0xFF00391B), Color(0xFF003920), Color(0xFF00E676))
        public val Luminous: ObsidianAccent = ObsidianAccent("Luminous", Color(0xFFEDEDF0), Color(0xFFFFFFFF), Color(0xFF1A1A1D), Color(0xFF1A1A1D), Color(0xFFFFFFFF))

        public val Presets: List<ObsidianAccent> = listOf(Crimson, Electric, Solar, Emerald, Luminous)
    }
}

/** A dark neutral ramp, from the page [background] up to the highest [tileHigh] surface. */
@Immutable
public data class ObsidianNeutrals(
    val name: String,
    val background: Color,
    val card: Color,
    val cardInner: Color,
    val tile: Color,
    val tileHigh: Color,
) {
    public companion object {
        public val Dark: ObsidianNeutrals = ObsidianNeutrals("Dark", Color(0xFF131316), Color(0xFF201F22), Color(0xFF1C1B1E), Color(0xFF2A2A2D), Color(0xFF353438))
        public val Darker: ObsidianNeutrals = ObsidianNeutrals("Darker", Color(0xFF0E0E11), Color(0xFF1A191D), Color(0xFF161519), Color(0xFF242328), Color(0xFF2F2E33))
        public val Amoled: ObsidianNeutrals = ObsidianNeutrals("AMOLED", Color(0xFF000000), Color(0xFF121114), Color(0xFF0D0C0F), Color(0xFF1E1D21), Color(0xFF29282D))

        public val Presets: List<ObsidianNeutrals> = listOf(Dark, Darker, Amoled)
    }
}

/** Neutral secondary-text base; blended with the accent so labels pick up a subtle tint (#E7BCBA for Crimson). */
private val SecondaryTextBase = Color(0xFFCBC7CC)

/** Derives the full token set from an accent and a neutral ramp. */
public fun obsidianColors(
    accent: ObsidianAccent = ObsidianAccent.Crimson,
    neutrals: ObsidianNeutrals = ObsidianNeutrals.Dark,
): ObsidianColors {
    val n = neutrals
    val textSecondary = lerp(SecondaryTextBase, accent.light, 0.45f)
    return ObsidianColors(
        background = n.background,
        card = n.card,
        cardGlass = n.card.copy(alpha = 0.7f),
        cardInner = n.cardInner,
        tile = n.tile,
        tileHigh = n.tileHigh,
        border = Color.White.copy(alpha = 0.05f),
        borderStrong = Color.White.copy(alpha = 0.10f),
        divider = n.tileHigh.copy(alpha = 0.4f),
        headerScrim = n.background.copy(alpha = 0.8f),
        navScrim = n.card.copy(alpha = 0.8f),
        textPrimary = Color(0xFFE5E1E5),
        textSecondary = textSecondary,
        textTertiary = textSecondary.copy(alpha = 0.7f),
        textDisabled = Color(0xFF52525B),
        accent = accent.main,
        accentLight = accent.light,
        onAccent = accent.onMain,
        onAccentLight = accent.onLight,
        accentContainer = accent.main.copy(alpha = 0.20f),
        accentContainerStrong = accent.main.copy(alpha = 0.25f),
        accentBorder = accent.light.copy(alpha = 0.30f),
        accentGlow = accent.main.copy(alpha = 0.40f),
        switchTrackOff = Color(0xFF474952),
        switchThumbOff = Color(0xFFB7B8C2),
        warning = Color(0xFFF59E0B),
        error = Color(0xFFFFB4AB),
    )
}

public val LocalObsidianColors: ProvidableCompositionLocal<ObsidianColors> = staticCompositionLocalOf { obsidianColors() }
