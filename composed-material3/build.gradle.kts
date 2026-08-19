plugins {
    id("w2sv.cmp-library")
    alias(libs.plugins.kover)
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

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nonAndroidMain = create("nonAndroidMain") {
            dependsOn(commonMain.get())
        }

        jvmMain { dependsOn(nonAndroidMain) }
        wasmJsMain { dependsOn(nonAndroidMain) }
        appleMain { dependsOn(nonAndroidMain) }

        val nonAndroidTest = create("nonAndroidTest") {
            dependsOn(commonTest.get())
        }

        jvmTest { dependsOn(nonAndroidTest) }
        wasmJsTest { dependsOn(nonAndroidTest) }
        appleTest { dependsOn(nonAndroidTest) }

        commonMain.dependencies {
            implementation(projects.composed.composedCore)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)
        }

        nonAndroidTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.jetbrains.compose.ui.test)
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
