# Changelog

Obsidian follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things.

## 2.0.0 (unreleased)

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

### Added
- The public API is recorded in `obsidian/api/obsidian.api`, and the build fails on any change to it
  that isn't recorded.

## 1.0.0

Extracted from Alarmzy as a standalone library: theme, colors and accents, the Inter type scale,
shapes and spacing, glows and glass, spring motion and entrances, about 20 components, the floating
nav bar and `ObsidianShowcase`.
