plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
//    alias(libs.plugins.kover)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose.compiler)
    `maven-publish`
}

kotlin {
    jvmToolchain(17)
    applyDefaultHierarchyTemplate()
    androidTarget {
        publishLibraryVariants("release")
//        publishAllLibraryVariants()
    }
    jvm()
    macosX64()
    macosArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    )
        .forEach { target ->
            target.binaries.framework {
                baseName = "composed"
            }
        }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.jetbrains.androidx.lifecycle)
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.core.ktx)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

android {
    namespace = "com.w2sv.composed"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependencies {
        lintChecks(libs.compose.lint.checks)
        testImplementation(libs.androidx.compose.ui.test.junit4.android)
        testImplementation(libs.roboelectric)
    }

    tasks.withType(Test::class.java) {
        android.sourceSets.getByName("main").res.srcDir("src/androidUnitTest/res")
    }
}

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            groupId = "com.w2sv.composed"
//            artifactId = "composed"
//            version = version.toString()
//            pom {
//                developers {
//                    developer {
//                        id.set("w2sv")
//                        name.set("Janek Zangenberg")
//                    }
//                }
//                description.set("Generic utils for development with Jetpack Compose.")
//                url.set("https://github.com/w2sv/Composed")
//                licenses {
//                    license {
//                        name.set("The Apache Software License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//            }
//
//            afterEvaluate {
//                from(components["release"])
//            }
//        }
//    }
//}
