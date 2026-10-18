package dev.redfox.obsidian.catalog

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.components.ChoicePill
import dev.redfox.obsidian.components.ObCard
import dev.redfox.obsidian.components.ObCheckbox
import dev.redfox.obsidian.components.ObListRow
import dev.redfox.obsidian.components.PillButton
import dev.redfox.obsidian.components.PrimaryButton
import dev.redfox.obsidian.components.SectionLabel
import dev.redfox.obsidian.motion.HorizontalReveal
import dev.redfox.obsidian.motion.ObMotion
import dev.redfox.obsidian.motion.RevealScope
import dev.redfox.obsidian.motion.RollingText
import dev.redfox.obsidian.motion.revealOnEnter
import dev.redfox.obsidian.theme.ObsidianTheme

@Composable
internal fun MotionScreen() {
    val navigator = LocalNavigator.current
    TabScreen("Motion") {
        SpringsSection()
        EntrancesSection()
        StateChangesSection()
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionLabel("Navigation")
            Text(
                "A real pushed screen, with the back gesture scrubbing it. Switching tabs drifts from the tab's side; scrolling this page shrinks the nav bar.",
                style = ObsidianTheme.typography.body,
                color = ObsidianTheme.colors.textSecondary,
            )
            PrimaryButton(text = "Push a screen", onClick = { navigator.push(Destination.PushedDemo(depth = 1)) })
        }
        TransitionsSection()
    }
}

@Composable
private fun SpringsSection() {
    var right by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Springs")
        ObCard {
            SpringRow("bouncy · 0.55 / 380", right, ObMotion.bouncy())
            SpringRow("snappy · 0.82 / 700", right, ObMotion.snappy())
            SpringRow("gentle · 0.90 / 240", right, ObMotion.gentle())
        }
        PillButton(if (right) "Send back" else "Send across", onClick = { right = !right })
    }
}

@Composable
private fun SpringRow(label: String, right: Boolean, spec: AnimationSpec<Float>) {
    val c = ObsidianTheme.colors
    val progress by animateFloatAsState(if (right) 1f else 0f, spec, label = label)
    Column(Modifier.fillMaxWidth().height(56.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = ObsidianTheme.typography.caption, color = c.textTertiary)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val travel = maxWidth - 24.dp
            Box(
                Modifier
                    .graphicsLayer { translationX = travel.toPx() * progress }
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(c.accent)
            )
        }
    }
}

@Composable
private fun EntrancesSection() {
    var replays by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Entrances")
        key(replays) {
            RevealScope {
                ObCard {
                    listOf("Rise", "Fade", "Scale", "Stagger", "Settle").forEachIndexed { i, label ->
                        ObListRow(title = label, subtitle = "Item ${i + 1}", titleStyleLarge = true, modifier = Modifier.revealOnEnter(i).padding(vertical = 10.dp))
                    }
                }
            }
        }
        PillButton("Replay", onClick = { replays++ })
    }
}

@Composable
private fun StateChangesSection() {
    var count by remember { mutableIntStateOf(128) }
    var picked by remember { mutableIntStateOf(0) }
    var selecting by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("State changes")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RollingText(count.toString(), style = ObsidianTheme.typography.numericLarge, color = ObsidianTheme.colors.textPrimary)
            PillButton("+17", onClick = { count += 17 })
            PillButton("−9", onClick = { count -= 9 })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Day", "Month", "Year").forEachIndexed { i, label -> ChoicePill(label, selected = picked == i, onClick = { picked = i }) }
        }
        ObCard {
            listOf("Beach", "Mountains").forEach { album ->
                ObListRow(title = album, titleStyleLarge = true, modifier = Modifier.padding(vertical = 12.dp), onClick = { selecting = !selecting }) {
                    HorizontalReveal(visible = selecting) { ObCheckbox(checked = false, onCheckedChange = null, modifier = Modifier.offset(x = 0.dp)) }
                }
            }
        }
    }
}

@Composable
internal fun PushedDemoScreen(depth: Int) {
    val navigator = LocalNavigator.current
    PushedScreen("Pushed screen $depth") {
        Text(
            "This screen drifted in from the end while the one below faded. Swipe back from the edge and the transition follows your finger; let go early and it settles back.",
            style = ObsidianTheme.typography.body,
            color = ObsidianTheme.colors.textSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillButton("Push another", onClick = { navigator.push(Destination.PushedDemo(depth + 1)) })
            PillButton("Back", onClick = { navigator.back() })
        }
    }
}
