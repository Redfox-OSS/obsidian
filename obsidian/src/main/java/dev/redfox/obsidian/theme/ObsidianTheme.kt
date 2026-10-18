package dev.redfox.obsidian.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Root theme. Screens read tokens from [ObsidianTheme.colors] / [ObsidianTheme.typography];
 * a matching Material 3 scheme is also installed so the few stock M3 components left
 * (dialogs, dropdown menus, text fields, bottom sheets) blend in.
 */
@Composable
public fun ObsidianTheme(
    colors: ObsidianColors = obsidianColors(),
    typography: ObsidianTypography = ObsidianTypography.Default,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(colors) {
        darkColorScheme(
            primary = colors.accent,
            onPrimary = colors.onAccent,
            primaryContainer = colors.accentContainer,
            onPrimaryContainer = colors.accentLight,
            secondary = colors.accentLight,
            onSecondary = colors.onAccentLight,
            secondaryContainer = colors.tileHigh,
            onSecondaryContainer = colors.textPrimary,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.background,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.tile,
            onSurfaceVariant = colors.textSecondary,
            surfaceContainerLowest = colors.background,
            surfaceContainerLow = colors.cardInner,
            surfaceContainer = colors.card,
            surfaceContainerHigh = colors.card,
            surfaceContainerHighest = colors.tile,
            outline = colors.borderStrong,
            outlineVariant = colors.border,
            error = colors.error,
        )
    }
    val material = remember(typography) {
        materialTypography(typography.body.fontFamily ?: InterFontFamily)
    }

    CompositionLocalProvider(
        LocalObsidianColors provides colors,
        LocalObsidianTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = material,
            shapes = ObsidianMaterialShapes,
            content = content
        )
    }
}

/** Convenience overload: pick an accent + neutral ramp (both have presets on their companions). */
@Composable
public fun ObsidianTheme(
    accent: ObsidianAccent,
    neutrals: ObsidianNeutrals = ObsidianNeutrals.Dark,
    typography: ObsidianTypography = ObsidianTypography.Default,
    content: @Composable () -> Unit,
) {
    val colors = remember(accent, neutrals) { obsidianColors(accent, neutrals) }
    ObsidianTheme(colors = colors, typography = typography, content = content)
}

public object ObsidianTheme {
    public val colors: ObsidianColors
        @Composable @ReadOnlyComposable get() = LocalObsidianColors.current

    public val typography: ObsidianTypography
        @Composable @ReadOnlyComposable get() = LocalObsidianTypography.current
}

/** Squircle radii. */
public object ObShapes {
    public val Card: RoundedCornerShape = RoundedCornerShape(16.dp)
    public val CardLarge: RoundedCornerShape = RoundedCornerShape(24.dp)
    public val Tile: RoundedCornerShape = RoundedCornerShape(16.dp)
    public val Small: RoundedCornerShape = RoundedCornerShape(12.dp)
    public val Fab: RoundedCornerShape = RoundedCornerShape(18.dp)
    public val Checkbox: RoundedCornerShape = RoundedCornerShape(8.dp)
    public val Pill: RoundedCornerShape = CircleShape
}

/** Spacing scale. Every page uses [Gutter] as its horizontal edge padding. */
public object ObSpacing {
    public val XXS: Dp = 4.dp
    public val XS: Dp = 6.dp
    public val S: Dp = 8.dp
    public val M: Dp = 12.dp
    public val L: Dp = 16.dp
    public val Gutter: Dp = 20.dp
    public val XL: Dp = 24.dp
    public val XXL: Dp = 32.dp
}

/** Screen horizontal gutter used by every page. */
public val ScreenGutter: Dp = ObSpacing.Gutter

private val ObsidianMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
