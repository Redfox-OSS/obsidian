import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/** SDK levels for every module. Apps using Obsidian must compile against at least [COMPILE].[COMPILE_MINOR]. */
internal object Sdk {
    const val COMPILE = 36
    const val COMPILE_MINOR = 1
    const val TARGET = 36
    /** The frosted glass blur (RenderEffect) needs Android 12. */
    const val MIN = 31
}

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.lib(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { IllegalArgumentException("No library '$alias' in libs.versions.toml") }
