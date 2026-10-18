plugins {
    alias(libs.plugins.obsidian.android.library)
    alias(libs.plugins.obsidian.android.compose)
    `maven-publish`
}

// Obsidian: the shared design system, as a standalone Compose library.
group = "dev.redfox"
version = "1.0.0"

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
