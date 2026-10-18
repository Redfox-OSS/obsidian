# Obsidian

A dark, glowing design system for Android, built with Jetpack Compose. Obsidian is the shared look of
[Messages](https://github.com/debz-g/messages), Alarmzy and the apps that follow: always-dark surfaces, one
vivid accent, soft colored glows, frosted glass, and spring-driven motion you can interrupt at any point.

- **Tokens:** 5 accents × 3 dark neutral ramps, the Inter type scale, shapes and spacing
- **Effects:** colored glows, frosted glass (via [Haze](https://github.com/chrisbanes/haze)), press feedback that replaces ripples
- **Motion:** shared springs, staggered entrances that never replay while scrolling, odometer digits, liquid selection indicators
- **Components:** buttons, switch, checkbox, pills, segmented control, cards, list rows, frosted headers and page layouts, menus, dialogs, sheets, swipe rows, avatars, badges, progress and spinners, snackbars, text fields, sliders, radio buttons, tabs, a collapsing header, keypad keys, copy pill, search field, accent picker, empty state, and a floating nav bar that can go compact on scroll
- **Media:** photo grid tiles with selection, pinch and double-tap zoom, and a full-screen viewer
- **Style guide:** `ObsidianShowcase()` shows every token and component on one screen

The full reference (every value, motion spec and usage rule) is in **[docs/GUIDE.md](docs/GUIDE.md)**.

## Requirements

| | |
|---|---|
| Min SDK | 31 (the glass blur needs Android 12) |
| Compile SDK | 36.1 |
| Toolchain | AGP 9.2, Kotlin 2.2.10, Compose BOM 2026.02.01 |
| Brings in (`api`) | Compose UI, Material 3, Haze 1.7.2 |

## Install

Obsidian isn't on Maven Central yet. Until it is, build it from source in one of two ways.

**Composite build (recommended).** Clone this repo next to your app, then in the app's `settings.gradle.kts`:

```kotlin
includeBuild("../obsidian")
```

```kotlin
// app/build.gradle.kts
dependencies { implementation("dev.redfox:obsidian:2.0.0") }
```

Gradle swaps the dependency for the local source, so changes to Obsidian show up in the app on the next build.

**Local Maven.** Publish once, then depend on it from any app on the same machine:

```bash
./gradlew :obsidian:publishToMavenLocal
```

```kotlin
// settings.gradle.kts → dependencyResolutionManagement.repositories
mavenLocal { content { includeGroup("dev.redfox") } }
```

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

## Building

```bash
./gradlew build
```

The build compiles the library, runs its tests and Android lint, and checks the public API against
`obsidian/api/obsidian.api`. CI runs the same on every push and pull request.

## Contributing

Issues and pull requests are welcome. Before you open a PR:

- Follow the rules in [docs/GUIDE.md](docs/GUIDE.md): tokens only, springs for anything interactive, glows only on primary elements.
- Add a new component to `ObsidianShowcase` so it can be checked by eye.
- Run `./gradlew build`. If you changed the public API on purpose, record it with
  `./gradlew :obsidian:apiDump` and commit `obsidian/api/obsidian.api`; the build fails on any
  API change that isn't recorded there.
- Note the change in [CHANGELOG.md](CHANGELOG.md).

## License

Obsidian is licensed under the [Apache License 2.0](LICENSE). It bundles the Inter typeface, which is under the
[SIL Open Font License 1.1](obsidian/src/main/assets/licenses/Inter-OFL.txt). See [NOTICE](NOTICE).
