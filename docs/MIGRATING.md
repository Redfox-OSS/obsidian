# Moving an existing app onto Obsidian

This guide takes an app with its own design, usually Material 3, onto Obsidian a step at a time, so the
app keeps working after every step. The last section covers apps already on Obsidian 1.x (Messages,
Alarmzy) moving to 2.x.

For setting up the parts that don't exist yet (navigation, nav bar, snackbars), see
[NEW_APP.md](NEW_APP.md). For every token and component, see [GUIDE.md](GUIDE.md).

## 0. Decide first

- **Dark only.** Obsidian has no light theme. Remove the light palette, `isSystemInDarkTheme()` switches
  and dynamic colour (`dynamicDarkColorScheme`); the app's identity becomes one accent on dark neutrals.
- **The accent.** Use the preset closest to your brand (`Crimson`, `Electric`, `Solar`, `Emerald`,
  `Luminous`), or build one from your brand colour: `ObsidianAccent("Brand", main, light, onMain, onLight)`.
- **Compose.** Obsidian is Compose-only. View-based screens move to Compose first, or host Obsidian
  inside them with `ComposeView` until they do.
- **Toolchain.** Obsidian needs min SDK 31, compile SDK 36.1, AGP 9.2 and Kotlin 2.2.10. Align these
  before anything else; it's the step most likely to need other dependency updates.

## 1. Add the dependency

