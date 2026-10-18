import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Turns on Compose for an Android library or app module, with previews in debug. */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        extensions.findByType(LibraryExtension::class.java)?.buildFeatures?.compose = true
        extensions.findByType(ApplicationExtension::class.java)?.buildFeatures?.compose = true
        dependencies {
            add("implementation", platform(libs.lib("androidx-compose-bom")))
            add("implementation", libs.lib("androidx-compose-ui-tooling-preview"))
            add("debugImplementation", libs.lib("androidx-compose-ui-tooling"))
        }
    }
}
