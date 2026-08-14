plugins {
    id("w2sv.cmp-library")
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.composed.composedCore)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)
        }

        getByName("androidHostTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.jetbrains.compose.ui.test.junit4)
            implementation(libs.junit)
            implementation(libs.roboelectric)
        }
    }
}
