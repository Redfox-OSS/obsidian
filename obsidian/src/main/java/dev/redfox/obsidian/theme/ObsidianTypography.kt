package dev.redfox.obsidian.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.redfox.obsidian.R

/** Inter, from a single variable TTF (google/fonts, OFL) instantiated at each weight we use. */
@OptIn(ExperimentalTextApi::class)
private fun inter(weight: FontWeight) = Font(
    resId = R.font.ob_inter_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

public val InterFontFamily: FontFamily = FontFamily(
    inter(FontWeight.Light),
    inter(FontWeight.Normal),
    inter(FontWeight.Medium),
    inter(FontWeight.SemiBold),
    inter(FontWeight.Bold),
)

/** Type scale of the Obsidian design system. */
@Immutable
public data class ObsidianTypography(
    /** Large-page header title ("Messages", "Settings"). */
    val screenTitle: TextStyle,
    /** Hero number: a time, a result, a total. Light weight, tight tracking. */
    val numericLarge: TextStyle,
    /** Digits inside picker tiles and keypad keys. */
    val numericTile: TextStyle,
    /** Secondary number (nested rows). */
    val numericMedium: TextStyle,
    /** Number in compact rows. */
    val numericSmall: TextStyle,
    /** Unit or suffix beside a number: AM/PM, %, kg. */
    val unit: TextStyle,
    /** Card titles. */
    val title: TextStyle,
    /** Detail-screen header title. */
    val headerTitle: TextStyle,
    /** Row titles in form cards. */
    val bodyLarge: TextStyle,
    val body: TextStyle,
    /** Settings row title, chips, small buttons. */
    val label: TextStyle,
    val labelStrong: TextStyle,
    /** Tiny uppercase eyebrow text ("NEXT ALARM", "PINNED"). */
    val caption: TextStyle,
    /** Uppercase section label above a form section ("REPEAT", "NOTIFICATIONS"). */
    val sectionLabel: TextStyle,
    /** Full-width CTA label. */
    val button: TextStyle,
) {
    @Deprecated("Renamed to numericLarge.", ReplaceWith("numericLarge"))
    public val timeLarge: TextStyle get() = numericLarge

    @Deprecated("Renamed to numericTile.", ReplaceWith("numericTile"))
    public val timeTile: TextStyle get() = numericTile

    @Deprecated("Renamed to numericMedium.", ReplaceWith("numericMedium"))
    public val timeMedium: TextStyle get() = numericMedium

    @Deprecated("Renamed to numericSmall.", ReplaceWith("numericSmall"))
    public val timeSmall: TextStyle get() = numericSmall

    @Deprecated("Renamed to unit.", ReplaceWith("unit"))
    public val meridiem: TextStyle get() = unit

    public companion object {
        public val Default: ObsidianTypography = obsidianTypography()
    }
}

/** Builds the type scale on [fontFamily] (Inter by default) — swap the face, keep the rhythm. */
public fun obsidianTypography(fontFamily: FontFamily = InterFontFamily): ObsidianTypography = ObsidianTypography(
    screenTitle = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.5).sp),
    numericLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-0.05).em),
    numericTile = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 44.sp, lineHeight = 44.sp, letterSpacing = (-0.025).em),
    numericMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Light, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.025).em),
    numericSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.025).em),
    unit = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.02.em),
    title = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    headerTitle = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.4).sp),
    bodyLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    body = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    label = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.04.em),
    labelStrong = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.04.em),
    caption = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.05.em),
    sectionLabel = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.05.em),
    button = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp),
)

public val LocalObsidianTypography: ProvidableCompositionLocal<ObsidianTypography> = staticCompositionLocalOf { ObsidianTypography.Default }

/** Material 3 type scale on [fontFamily], for the few stock M3 components (dialogs, menus, text fields). */
internal fun materialTypography(fontFamily: FontFamily): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = base.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = base.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = base.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = base.headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = base.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = base.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = base.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = base.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = base.labelSmall.copy(fontFamily = fontFamily),
    )
}
