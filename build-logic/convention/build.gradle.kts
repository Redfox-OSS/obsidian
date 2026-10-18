plugins {
    `kotlin-dsl`
}

group = "dev.redfox.obsidian.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "obsidian.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "obsidian.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = "obsidian.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
    }
}
