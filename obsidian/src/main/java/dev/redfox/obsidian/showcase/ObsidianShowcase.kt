package dev.redfox.obsidian.showcase

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.R
import dev.redfox.obsidian.components.AccentSwatchRow
import dev.redfox.obsidian.components.ActionPillButton
import dev.redfox.obsidian.components.ChoicePill
import dev.redfox.obsidian.components.CollapsingHeader
import dev.redfox.obsidian.components.CollapsingHeaderExpandedHeight
import dev.redfox.obsidian.components.CopyPill
import dev.redfox.obsidian.components.CountBadge
import dev.redfox.obsidian.components.DateDivider
import dev.redfox.obsidian.components.DayChip
import dev.redfox.obsidian.components.KeypadKey
import dev.redfox.obsidian.components.KeypadKeyStyle
import dev.redfox.obsidian.components.ObAvatar
import dev.redfox.obsidian.components.ObAvatarPair
import dev.redfox.obsidian.components.ObCard
import dev.redfox.obsidian.components.ObCheckbox
import dev.redfox.obsidian.components.ObDivider
import dev.redfox.obsidian.components.ObFab
import dev.redfox.obsidian.components.ObListRow
import dev.redfox.obsidian.components.ObProgressBar
import dev.redfox.obsidian.components.ObRadioButton
import dev.redfox.obsidian.components.ObSearchField
import dev.redfox.obsidian.components.ObSheet
import dev.redfox.obsidian.components.ObSlider
import dev.redfox.obsidian.components.ObSnackbarHost
import dev.redfox.obsidian.components.ObSnackbarState
import dev.redfox.obsidian.components.ObSpinner
import dev.redfox.obsidian.components.ObSwitch
import dev.redfox.obsidian.components.ObTabRow
import dev.redfox.obsidian.components.ObTextField
import dev.redfox.obsidian.components.PillButton
import dev.redfox.obsidian.components.PrimaryButton
import dev.redfox.obsidian.components.SectionLabel
import dev.redfox.obsidian.components.SegmentOption
import dev.redfox.obsidian.components.SegmentedPill
import dev.redfox.obsidian.components.SheetAction
import dev.redfox.obsidian.components.SwipeActionRow
import dev.redfox.obsidian.components.SwipeVisual
import dev.redfox.obsidian.components.rememberHeaderCollapse
import dev.redfox.obsidian.effects.GlowSpec
import dev.redfox.obsidian.effects.ObGlows
import dev.redfox.obsidian.effects.Placeholder
import dev.redfox.obsidian.effects.breathingGlow
import dev.redfox.obsidian.effects.glassSource
import dev.redfox.obsidian.effects.glow
import dev.redfox.obsidian.media.MediaTile
import dev.redfox.obsidian.media.ViewerScaffold
import dev.redfox.obsidian.media.Zoomable
import dev.redfox.obsidian.motion.ObTransitions
import dev.redfox.obsidian.motion.RevealColumn
import dev.redfox.obsidian.motion.RevealScope
import dev.redfox.obsidian.motion.RollingText
import dev.redfox.obsidian.theme.ObShapes
import dev.redfox.obsidian.theme.ObsidianAccent
import dev.redfox.obsidian.theme.ObsidianTheme
import dev.redfox.obsidian.theme.ScreenGutter
import kotlinx.coroutines.launch

/**
 * A living style guide: every token and component on one scrolling page. Drop it into a debug
 * screen of any app (inside an [ObsidianTheme]) to eyeball accents, glows and motion on-device.
 * [onAccentChange] lets the swatch row switch the host's accent; omit it for a read-only row.
 */
