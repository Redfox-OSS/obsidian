package dev.redfox.obsidian.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.components.EmptyState
import dev.redfox.obsidian.components.HeaderIconButton
import dev.redfox.obsidian.components.ObAlertDialog
import dev.redfox.obsidian.components.ObCard
import dev.redfox.obsidian.components.ObDivider
import dev.redfox.obsidian.components.ObDropdownMenu
import dev.redfox.obsidian.components.ObListRow
import dev.redfox.obsidian.components.ObMenuItem
import dev.redfox.obsidian.components.PillButton
import dev.redfox.obsidian.components.SectionLabel
import dev.redfox.obsidian.theme.ObsidianTheme

/** A group of components with its own page. */
internal enum class Category(val title: String, val summary: String) {
    Buttons("Buttons", "Primary, action pills, pills, FAB"),
    Controls("Controls", "Switch, checkbox, choice pills, day chips, segments"),
    Inputs("Inputs", "Text fields, sliders, radio buttons, tabs"),
    Lists("Lists", "Swipe rows, avatars, date dividers"),
    Feedback("Feedback", "Progress, badges, copy pill, search, sheets"),
    Loading("Loading and messages", "Spinners, shimmer, snackbars, toasts"),
    Overlays("Overlays", "Dialog, menu, empty state"),
    Keypad("Keypad", "Calculator and dial-pad keys"),
}

@Composable
internal fun ComponentsScreen() {
    val navigator = LocalNavigator.current
    TabScreen("Components") {
        ObCard {
            Category.entries.forEachIndexed { index, category ->
                if (index > 0) ObDivider()
                ObListRow(
                    title = category.title,
                    subtitle = category.summary,
                    titleStyleLarge = true,
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = { navigator.push(Destination.Components(category)) },
                ) {
                    Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = ObsidianTheme.colors.textTertiary)
                }
            }
        }
    }
}

@Composable
internal fun CategoryScreen(category: Category) {
    PushedScreen(category.title) {
        when (category) {
            Category.Buttons -> ButtonsSection()
            Category.Controls -> ControlsSection()
            Category.Inputs -> InputsSection()
            Category.Lists -> ListSection()
            Category.Feedback -> FeedbackSection()
            Category.Loading -> LoadingSection()
            Category.Overlays -> OverlaysSection()
            Category.Keypad -> KeypadSection()
        }
    }
}

@Composable
private fun OverlaysSection() {
    var dialog by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf("Newest first") }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionLabel("Dialog")
        PillButton("Delete 3 photos", onClick = { dialog = true })
        SectionLabel("Menu")
        Row {
            PillButton(sort, onClick = { menu = true })
            Box {
                HeaderIconButton(icon = R.drawable.ic_more_vert, contentDescription = "More", onClick = { menu = true })
                ObDropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    listOf("Newest first", "Oldest first", "Largest first").forEach { option ->
                        ObMenuItem(option, onClick = { sort = option; menu = false }, selected = option == sort)
                    }
                }
            }
        }
        SectionLabel("Empty state")
        Box(Modifier.fillMaxWidth().height(260.dp)) {
            EmptyState(icon = R.drawable.ic_star, title = "No favourites yet", subtitle = "Tap the star on a photo to keep it here")
        }
    }
    if (dialog) {
        ObAlertDialog(
            title = "Delete 3 photos?",
            text = "They'll be removed from this device.",
            confirmLabel = "Delete",
            onConfirm = { dialog = false },
            onDismiss = { dialog = false },
            destructive = true,
        )
    }
}
