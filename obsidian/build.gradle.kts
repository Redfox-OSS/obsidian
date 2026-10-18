plugins {
    alias(libs.plugins.obsidian.android.library)
    alias(libs.plugins.obsidian.android.compose)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
}

// Obsidian: the shared design system, as a standalone Compose library.
group = "dev.redfox"
version = "2.0.0"

android {
    namespace = "dev.redfox.obsidian"
    // Library resources (font, icons) are prefixed so they never clash with a host app's.
    resourcePrefix = "ob_"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    // Every public declaration states its visibility and type, so nothing goes public by accident.
    explicitApi()
}

dependencies {
    // Exposed as `api`: host apps write Compose/M3 code against the same versions, and
    // LocalHazeState / rememberHazeState come from Haze.
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.material3)
    api(libs.haze)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "obsidian"
            afterEvaluate { from(components["release"]) }
        }
    }
}

// Binary compatibility: the public API is recorded in api/obsidian.api, and `check` fails when it
// changes without `./gradlew :obsidian:apiDump`, so every API change is deliberate and reviewed.
// The validator plugin doesn't recognise AGP 9's built-in Kotlin, so its tasks are wired up here
// from the release compilation.
val apiBuild = tasks.register<kotlinx.validation.KotlinApiBuildTask>("apiBuild") {
    inputClassesDirs.from(
        tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileReleaseKotlin")
            .flatMap { it.destinationDirectory }
    )
    runtimeClasspath.from(configurations.named("bcv-rt-jvm-cp-resolver"))
    outputApiFile.set(layout.buildDirectory.file("api/obsidian.api"))
    // The Compose compiler's ComposableSingletons hold lambdas whose names are hashes that
    // change with any edit; they aren't API, so they're left out of the record.
    doLast {
        val file = outputApiFile.get().asFile
        file.writeText(
            file.readText().replace(Regex("""public final class \S+/ComposableSingletons\$\S+ \{\n.*?\n}\n\n?""", RegexOption.DOT_MATCHES_ALL), "")
        )
    }
}
val apiDump = tasks.register<Sync>("apiDump") {
    group = "verification"
    description = "Records the public API in api/obsidian.api."
    from(apiBuild.flatMap { it.outputApiFile })
    into(layout.projectDirectory.dir("api"))
}
val apiCheck = tasks.register<kotlinx.validation.KotlinApiCompareTask>("apiCheck") {
    group = "verification"
    description = "Fails if the public API differs from api/obsidian.api."
    projectApiFile.set(layout.projectDirectory.file("api/obsidian.api"))
    generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
    mustRunAfter(apiDump)
}
tasks.named("check") { dependsOn(apiCheck) }
dependencies {
    // Reads Kotlin metadata from the compiled classes (the plugin adds it only for Kotlin plugins it knows).
    "bcv-rt-jvm-cp"(libs.kotlin.metadata.jvm)
}