@Composable
public fun ObsidianShowcase(
    modifier: Modifier = Modifier,
    onAccentChange: ((ObsidianAccent) -> Unit)? = null,
) {
    val colors = ObsidianTheme.colors
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val scroll = rememberScrollState()
    val snackbars = remember { ObSnackbarState() }
    var viewerOpen by remember { mutableStateOf(false) }
    RevealScope {
        Box(modifier.fillMaxSize().background(colors.background)) {
            RevealColumn(
                modifier = Modifier
                    .glassSource()
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = ScreenGutter)
                    .padding(top = topInset + CollapsingHeaderExpandedHeight + 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AccentSection(onAccentChange)
                ColorSection()
                TypeSection()
                GlowSection()
                ControlsSection()
                ButtonsSection()
                FeedbackSection()
                ListSection()
                InputsSection()
                LoadingSection(snackbars)
                MediaSection(onOpenViewer = { viewerOpen = true })
                KeypadSection()
                TransitionsSection()
            }
            CollapsingHeader(title = "Obsidian", collapse = rememberHeaderCollapse(scroll))
            ObSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter).navigationBarsPadding())
            if (viewerOpen) DemoViewer(onBack = { viewerOpen = false })
        }
    }
}

@Composable
private fun AccentSection(onAccentChange: ((ObsidianAccent) -> Unit)?) {
    val colors = ObsidianTheme.colors
    val current = ObsidianAccent.Presets.firstOrNull { it.main == colors.accent } ?: ObsidianAccent.Presets.first()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Accent")
        AccentSwatchRow(selected = current, onSelect = { onAccentChange?.invoke(it) })
    }
}

@Composable
private fun ColorSection() {
    val c = ObsidianTheme.colors
    val swatches = listOf(
        "background" to c.background, "card" to c.card, "cardInner" to c.cardInner,
        "tile" to c.tile, "tileHigh" to c.tileHigh, "accent" to c.accent,
        "accentLight" to c.accentLight, "accentContainer" to c.accentContainerStrong,
        "textPrimary" to c.textPrimary, "textSecondary" to c.textSecondary,
        "warning" to c.warning, "error" to c.error,
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Colors")
        swatches.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (name, color) ->
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(ObShapes.Small)
                                .background(color)
                                .border(1.dp, c.borderStrong, ObShapes.Small)
                        )
                        Text(name, style = ObsidianTheme.typography.caption, color = c.textSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeSection() {
    val c = ObsidianTheme.colors
    val t = ObsidianTheme.typography
    val styles: List<Pair<String, TextStyle>> = listOf(
        "numericLarge" to t.numericLarge, "screenTitle" to t.screenTitle, "title" to t.title,
        "bodyLarge" to t.bodyLarge, "body" to t.body, "label" to t.label,
        "button" to t.button, "SECTION LABEL" to t.sectionLabel, "CAPTION" to t.caption,
    )
    var counter by remember { mutableIntStateOf(7) }
    ObCard {
        SectionLabel("Type")
        styles.forEach { (name, style) ->
            Text(name, style = style, color = c.textPrimary, modifier = Modifier.padding(top = 8.dp))
        }
        Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            RollingText("%02d:%02d".format(counter / 60 % 24, counter % 60), style = t.numericLarge, color = c.textPrimary)
            Box(Modifier.weight(1f))
            PillButton("Roll", onClick = { counter += 13 })
        }
    }
}

@Composable
private fun GlowSection() {
    val c = ObsidianTheme.colors
    val glows: List<Pair<String, GlowSpec>> = listOf(
        "Switch" to ObGlows.Switch, "Checkbox" to ObGlows.Checkbox, "Action" to ObGlows.Action,
        "Fab" to ObGlows.Fab, "Cta" to ObGlows.Cta, "Ambient" to ObGlows.Ambient,
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Glows")
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 24.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            glows.forEach { (name, spec) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        Modifier
                            .glow(c.accent, ObShapes.Card, spec)
                            .size(52.dp)
                            .clip(ObShapes.Card)
                            .background(c.accent)
                    )
                    Text(name, style = ObsidianTheme.typography.caption, color = c.textSecondary)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    Modifier
                        .breathingGlow(c.accent, CircleShape)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(c.accentContainer)
                        .border(1.dp, c.accentBorder, CircleShape)
                )
                Text("Breathing", style = ObsidianTheme.typography.caption, color = c.textSecondary)
            }
        }
    }
}