Add the repository and `dev.redfox:obsidian` as in [NEW_APP.md §1](NEW_APP.md#1-gradle). If the app pins
its own Compose BOM, line it up with Obsidian's (`2026.02.01`) so there's one Compose version.

## 2. Swap the root theme

Replace the app's theme with a thin adapter onto `ObsidianTheme`, keeping its name, so call sites don't
change:

```kotlin
@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    // Was: MaterialTheme(colorScheme = …, typography = …, shapes = …, content)
    ObsidianTheme(accent = ObsidianAccent.Electric, neutrals = ObsidianNeutrals.Dark, content = content)
}
```

`ObsidianTheme` also installs a Material 3 colour scheme, type scale and shapes made from Obsidian's
tokens. So the moment this lands, screens you haven't touched yet already render on Obsidian's surfaces,
accent and Inter type. Ship that, then convert screens one by one.

If users pick a colour or theme today, map the saved value onto an accent (see `MessagesTheme` in
Messages for an example) and drop the light/dark choice. Set the window background and edge-to-edge as in
[NEW_APP.md §2](NEW_APP.md#2-the-window).

## 3. Convert the shell

Do these once, before the screens:

1. Provide a `HazeState` through `LocalHazeState` at the root, so headers and the nav bar can frost.
2. Navigation transitions: `ObTransitions.push()`, `pop()`, `predictivePop()` and `tab(forward)`.
   They're `ContentTransform`s, so they work with Navigation Compose's `enterTransition`/`exitTransition`
   pairs (`push().targetContentEnter`, `push().initialContentExit`, …) as well as Navigation 3.
3. Bottom navigation: `NavigationBar` → `FloatingNavBar`. Tab screens then pad their content by
   `floatingNavClearance()` instead of the old bar height.
4. Snackbars: one `ObSnackbarState` and `ObSnackbarHost` at the root, replacing `SnackbarHostState` and
   `Toast`.

## 4. Convert screens

Take one screen at a time: its header, then its content, then its interactions. The tables below are the
usual replacements.

### Colours (`MaterialTheme.colorScheme` → `ObsidianTheme.colors`)

| Material 3 | Obsidian |
|---|---|
| `primary` / `onPrimary` | `accent` / `onAccent` |
| `primaryContainer` / `onPrimaryContainer` | `accentContainer` (or `accentContainerStrong` when selected) / `accentLight` |
| `secondary` | `accentLight` |
| `background`, `surface` | `background` |
| `surfaceContainer`, `surfaceContainerHigh` | `card` |
| `surfaceContainerLow` | `cardInner` |
| `surfaceVariant`, `surfaceContainerHighest` | `tile` (`tileHigh` for the highest) |
| `onBackground`, `onSurface` | `textPrimary` |
| `onSurfaceVariant` | `textSecondary` (`textTertiary` for hints) |
| `outline` / `outlineVariant` | `borderStrong` / `border` (`divider` for list dividers) |
| `error` | `error` (`warning` for cautions) |
| `scrim` | `Color.Black` at 55% |

### Type (`MaterialTheme.typography` → `ObsidianTheme.typography`)

| Material 3 | Obsidian |
|---|---|
| `displayLarge`, `displayMedium` | `numericLarge` for numbers; `screenTitle` for words |
| `headlineSmall`, `titleLarge` | `title` |
| `titleMedium` | `headerTitle` (in a header) or `bodyLarge` |
| `bodyLarge` | `bodyLarge` |
| `bodyMedium` | `body` |
| `bodySmall`, `labelMedium` | `label` |
| `labelLarge` | `button` (main buttons) or `labelStrong` |
| `labelSmall` | `caption` (uppercase eyebrows) |

### Components

| Material 3 / common | Obsidian |
|---|---|
| `Button` (main action) | `PrimaryButton` (full width) or `ActionPillButton(primary = true)` |
| `FilledTonalButton`, `OutlinedButton`, `TextButton` | `PillButton` or `ActionPillButton` |
| `FloatingActionButton` | `ObFab` |
| `IconButton` | `HeaderIconButton` |
| `Switch` / `Checkbox` / `RadioButton` | `ObSwitch` / `ObCheckbox` / `ObRadioButton` |
| `Slider` | `ObSlider` |
| `FilterChip`, `AssistChip` | `ChoicePill` (`DayChip` for day toggles) |
| `SegmentedButton` | `SegmentedPill` |
| `TabRow` | `ObTabRow` |
| `TextField`, `OutlinedTextField` | `ObTextField` |
| `SearchBar` | `ObSearchField` |
| `TopAppBar` | `LargeHeader` (tab roots) or `DetailHeader` (pushed screens), or the `TabPage` / `DetailPage` layouts |
| `LargeTopAppBar` | `CollapsingHeader` + `rememberHeaderCollapse` |
| `NavigationBar` | `FloatingNavBar` |
| `Card` | `ObCard` |
| `ListItem` | `ObListRow` (+ `IconTile`, `SectionLabel`) |
| `HorizontalDivider` | `ObDivider` |
| `AlertDialog` | `ObAlertDialog` |
| `DropdownMenu` / `DropdownMenuItem` | `ObDropdownMenu` / `ObMenuItem` |
| `ModalBottomSheet` | `ObSheet` + `SheetAction` |
| `Snackbar`, `Toast` | `ObSnackbarState.show(message, actionLabel)` (no action = a toast) |
| `Badge` | `CountBadge` |
| `CircularProgressIndicator` | `ObSpinner` |
| `LinearProgressIndicator` | `ObProgressBar` |
| `SwipeToDismissBox` | `SwipeActionRow` |
| Placeholder / skeleton libraries | `Placeholder`, `Modifier.shimmer()` |
| Image grid cells | `MediaTile` (the image goes in its content slot) |
| A photo viewer | `ViewerScaffold` + `Zoomable` |

### Interaction and motion

| Before | After |
|---|---|
| `Modifier.clickable { }` | `Modifier.pressable { }` (pick the press scale for the element) |
| `Modifier.combinedClickable(onLongClick = …)` | `Modifier.pressable(onLongClick = …) { }` |
| Ripples, `indication = …` | none: the press scale is the feedback |
| `tween(…)` on movement, `animateXAsState` defaults | `ObMotion.bouncy()`, `snappy()` or `gentle()` |
| Fades with arbitrary durations | `ObDurations.Exit`, `Short`, `Enter`, `Long` |
| `AnimatedVisibility` with default specs | `ExpandEnter` / `CollapseExit`, or `Modifier.popIn()` |
| Lists or forms appearing at once | `Modifier.revealOnEnter(index)`, `RevealColumn`, inside a `RevealScope` |
| `Modifier.shadow` / elevation | floating surfaces: `glow(Color.Black, shape, ObGlows.Elevation)`; primary controls: their `ObGlows` preset; everything else: none |

## 5. Finish

1. When a module has no `MaterialTheme.` left, add the design-rules check from
   [NEW_APP.md §7](NEW_APP.md#7-keep-it-on-the-design-system) to it, so it stays that way. Turn it on
   module by module.
2. Delete the old theme: colour files, `Color.kt`/`Type.kt`/`Shape.kt`, light-theme resources, dynamic
   colour code.
3. Replace remaining icons with Material Symbols Rounded.
4. Go through the release checklist in [NEW_APP.md §8](NEW_APP.md#8-before-a-release).

## Pitfalls

- **Glass needs a sibling.** A frosted header must sit next to the `glassSource()` content in a `Box`,
  not inside it, or nothing blurs.
- **Glow before clip.** `Modifier.glow(…)` has to come before `.clip(…)`; the glow draws outside the
  bounds.
- **Entrances replaying.** If lists re-animate when going back, the screen's `RevealScope` isn't
  disabled on pop (see the navigator in NEW_APP.md).
- **Content under the nav bar.** Tab screens need `floatingNavClearance()` as bottom padding, not a fixed
  number; it includes the system navigation bar.

## Upgrading from Obsidian 1.x

Messages and Alarmzy use Obsidian 1.0.0 (from `mavenLocal()` or the `:obsidian` source module in
Alarmzy). 2.0.0 changes:

| 1.x | 2.0.0 |
|---|---|
| `mavenLocal()`, or `project(":obsidian")` in Alarmzy | The Maven repository above, `dev.redfox:obsidian:2.0.0`; delete Alarmzy's `obsidian/` module |
| `typography.timeLarge`, `timeTile`, `timeMedium`, `timeSmall`, `meridiem` | `numericLarge`, `numericTile`, `numericMedium`, `numericSmall`, `unit` (old names work, deprecated) |
| `ObsidianTheme.typography.copy(timeLarge = …)` | `copy(numericLarge = …)`: the constructor and `copy` take the new names only |
| `ObsidianShowcase()` | Removed; the catalog app replaces it. Delete in-app showcase screens |
| `FloatingNavBar`'s round halo | A pill that fills the tab, automatically |

What Messages built on top of 1.x is now in the library; swap each for the library's and delete the copy
in `:core:ui`:

| Messages `:core:ui` | Obsidian 2.0.0 |
|---|---|
| `CompactFloatingNavBar(items, selectedIndex, onSelect, compact, action, onAction, pill, onPill)` | `FloatingNavBar(…)` with the same parameters; pass `compact =` by name, since `modifier` now comes before it |
| `rememberCompactNavState(key)` returning a `Pair` | `rememberCompactNavState(key)` returning `CompactNavState` with `.compact` and `.connection` |
| `ObSheet`, `SheetAction`, `TabPage`, `DetailPage`, `SwipeActionRow`, `SwipeVisual`, `DateDivider` | Same names and parameters, in `dev.redfox.obsidian.components` |
| `UnreadBadge(count)` | `CountBadge(count)` |
| `SyncProgressBar(visible, progress, label = "Syncing messages")` | `ObProgressBar(visible, progress, label)`: the label is required |
| `AccentSwatchRow(selected: AppAccent, onSelect)` | `AccentSwatchRow(selected: ObsidianAccent, onSelect)`: map with `toObsidian()` / `toAppAccent()` |
| `Avatar(participants)` | `ObAvatar(name, image = { AsyncImage(…) })`, and `ObAvatarPair` for groups; keep the contact logic in the app |
| `OtpCopyButton(code, onCopy)` | `CopyPill(value = code, onCopy)` |
| `SearchOverlay`'s private `SearchField` | `ObSearchField` for a plain search field; the morphing pill stays in the app |
| `Context.copyToClipboard` (`core.ui.platform`) | `Context.copyToClipboard` in `dev.redfox.obsidian.platform` |

Then remove Messages' debug design-showcase screen, which imported `ObsidianShowcase`.
