plugins {
    id("w2sv.android-library")
    id("w2sv.compose-conventions")
}

dependencies {
    api(libs.google.accompanist.permissions)
    implementation(libs.compose.ui.tooling)
}