@Composable
private fun ControlsSection() {
    var switchOn by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(true) }
    var choice by remember { mutableIntStateOf(1) }
    var segment by remember { mutableIntStateOf(0) }
    val days = remember { mutableStateOf(setOf(0, 2, 4)) }
    SpacedCard(spacing = 16.dp) {
        SectionLabel("Controls")
        ObListRow(title = "Switch", subtitle = "Squish thumb, spring travel", icon = R.drawable.ob_ic_check) {
            ObSwitch(switchOn, { switchOn = it }, showDot = true)
        }
        ObDivider()
        ObListRow(title = "Checkbox", subtitle = "Pop on select", icon = R.drawable.ob_ic_check_bold) {
            ObCheckbox(checked, { checked = it })
        }
        ObDivider()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("5 min", "10 min", "15 min").forEachIndexed { i, label ->
                ChoicePill(label, selected = choice == i, onClick = { choice = i })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { i, d ->
                DayChip(d, selected = i in days.value, onClick = {
                    days.value = if (i in days.value) days.value - i else days.value + i
                })
            }
        }
        SegmentedPill(
            options = listOf(SegmentOption("Single"), SegmentOption("Series")),
            selectedIndex = segment,
            onSelect = { segment = it }
        )
    }
}

@Composable
private fun ButtonsSection() {
    var running by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Buttons")
        PrimaryButton(text = "Primary action", onClick = {})
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ActionPillButton(text = "Reset", onClick = { running = false })
            ActionPillButton(text = if (running) "Pause" else "Start", primary = true, onClick = { running = !running })
            Box(Modifier.weight(1f))
            ObFab(icon = R.drawable.ob_ic_check, contentDescription = "Add", onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillButton("Pill button", onClick = {})
            PillButton(
                "Accent pill",
                onClick = {},
                containerColor = ObsidianTheme.colors.accentContainerStrong,
                contentColor = ObsidianTheme.colors.accentLight
            )
        }
    }
}

@Composable
private fun FeedbackSection() {
    var progress by remember { mutableStateOf(0.35f) }
    var count by remember { mutableIntStateOf(3) }
    var query by remember { mutableStateOf("") }
    var sheetOpen by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Feedback")
        ObProgressBar(visible = true, progress = progress, label = "Importing photos")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PillButton("Advance", onClick = { progress = if (progress >= 1f) 0f else (progress + 0.2f).coerceAtMost(1f) })
            PillButton("Count", onClick = { count += 7 })
            CountBadge(count)
            Box(Modifier.weight(1f))
            CopyPill("482913", onCopy = {})
        }
        ObSearchField(query = query, onQueryChange = { query = it }, placeholder = "Search", modifier = Modifier.fillMaxWidth())
        PillButton("Open sheet", onClick = { sheetOpen = true })
    }
    if (sheetOpen) {
        ObSheet(onDismiss = { sheetOpen = false }, title = "Sheet") {
            SheetAction("Mark as done", R.drawable.ob_ic_check, onClick = { sheetOpen = false })
            SheetAction("Delete", R.drawable.ob_ic_close, onClick = { sheetOpen = false }, destructive = true)
        }
    }
}

