import gradle.kotlin.dsl.plugins._2035c710eaf7e06d9cfab030694b8afb.compose
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

android {
    buildFeatures {
        compose = true
    }
    // androidx.compose.ui:ui-test-junit4-android:1.10.0 needs minSdk=23
    sourceSets {
        getByName("test") {
            defaultConfig {
                minSdk = 23
            }
        }
    }
}

dependencies {
    lintChecks(libs.compose.lint.checks)
}
