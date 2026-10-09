plugins {
    id("w2sv.cmp")
    id("w2sv.kmp-library")
}

kotlin {
    android {
        minSdk = 23
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.navigation3.runtime)
            api(libs.androidx.savedstate)
            api(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.runtime.saveable)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
