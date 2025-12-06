plugins {
    id("w2sv.android-library")
    id("w2sv.compose-conventions")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.w2sv.composed"
            artifactId = "permissions"
            version = version.toString()
            pom {
                developers {
                    developer {
                        id.set("w2sv")
                        name.set("Janek Zangenberg")
                    }
                }
                description.set("Permission utils for development with Jetpack Compose.")
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
    api(libs.google.accompanist.permissions)
    implementation(libs.compose.ui.tooling)
}
