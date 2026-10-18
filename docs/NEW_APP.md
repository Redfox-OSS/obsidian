# Starting a new app on Obsidian

This guide sets up an Android app on Obsidian from an empty project: Gradle, the window, the theme, the app
shell (navigation, nav bar, snackbars), and the screen patterns. It ends with a check that keeps the app on
the design system as it grows.

For every token and component in detail, see [GUIDE.md](GUIDE.md). For the real thing on a phone, install
the catalog app (`./gradlew :catalog:installDebug` in this repository); its source in `catalog/` is a
complete, small Obsidian app, and the patterns below come from it.

## 1. Gradle

Obsidian apps build on the same toolchain as the library:

| | |
|---|---|
| AGP | 9.2.1 |
| Kotlin | 2.2.10 (with the Compose compiler plugin) |
| Compose BOM | 2026.02.01 (comes with Obsidian) |
| Compile SDK | 36.1 (`compileSdk { version = release(36) { minorApiLevel = 1 } }`) |
| Min SDK | 31 or higher |

**`settings.gradle.kts`:** add the Obsidian repository.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://debz-g.github.io/obsidian/maven") {
            content { includeGroup("dev.redfox") }
        }
    }
}
```

**`gradle/libs.versions.toml`:**

```toml
[versions]
obsidian = "2.0.0"
activityCompose = "1.13.0"
navigation3 = "1.0.0"

[libraries]
obsidian = { group = "dev.redfox", name = "obsidian", version.ref = "obsidian" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-navigation3-runtime = { group = "androidx.navigation3", name = "navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { group = "androidx.navigation3", name = "navigation3-ui", version.ref = "navigation3" }
```

**`app/build.gradle.kts`:**

```kotlin
dependencies {
    implementation(libs.obsidian)            // brings Compose UI, Material 3 and Haze
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}
```

In a multi-module app, add Obsidian once, as `api`, to your shared UI module (`:core:ui`), and keep the
app's own components there too, built only from Obsidian's tokens, effects and motion.

## 2. The window

Obsidian is always dark. Make the window dark from the first frame so launch never flashes white, and
draw edge to edge.

**`res/values/themes.xml`:**

```xml
<resources>
    <style name="Theme.MyApp" parent="android:Theme.Material.NoActionBar">
        <item name="android:windowBackground">@color/window_background</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>
</resources>
```

with `window_background` set to your neutral ramp's background (`#131316` for `ObsidianNeutrals.Dark`).
In the manifest, set `android:theme="@style/Theme.MyApp"` and `android:enableOnBackInvokedCallback="true"`
on `<application>` so predictive back works.

**`MainActivity`:**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent { MyApp(onExit = ::finish) }
    }
}
```

## 3. The theme

Pick an accent (`Crimson`, `Electric`, `Solar`, `Emerald`, `Luminous`, or your own) and a neutral ramp
(`Dark`, `Darker`, `Amoled`). Wrap the whole app once, and provide the two things the components look for:
a `HazeState` for frosted glass, and a snackbar state.

```kotlin
@Composable
fun MyApp(onExit: () -> Unit) {
    ObsidianTheme(accent = ObsidianAccent.Electric, neutrals = ObsidianNeutrals.Dark) {
        val hazeState = rememberHazeState()                  // dev.chrisbanes.haze
        val snackbars = remember { ObSnackbarState() }
        CompositionLocalProvider(LocalHazeState provides hazeState) {
            Box(Modifier.fillMaxSize().background(ObsidianTheme.colors.background)) {
                AppNavigation(onExit)                        // section 4
                ObSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter), bottomPadding = floatingNavClearance())
            }
        }
    }
}
```

Read colours and type from `ObsidianTheme.colors` and `ObsidianTheme.typography`, never `MaterialTheme`.
Obsidian also installs a matching Material 3 theme, so the few stock Material pieces you might still use
blend in.

If users can choose the accent, store its `name` and look it up in `ObsidianAccent.Presets`; offer the
choice with `AccentSwatchRow`. A brand colour of your own works too:
`ObsidianAccent("Brand", main = …, light = …, onMain = …, onLight = …)` (the guide explains how to pick the
four shades).

## 4. The app shell

### Navigation

Navigation 3 lets the app own its back stack, which is what Obsidian's entrances need: content staggers
in when a screen arrives, but not when it's uncovered by going back. Keep a navigator that remembers how
the last move happened.

```kotlin
sealed interface Move {
    data class TabSwitch(val forward: Boolean) : Move
    data object Push : Move
    data object Pop : Move
}

