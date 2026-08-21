plugins {
    id("w2sv.cmp")
    id("w2sv.kmp-library")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    targets.configureEach {
        compilations
            .matching { it.name.endsWith("Test", ignoreCase = true) }
            .configureEach {
                compileTaskProvider.configure {
                    compilerOptions {
                        optIn.addAll(
                            "androidx.compose.ui.test.ExperimentalTestApi",
                            "kotlinx.coroutines.ExperimentalCoroutinesApi"
                        )
                    }
                }
            }
    }

    configureNonAndroidSourceSets()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.composed.composedCore)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)
        }

        named("nonAndroidTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.jetbrains.compose.ui.test)
            }
        }

        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
        }

        androidHostTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.jetbrains.compose.ui.test.junit4)
            implementation(libs.junit)
            implementation(libs.roboelectric)
        }
    }
}
