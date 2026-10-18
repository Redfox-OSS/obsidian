package dev.redfox.obsidian.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.redfox.obsidian.components.ChoicePill
import dev.redfox.obsidian.components.ObCard
import dev.redfox.obsidian.components.SectionLabel
import dev.redfox.obsidian.theme.ObShapes
import dev.redfox.obsidian.theme.ObSpacing
import dev.redfox.obsidian.theme.ObsidianNeutrals
import dev.redfox.obsidian.theme.ObsidianTheme

@Composable
internal fun TokensScreen() {
    val choice = LocalThemeChoice.current
    TabScreen("Tokens") {
        AccentSection(selected = choice.accent, onSelect = choice.onAccent)
        NeutralsSection(selected = choice.neutrals, onSelect = choice.onNeutrals)
        ColorSection()
        TypeSection()
        ShapesSection()
        GlowSection()
    }
}

@Composable
private fun NeutralsSection(selected: ObsidianNeutrals, onSelect: (ObsidianNeutrals) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Neutrals")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ObsidianNeutrals.Presets.forEach { ramp ->
                ChoicePill(ramp.name, selected = ramp == selected, onClick = { onSelect(ramp) })
            }
        }
    }
}

@Composable
private fun ShapesSection() {
    val c = ObsidianTheme.colors
    val shapes: List<Pair<String, Shape>> = listOf(
        "Card 16" to ObShapes.Card, "CardLarge 24" to ObShapes.CardLarge, "Small 12" to ObShapes.Small,
        "Fab 18" to ObShapes.Fab, "Checkbox 8" to ObShapes.Checkbox, "Pill" to ObShapes.Pill,
    )
    val spacing: List<Pair<String, Dp>> = listOf(
        "XXS" to ObSpacing.XXS, "XS" to ObSpacing.XS, "S" to ObSpacing.S, "M" to ObSpacing.M,
        "L" to ObSpacing.L, "Gutter" to ObSpacing.Gutter, "XL" to ObSpacing.XL, "XXL" to ObSpacing.XXL,
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Shapes")
        shapes.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (name, shape) ->
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            Modifier
                                .height(56.dp)
                                .width(88.dp)
                                .clip(shape)
                                .background(c.tile)
                                .border(1.dp, c.borderStrong, shape)
                        )
                        Text(name, style = ObsidianTheme.typography.caption, color = c.textSecondary)
                    }
                }
            }
        }
        SectionLabel("Spacing")
        ObCard {
            spacing.forEach { (name, value) ->
                Row(Modifier.height(28.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(name, style = ObsidianTheme.typography.label, color = c.textSecondary, modifier = Modifier.width(64.dp))
                    Box(Modifier.size(width = value * 4, height = 8.dp).clip(ObShapes.Pill).background(c.accentContainerStrong))
                    Text("  ${value.value.toInt()}dp", style = ObsidianTheme.typography.caption, color = c.textTertiary)
                }
            }
        }
    }
}
