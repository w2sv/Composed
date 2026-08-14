plugins {
    id("w2sv.android-library")
    id("w2sv.compose-conventions")
    alias(libs.plugins.kover)
}

dependencies {
    implementation(projects.composed.composedCore)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.annotation)

    testImplementation(libs.junit)
    testImplementation(libs.roboelectric)
    testImplementation(libs.androidx.ui.test.junit4.android)
}
