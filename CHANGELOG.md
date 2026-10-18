# Changelog

Obsidian follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things.

## 2.0.0 (2026-10-18)

The first release from this repository.

### Changed
- **Licensed under Apache 2.0** (previously GPL-3.0, as part of Alarmzy).
- **The floating nav bar's selection halo is a pill** that fills the selected tab, in place of the
  48dp circle.
- **Numeric type styles are renamed** for use beyond clocks. The old names still work but are
  deprecated and will be removed in 3.0.0:

  | Old | New |
  |---|---|
  | `timeLarge` | `numericLarge` |
  | `timeTile` | `numericTile` |
  | `timeMedium` | `numericMedium` |
  | `timeSmall` | `numericSmall` |
  | `meridiem` | `unit` |

  `ObsidianTypography(...)` and `copy(...)` take the new names only.
- Every public declaration now states its visibility and type (Kotlin explicit API mode). No
  declarations changed visibility.

### Removed
- `ObsidianShowcase` moved out of the library into the catalog app (`catalog/`), which shows every
  token, component and transition on a phone. Apps that embedded it as a debug screen can drop it.

### Added
- Components from Messages, made app-independent: `TabPage` and `DetailPage` screen layouts,
  `ObSheet` and `SheetAction`, `SwipeActionRow`, `ObAvatar` and `ObAvatarPair`, `CountBadge`,
  `ObProgressBar`, `CopyPill`, `ObSearchField`, `AccentSwatchRow` and `DateDivider`.
- `FloatingNavBar` can go compact while content scrolls (`compact`, with `rememberCompactNavState`),
  and take an end `action` and a separate accent `pill` button beside it.
- Components for more kinds of app: `ObTextField`, `ObSlider`, `ObRadioButton`, `ObTabRow`,
  `ObSpinner`, `Modifier.shimmer` and `Placeholder`, `ObSnackbarState` and `ObSnackbarHost`
  (snackbars and toasts), `CollapsingHeader` with `rememberHeaderCollapse`, and `KeypadKey`.
- Media (`dev.redfox.obsidian.media`): `MediaTile` for photo grids with selection, `Zoomable`
  with `ZoomState` for pinch and double-tap zoom, and `ViewerScaffold` for full-screen viewers.
- Motion tokens: `ObDurations` (Exit, Short, Enter, Long) for timed fades, and `ObTransitions`
  screen transitions (`push`, `pop`, `predictivePop`, `tab`) plus a `sharedBounds` spring for
  shared elements. They plug into Navigation 3's `NavDisplay` or any `AnimatedContent`.
- - `Context.copyToClipboard(label, text, sensitive)`, which hides sensitive clips (codes) from the
  clipboard preview and history.
- The public API is recorded in `obsidian/api/obsidian.api`, and the build fails on any change to it
  that isn't recorded.

## 1.0.0

Extracted from Alarmzy as a standalone library: theme, colors and accents, the Inter type scale,
shapes and spacing, glows and glass, spring motion and entrances, about 20 components, the floating
nav bar and `ObsidianShowcase`.
