import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

// Include every module applying this convention plugin in the root Dokka site.
rootProject.dependencies.add("dokka", project)

kotlin {
    android {
        namespace = "com.w2sv.${path.removePrefix(":").replace(':', '.').replace('-', '.')}"
        compileSdk = 37
        minSdk = 21

        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources { enable = true }
        withHostTest {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    jvm()
    iosArm64()
    iosSimulatorArm64()
}

mavenPublishing {
    // Use module name as artifactId.
    coordinates(
        artifactId = project.name,
        version = rootProject.version.toString(),
    )
}
