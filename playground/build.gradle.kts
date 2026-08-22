plugins {
    id("w2sv.cmp")
    id("w2sv.kmp")
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "com.w2sv.composed.ui.layout.ExperimentalAnimatedSpacingApi"
        )
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":composed-animation"))
            implementation(project(":composed-core"))
            implementation(project(":composed-material3"))

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(libs.jetbrains.compose.material3.expressive)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.oikvpqya.compose.fastscroller.core)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }
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
