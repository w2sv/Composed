plugins {
    id("w2sv.android-library")
    id("w2sv.compose-conventions")
    alias(libs.plugins.kover)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.w2sv.composed"
            artifactId = "material3"
            version = version.toString()
            pom {
                developers {
                    developer {
                        id.set("w2sv")
                        name.set("Janek Zangenberg")
                    }
                }
                description.set("Material3 utils for development with Jetpack Compose.")
                url.set("https://github.com/w2sv/Composed")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }
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