@Stable
class Navigator(start: Any) {
    val backStack = mutableStateListOf(start)
    var lastMove: Move by mutableStateOf(Move.Push)
        private set

    fun push(destination: Any) { lastMove = Move.Push; backStack.add(destination) }

    fun back(): Boolean {
        if (backStack.size <= 1) return false
        lastMove = Move.Pop
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    fun switchTab(root: Any, forward: Boolean) {
        lastMove = Move.TabSwitch(forward)
        backStack.clear()
        backStack.add(root)
    }
}
```

Give `NavDisplay` Obsidian's transitions, and wrap every destination in a `RevealScope`:

```kotlin
NavDisplay(
    backStack = navigator.backStack,
    onBack = { if (!navigator.back()) onExit() },
    entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
    transitionSpec = {
        when (val move = navigator.lastMove) {
            is Move.TabSwitch -> ObTransitions.tab(move.forward)
            else -> ObTransitions.push()
        }
    },
    popTransitionSpec = { ObTransitions.pop() },
    predictivePopTransitionSpec = { ObTransitions.predictivePop() },
    entryProvider = entryProvider {
        entry<HomeKey> {
            RevealScope(enabled = navigator.lastMove != Move.Pop) { HomeScreen() }
        }
        // …one entry per destination, each in a RevealScope
    },
)
```

For a photo that grows from a grid into a viewer, wrap `NavDisplay` in a `SharedTransitionLayout` and give
both ends `Modifier.sharedElement(…, boundsTransform = ObTransitions.sharedBounds)`, using
`LocalNavAnimatedContentScope.current` as the animated scope (see `catalog/…/Scaffolds.kt`).

### The nav bar

Tab roots show a `FloatingNavBar`; pushed screens hide it. It can shrink while content scrolls: attach the
compact state's connection to the container that holds the screens.

```kotlin
val compact = rememberCompactNavState(resetKey = navigator.backStack.lastOrNull())
Box(Modifier.fillMaxSize().nestedScroll(compact.connection)) {
    NavDisplay(/* … */)
    AnimatedVisibility(
        visible = navigator.backStack.size == 1,
        enter = fadeIn() + slideInVertically(ObMotion.gentle()) { it / 2 },
        exit = fadeOut() + slideOutVertically(ObMotion.gentle()) { it / 2 },
        modifier = Modifier.align(Alignment.BottomCenter),
    ) {
        FloatingNavBar(
            items = tabs.map { FloatingNavItem(it.icon, it.label, it.motion) },
            selectedIndex = currentTab,
            onSelect = { selectTab(it) },
            compact = compact.compact,
        )
    }
}
```

Give each tab icon a `NavIconMotion` that suits its glyph (`Pop`, `Shake` for a bell, `Turn` for a gear,
`Jelly` for stacks, `Press`, `Flip`). An end `action` (new message) or a separate accent `pill` (search)
are optional.

## 5. Screen patterns

| Screen | Use |
|---|---|
| A tab root | `CollapsingHeader` over the content, with `rememberHeaderCollapse(listState)`; or `TabPage` for a fixed header |
| A pushed screen | `DetailPage(title, onBack) { padding -> … }` |
| A list | `LazyColumn(Modifier.glassSource(), contentPadding = …)`, rows with `Modifier.revealOnEnter(index)` |
| A form or settings | `RevealColumn` of `ObCard`s with `ObListRow`s and `SectionLabel`s |
| A photo grid | `LazyVerticalGrid` of `MediaTile`s; open into `ViewerScaffold` + `Zoomable` |
| Nothing to show | `EmptyState(icon, title, subtitle)` |

A tab root with a collapsing header:

```kotlin
@Composable
fun AlbumsScreen(albums: List<Album>) {
    val list = rememberLazyListState()
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(Modifier.fillMaxSize().background(ObsidianTheme.colors.background)) {
        LazyColumn(
            state = list,
            modifier = Modifier.glassSource().fillMaxSize(),
            contentPadding = PaddingValues(
                start = ScreenGutter, end = ScreenGutter,
                top = top + CollapsingHeaderExpandedHeight,
                bottom = floatingNavClearance() + ObSpacing.L,
            ),
        ) {
            itemsIndexed(albums) { index, album ->
                ObListRow(
                    title = album.name,
                    subtitle = "${album.count} photos",
                    titleStyleLarge = true,
                    onClick = { /* push */ },
                    modifier = Modifier.revealOnEnter(index).padding(vertical = ObSpacing.M),
                )
            }
        }
        CollapsingHeader(title = "Albums", collapse = rememberHeaderCollapse(list))
    }
}
```

The essentials, which the components already follow:

- **Glass.** Scrolling content is `glassSource()`; headers and the nav bar are its siblings, drawn on top.
- **Insets.** Pad content by the status bar plus the header height at the top, and by
  `floatingNavClearance()` at the bottom on tab roots.
- **Gutters.** Every page's horizontal edge is `ScreenGutter` (20dp).
- **Clicks.** `Modifier.pressable { }` instead of `clickable`: no ripples. Press scales: rows 0.985, cards
  0.97, buttons 0.96, pills 0.93, header icons 0.9, chips and FAB 0.88.
- **Motion.** Anything interactive moves on an `ObMotion` spring; only fades are timed (`ObDurations`).
- **Glow.** Only on primary or "on" elements, one strong glow per region.
- **Strings.** Components take their labels as parameters (`"Copied"`, `"Clear search"`, `"Back"` have
  English defaults); pass translated strings from your resources.

## 6. Icons and haptics

Use Material Symbols **Rounded** at 24dp as vector drawables, tinted `textSecondary` at rest,
`textPrimary` when selected, and `accentLight` on accent containers. Obsidian's components already play
the right haptics; for your own controls follow the table in the guide (toggles `ToggleOn`/`ToggleOff`,
choices `SegmentTick`, primary actions `Confirm`, secondary actions `ContextClick`, keys `KeyboardTap`).

## 7. Keep it on the design system

Add this check to each module with screens. It fails the build when code reaches past Obsidian:

```kotlin
// app/build.gradle.kts (or a convention plugin)
val verifyDesignRules = tasks.register("verifyDesignRules") {
    group = "verification"
    val sources = fileTree("src/main") { include("**/*.kt") }
    inputs.files(sources)
    doLast {
        val rules = listOf(
            Regex("""\bMaterialTheme\.""") to "use ObsidianTheme.colors / typography",
            Regex("""\.(combinedC|c)lickable\s*[({]""") to "use Modifier.pressable { }",
            Regex("""\bripple\s*\(""") to "no ripples; use Modifier.pressable",
            Regex("""\bColor\s*\(\s*0x""") to "hard-coded colour; use a token",
        )
        val problems = sources.files.sortedBy { it.path }.flatMap { file ->
            file.readLines().flatMapIndexed { i, line ->
                val code = line.substringBefore("//")
                rules.filter { (pattern, _) -> pattern.containsMatchIn(code) }
                    .map { (_, why) -> "${file.path}:${i + 1}: $why" }
            }
        }
        if (problems.isNotEmpty()) throw GradleException("Obsidian design rules:\n" + problems.joinToString("\n"))
    }
}
tasks.named("check") { dependsOn(verifyDesignRules) }
```

## 8. Before a release

- [ ] Every screen looked at on a phone, in each accent you offer.
- [ ] Back gesture tried on every pushed screen: content follows the finger and doesn't replay its entrance.
- [ ] Lists scroll under frosted headers; the last item clears the nav bar.
- [ ] Every icon-only control has a content description; tap targets are at least 44dp.
- [ ] `./gradlew check` passes, including `verifyDesignRules`.
