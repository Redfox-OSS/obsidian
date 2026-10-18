# Obsidian

A dark, glowing design system for Android, built with Jetpack Compose. Obsidian is the shared look of
[Messages](https://github.com/debz-g/messages), Alarmzy and the apps that follow: always-dark surfaces, one
vivid accent, soft colored glows, frosted glass, and spring-driven motion you can interrupt at any point.

- **Tokens:** 5 accents × 3 dark neutral ramps, the Inter type scale, shapes and spacing
- **Effects:** colored glows, frosted glass (via [Haze](https://github.com/chrisbanes/haze)), press feedback that replaces ripples
- **Motion:** shared springs, staggered entrances that never replay while scrolling, odometer digits, liquid selection indicators
- **Components:** buttons, switch, checkbox, pills, segmented control, cards, list rows, frosted headers and page layouts, menus, dialogs, sheets, swipe rows, avatars, badges, progress and spinners, snackbars, text fields, sliders, radio buttons, tabs, a collapsing header, keypad keys, copy pill, search field, accent picker, empty state, and a floating nav bar that can go compact on scroll
- **Media:** photo grid tiles with selection, pinch and double-tap zoom, and a full-screen viewer
- **Catalog app:** every token, component, motion and transition on a phone, in [`catalog/`](catalog)

The full reference (every value, motion spec and usage rule) is in **[docs/GUIDE.md](docs/GUIDE.md)**.

## Requirements

| | |
|---|---|
| Min SDK | 31 (the glass blur needs Android 12) |
| Compile SDK | 36.1 |
| Toolchain | AGP 9.2, Kotlin 2.2.10, Compose BOM 2026.02.01 |
| Brings in (`api`) | Compose UI, Material 3, Haze 1.7.2 |

## Install

Obsidian is published from this repository; no account or token is needed to use it.

**From the Maven repository (recommended).** Add the repository in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://debz-g.github.io/obsidian/maven") {
            content { includeGroup("dev.redfox") }
        }
    }
}
```

then the dependency, for example through the version catalog:

```toml
[versions]
obsidian = "2.0.0"

[libraries]
obsidian = { group = "dev.redfox", name = "obsidian", version.ref = "obsidian" }
```

```kotlin
// app/build.gradle.kts
dependencies { implementation(libs.obsidian) }
```

Compose, Material 3 and Haze come with it.

**As an AAR.** Each [release](https://github.com/debz-g/obsidian/releases) has `obsidian-<version>.aar`
attached. Put it in `app/libs/` and add `implementation(files("libs/obsidian-2.0.0.aar"))`. A bare AAR
doesn't bring its dependencies, so also add the Compose BOM (`2026.02.01`), `androidx.compose.ui:ui`,
`androidx.compose.ui:ui-graphics`, `androidx.compose.material3:material3` and
`dev.chrisbanes.haze:haze:1.7.2`.

**From source, while changing Obsidian and an app together.** Clone this repository next to the app and
add `includeBuild("../obsidian")` to the app's `settings.gradle.kts`. Gradle uses the local source in place
of the published `dev.redfox:obsidian`, so changes show up on the next build.

New to Obsidian? [docs/NEW_APP.md](docs/NEW_APP.md) sets up an app from scratch;
[docs/MIGRATING.md](docs/MIGRATING.md) moves an existing app over.

## Quick start

```kotlin
setContent {
    ObsidianTheme(accent = ObsidianAccent.Electric) {
        val hazeState = rememberHazeState()
        CompositionLocalProvider(LocalHazeState provides hazeState) {
            Box(Modifier.fillMaxSize().background(ObsidianTheme.colors.background)) {
                LazyColumn(Modifier.glassSource()) { /* … */ }
                FloatingNavBar(items, selectedIndex, onSelect, Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}
```

Read colors and type from `ObsidianTheme.colors` and `ObsidianTheme.typography`, not `MaterialTheme`. A matching
Material 3 theme is installed too, so stock dialogs, menus and text fields blend in.

## Catalog app

`catalog/` is a small app that shows everything in Obsidian: tokens you can switch live (accent and
neutrals), every component, the springs and entrances, real screen transitions with predictive back, a
photo grid that opens into a zoomable viewer with a shared-element transition, and a calculator keypad.

```bash
./gradlew :catalog:installDebug
```

CI builds the catalog APK on every push; download it from the run's artifacts.

## Building

```bash
./gradlew build
```

The build compiles the library, runs its tests and Android lint, and checks the public API against
`obsidian/api/obsidian.api`. CI runs the same on every push and pull request.

## Releasing

1. Set `version` in `obsidian/build.gradle.kts`, and turn the changelog's "unreleased" heading into
   that version.
2. Commit, then tag and push: `git tag v2.0.0 && git push origin v2.0.0`.

The release workflow checks the tag matches the version, builds and tests, adds the version to the
Maven repository on GitHub Pages (never overwriting a released version), and creates a GitHub Release
with the AAR, its sources and the catalog APK.

## Contributing

Issues and pull requests are welcome. Before you open a PR:

- Follow the rules in [docs/GUIDE.md](docs/GUIDE.md): tokens only, springs for anything interactive, glows only on primary elements.
- Add a new component to the catalog app (`catalog/`) so it can be checked by eye on a phone.
- Run `./gradlew build`. If you changed the public API on purpose, record it with
  `./gradlew :obsidian:apiDump` and commit `obsidian/api/obsidian.api`; the build fails on any
  API change that isn't recorded there.
- Note the change in [CHANGELOG.md](CHANGELOG.md).

## License

Obsidian is licensed under the [Apache License 2.0](LICENSE). It bundles the Inter typeface, which is under the
[SIL Open Font License 1.1](obsidian/src/main/assets/licenses/Inter-OFL.txt). See [NOTICE](NOTICE).
