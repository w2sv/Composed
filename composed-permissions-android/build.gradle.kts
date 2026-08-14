plugins {
    id("w2sv.android-library")
    id("w2sv.jetpack-compose-library")
}

dependencies {
    api(libs.google.accompanist.permissions)
    api(libs.androidx.compose.runtime)
    api(libs.jetbrains.kotlinx.coroutines.core)
}
