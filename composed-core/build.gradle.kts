plugins {
    id("w2sv.cmp-library")
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.androidx.lifecycle.runtime.compose)
            api(libs.jetbrains.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.jetbrains.compose.foundation)
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            api(libs.jetbrains.compose.animation)
            api(libs.androidx.annotation)
            implementation(libs.androidx.core.ktx)
        }

        getByName("androidHostTest").dependencies {
            implementation(libs.jetbrains.compose.material3)
            implementation(kotlin("test"))
            implementation(libs.androidx.activity.compose)
            implementation(libs.jetbrains.compose.ui.test.junit4)
            implementation(libs.jetbrains.kotlinx.coroutines.test)
            implementation(libs.junit)
            implementation(libs.roboelectric)
        }
    }
}
