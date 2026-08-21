import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlinx.kover")
    id("w2sv.kmp")
    id("w2sv.maven-publishing")
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