@Composable
private fun ListSection() {
    val c = ObsidianTheme.colors
    var swipes by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Lists")
        DateDivider("Today")
        SwipeActionRow(
            left = SwipeVisual("Delete", R.drawable.ob_ic_close, c.error.copy(alpha = 0.35f)),
            right = SwipeVisual("Done", R.drawable.ob_ic_check, c.accentContainerStrong),
            onSwipeLeft = { swipes++ },
            onSwipeRight = { swipes++ },
        ) {
            Row(
                Modifier.fillMaxWidth().background(c.background).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ObAvatar(name = "Ada Lovelace")
                Column(Modifier.weight(1f)) {
                    Text("Swipe me", style = ObsidianTheme.typography.bodyLarge, color = c.textPrimary)
                    Text("Swiped $swipes times", style = ObsidianTheme.typography.body, color = c.textSecondary)
                }
                ObAvatarPair(
                    front = { m, size -> ObAvatar(name = "Grace Hopper", modifier = m, size = size) },
                    back = { m, size -> ObAvatar(name = null, modifier = m, size = size) },
                )
            }
        }
    }
}

@Composable
private fun InputsSection() {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf(0.6f) }
    var level by remember { mutableStateOf(2f) }
    var choice by remember { mutableIntStateOf(0) }
    var tab by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Inputs")
        ObTextField(
            value = name,
            onValueChange = { name = it },
            label = "Album name",
            placeholder = "Summer 2026",
            isError = name.length > 20,
            supportingText = if (name.length > 20) "Keep it under 20 characters" else null,
        )
        ObTextField(value = note, onValueChange = { note = it }, placeholder = "A longer note…", singleLine = false)
        ObSlider(value = volume, onValueChange = { volume = it })
        ObSlider(value = level, onValueChange = { level = it }, valueRange = 0f..4f, steps = 3)
        ObCard {
            listOf("Date taken", "Date added", "Name").forEachIndexed { i, label ->
                ObListRow(title = label, onClick = { choice = i }) {
                    ObRadioButton(selected = choice == i, onClick = null)
                }
            }
        }
        ObTabRow(tabs = listOf("Photos", "Albums", "Places"), selectedIndex = tab, onSelect = { tab = it })
    }
}

@Composable
private fun LoadingSection(snackbars: ObSnackbarState) {
    val scope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Loading and messages")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ObSpinner()
            ObSpinner(size = 40.dp, strokeWidth = 3.dp)
            Placeholder(Modifier.weight(1f).height(40.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillButton("Toast", onClick = { scope.launch { snackbars.show("Saved to Favourites") } })
            PillButton("Snackbar", onClick = { scope.launch { snackbars.show("Photo deleted", actionLabel = "Undo") } })
        }
    }
}

@Composable
private fun MediaSection(onOpenViewer: () -> Unit) {
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Media")
        listOf(0..2, 3..5).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                row.forEach { i ->
                    MediaTile(
                        onClick = {
                            if (selecting) selected = if (i in selected) selected - i else selected + i
                            else onOpenViewer()
                        },
                        onLongClick = { selecting = true; selected = selected + i },
                        selectionMode = selecting,
                        selected = i in selected,
                        label = if (i == 4) "0:42" else null,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (i == 5) Placeholder(Modifier.fillMaxSize(), RectangleShape) else DemoPhoto(i, Modifier.fillMaxSize())
                    }
                }
            }
        }
        if (selecting) PillButton("Done", onClick = { selecting = false; selected = emptySet() })
    }
}

/** A stand-in photo: a gradient in two accent-ish tones. */
@Composable
private fun DemoPhoto(seed: Int, modifier: Modifier = Modifier) {
    val hues = listOf(Color(0xFF3D9BFF), Color(0xFFFF535A), Color(0xFF21D07A), Color(0xFFFFB800), Color(0xFF9B6BFF))
    Box(modifier.background(Brush.linearGradient(listOf(hues[seed % hues.size], hues[(seed + 2) % hues.size].copy(alpha = 0.6f)))))
}

@Composable
private fun DemoViewer(onBack: () -> Unit) {
    var chrome by remember { mutableStateOf(true) }
    ViewerScaffold(
        chromeVisible = chrome,
        onBack = onBack,
        title = "IMG_2041",
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                PillButton("Share", onClick = {})
                PillButton("Edit", onClick = {})
                PillButton("Delete", onClick = {})
            }
        },
    ) {
        Zoomable(Modifier.fillMaxSize(), onTap = { chrome = !chrome }) {
            DemoPhoto(1, Modifier.fillMaxWidth().aspectRatio(0.75f))
        }
    }
}

