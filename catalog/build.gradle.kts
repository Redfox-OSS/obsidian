plugins {
    alias(libs.plugins.obsidian.android.application)
    alias(libs.plugins.obsidian.android.compose)
}

// The Obsidian catalog: every token, component, motion and transition, on a phone.
android {
    namespace = "dev.redfox.obsidian.catalog"

    defaultConfig {
        applicationId = "dev.redfox.obsidian.catalog"
        versionCode = 1
        versionName = project(":obsidian").version.toString()
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            // Signed with the debug key so the release APK installs anywhere for a look.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":obsidian"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}
