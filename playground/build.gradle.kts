plugins {
    id("w2sv.cmp")
    id("w2sv.kmp")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":composed-animation"))
            implementation(project(":composed-core"))

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.w2sv.composed.playground.MainKt"
    }
}

tasks.register<JavaExec>("usage") {
    group = "Compose desktop"
    description = "Prints playground launch options and available samples"

    val runTask = tasks.named<JavaExec>("run")
    classpath = runTask.get().classpath
    mainClass.set(runTask.flatMap { it.mainClass })
    args("--help")
}
