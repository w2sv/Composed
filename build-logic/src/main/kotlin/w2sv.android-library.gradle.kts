plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
}

kotlin { jvmToolchain(11) }

android {
    namespace = "com.w2sv.${path.removePrefix(":").replace(':', '.').replace('-', '.')}"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes { getByName("release") { isMinifyEnabled = false } }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { buildConfig = false }

    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all { test -> test.failOnNoDiscoveredTests = false }
        }
    }

    publishing { singleVariant("release") { withSourcesJar() } }
}
