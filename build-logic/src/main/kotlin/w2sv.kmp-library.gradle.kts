import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

// Include every module applying this convention plugin in the root Dokka site.
rootProject.dependencies.add("dokka", project)

ktlint {
    version.set(libs.version("ktlint"))
}

kotlin {
    android {
        namespace = pathBasedPackageName
        compileSdk = 37
        minSdk = 21

        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources { enable = true }
        withHostTest {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(
                        rootProject.projectDir.resolve("karma.config.d")
                    )
                }
            }
        }
    }

    iosArm64()
    iosSimulatorArm64()
}

mavenPublishing {
    // Use module name as artifactId.
    coordinates(
        artifactId = project.name,
        version = rootProject.version.toString()
    )
}
