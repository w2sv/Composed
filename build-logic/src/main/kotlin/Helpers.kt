import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.pathBasedPackageName: String
    get() = "com.w2sv.${path.removePrefix(":").replace(':', '.').replace('-', '.')}"

@Suppress("NewApi")
internal fun VersionCatalog.version(name: String): String =
    findVersion(name).get().requiredVersion

/**
 * Creates shared source sets for code used by every configured non-Android target.
 *
 * Applies the default Kotlin hierarchy template, then places the JVM, Wasm JS, and Apple main
 * and test source sets beneath `nonAndroidMain` and `nonAndroidTest`, respectively.
 */
fun KotlinMultiplatformExtension.configureNonAndroidSourceSets() {
    applyDefaultHierarchyTemplate()

    sourceSets.apply {
        val nonAndroidMain = create("nonAndroidMain") {
            dependsOn(getByName("commonMain"))
        }

        getByName("jvmMain").dependsOn(nonAndroidMain)
        getByName("wasmJsMain").dependsOn(nonAndroidMain)
        getByName("appleMain").dependsOn(nonAndroidMain)

        val nonAndroidTest = create("nonAndroidTest") {
            dependsOn(getByName("commonTest"))
        }

        getByName("jvmTest").dependsOn(nonAndroidTest)
        getByName("wasmJsTest").dependsOn(nonAndroidTest)
        getByName("appleTest").dependsOn(nonAndroidTest)
    }
}
