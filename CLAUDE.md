# CLAUDE.md

Guidance for Claude Code (and anyone else) working in this repository.

## What this is

Obsidian is a Jetpack Compose design system for Android: always-dark surfaces, one vivid accent, soft
coloured glows, frosted glass, and spring motion. It's the shared look of Messages, Alarmzy and the apps
that follow (gallery, calculator, …). It is open source (Apache 2.0) and published from this repository.

| Path | What |
|---|---|
| `obsidian/` | The library, `dev.redfox:obsidian`, package `dev.redfox.obsidian` |
| `obsidian/api/obsidian.api` | The recorded public API; the build fails if it drifts |
| `catalog/` | The catalog app: every token, component, motion and transition on a phone |
| `build-logic/` | Convention plugins (SDK levels, Compose) |
| `docs/GUIDE.md` | The full reference: every value, motion spec and usage rule |
| `docs/NEW_APP.md` | Setting up a new app on Obsidian |
| `docs/MIGRATING.md` | Moving an existing app onto Obsidian |
| `CHANGELOG.md` | What changed in each version |

## Commands

```bash
./gradlew build                          # library + catalog: compile, unit tests, lint, API check
./gradlew :obsidian:apiDump              # record an intended public API change in obsidian/api/
./gradlew :catalog:installDebug          # put the catalog on a connected phone
./gradlew :obsidian:publishToMavenLocal  # try a change in an app that uses mavenLocal()
```

## Toolchain (pinned on purpose)

AGP 9.2.1, Kotlin 2.2.10, Compose BOM 2026.02.01, Haze 1.7.2, compile SDK 36.1, min SDK 31 (the glass
blur needs Android 12). Apps that use Obsidian build on the same versions, so don't upgrade any of these
casually: Haze 2.x needs Compose 1.12 and Kotlin 2.4. Lint's "newer version available" warnings are
expected. Don't add a dependency to the library without discussing it; every app inherits it.

## Rules for library code

**API**
- Kotlin explicit API mode is on: every public declaration states `public` and its type.
- Every public declaration has KDoc that says what it is for, not only what it does.
- After an intended API change, run `./gradlew :obsidian:apiDump` and commit the `.api` file. The diff
  is the review of the change.
- Semantic versioning. Renaming or removing something public: keep the old name as a `@Deprecated`
  alias with `ReplaceWith` for one major version, and list it in the changelog.
- Library resources start with `ob_` (enforced by `resourcePrefix`).

**Design**
- Only tokens: `ObsidianTheme.colors`, `ObsidianTheme.typography`, `ObShapes`, `ObSpacing`, `ObGlows`,
  `ObMotion`, `ObDurations`. No hard-coded colours (pure black/white for photo viewers and on-accent
  icons excepted), and no `MaterialTheme.*` outside `theme/`.
- No `Modifier.clickable` or ripples: use `Modifier.pressable { }` with the press scales from the guide.
- Anything interactive moves on an `ObMotion` spring so it can be interrupted; only fades and
  choreographed entrances are timed (`ObDurations`).
- Glows only on primary or "on" elements, one strong glow per region, applied before `clip`, none when
  disabled.
- Glass: scrolling content is `glassSource()`, and a glass surface is a sibling of it, never inside it.
- Haptics follow the guide's table (toggle, segment tick, confirm, context click, keyboard tap).
- Accessibility: tap targets of at least 44dp, content descriptions on icon-only controls, semantics
  (role, selected, progress) on custom controls.

**Performance**
- Per-frame animation is read in layout or draw (`Modifier.layout`, `placeWithLayer`, `graphicsLayer {}`,
  `drawWithContent`), not in composition, so animating never recomposes.
- Pass fast-changing values as `State<T>` or lambdas (`() -> Float`), not plain values.
- Check gesture maths for edge cases (no pointers, unspecified centroid, zero size); keep the maths in a
  plain class so it can be unit tested (see `ZoomState`).

## Adding or changing a component

1. The composable, with KDoc, following the rules above.
2. Unit tests for any logic that isn't drawing (maths, parsing, state machines).
3. A demo in the catalog app (`catalog/`): on a component page, or the Motion or Media tab.
4. A row in the components table in `docs/GUIDE.md` (and the haptics or motion tables if relevant).
5. An entry in `CHANGELOG.md` under the unreleased version.
6. `./gradlew :obsidian:apiDump`, then `./gradlew build`.
7. Look at it on a phone: `./gradlew :catalog:installDebug`. Screenshots catch spacing and type
   problems that compile fine.

## Git and releases

- Commits are the maintainer's: no `Co-Authored-By` or "Generated with" lines in messages.
- Don't push, tag or release without asking.
- Releasing: set `version` in `obsidian/build.gradle.kts`, turn the changelog's "unreleased" heading
  into that version, commit, then push a `v<version>` tag. The release workflow publishes to the Maven
  repository on the `gh-pages` branch (https://redfox-oss.github.io/obsidian/maven) and attaches the AAR,
  sources and catalog APK to a GitHub Release. A released version is never overwritten: fix forward
  with a patch version.
- Never commit signing keys, `local.properties` or `.claude/settings.local.json`.