@Composable
private fun KeypadSection() {
    var display by remember { mutableStateOf("0") }
    var pending by remember { mutableStateOf<String?>(null) }
    fun type(digit: String) { display = if (display == "0") digit else display + digit }
    val rows = listOf(
        listOf("AC" to KeypadKeyStyle.Function, "±" to KeypadKeyStyle.Function, "%" to KeypadKeyStyle.Function, "÷" to KeypadKeyStyle.Operator),
        listOf("7" to KeypadKeyStyle.Digit, "8" to KeypadKeyStyle.Digit, "9" to KeypadKeyStyle.Digit, "×" to KeypadKeyStyle.Operator),
        listOf("4" to KeypadKeyStyle.Digit, "5" to KeypadKeyStyle.Digit, "6" to KeypadKeyStyle.Digit, "−" to KeypadKeyStyle.Operator),
        listOf("1" to KeypadKeyStyle.Digit, "2" to KeypadKeyStyle.Digit, "3" to KeypadKeyStyle.Digit, "+" to KeypadKeyStyle.Operator),
        listOf("0" to KeypadKeyStyle.Digit, "." to KeypadKeyStyle.Digit, "⌫" to KeypadKeyStyle.Function, "=" to KeypadKeyStyle.Primary),
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Keypad")
        RollingText(display, style = ObsidianTheme.typography.numericLarge, color = ObsidianTheme.colors.textPrimary, modifier = Modifier.align(Alignment.End))
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, style) ->
                    KeypadKey(
                        label = label,
                        style = style,
                        selected = pending == label,
                        onClick = {
                            when (style) {
                                KeypadKeyStyle.Digit -> type(label)
                                KeypadKeyStyle.Operator -> pending = label
                                KeypadKeyStyle.Function -> if (label == "AC") { display = "0"; pending = null } else if (label == "⌫") display = display.dropLast(1).ifEmpty { "0" }
                                KeypadKeyStyle.Primary -> pending = null
                            }
                        },
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                    )
                }
            }
        }
    }
}

/** A screen in the transitions demo: its depth in a stack, and which tab it belongs to. */
private data class DemoScreen(val tab: Int, val depth: Int)

@Composable
private fun TransitionsSection() {
    val c = ObsidianTheme.colors
    var screen by remember { mutableStateOf(DemoScreen(tab = 0, depth = 0)) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Transitions")
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                when {
                    targetState.tab != initialState.tab -> ObTransitions.tab(forward = targetState.tab > initialState.tab)
                    targetState.depth > initialState.depth -> ObTransitions.push()
                    else -> ObTransitions.pop()
                }
            },
            label = "demoScreen",
        ) { shown ->
            ObCard(Modifier.fillMaxWidth()) {
                Text("Tab ${shown.tab + 1}", style = ObsidianTheme.typography.caption, color = c.textTertiary)
                Text(
                    if (shown.depth == 0) "Top level" else "Pushed screen ${shown.depth}",
                    style = ObsidianTheme.typography.title,
                    color = c.textPrimary,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillButton("Push", onClick = { screen = screen.copy(depth = screen.depth + 1) })
            PillButton("Back", onClick = { if (screen.depth > 0) screen = screen.copy(depth = screen.depth - 1) })
            PillButton("Next tab", onClick = { screen = DemoScreen(tab = (screen.tab + 1) % 3, depth = 0) })
        }
    }
}

/** [ObCard] whose children are spaced evenly (cards are plain Columns). */
@Composable
private fun SpacedCard(spacing: Dp, content: @Composable () -> Unit) {
    ObCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) { content() }
    }
}

@Preview(heightDp = 2200)
@Composable
private fun ObsidianShowcasePreview() {
    ObsidianTheme { ObsidianShowcase() }
}
