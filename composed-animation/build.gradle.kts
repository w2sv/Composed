plugins {
    id("w2sv.cmp-library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.animation)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            api(libs.androidx.annotation)
        }

        androidHostTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.androidx.activity.compose)
            implementation(libs.jetbrains.compose.ui.test.junit4)
            implementation(libs.jetbrains.kotlinx.coroutines.test)
            implementation(libs.junit)
            implementation(libs.roboelectric)
        }
    }
}
