plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    version.set(libs.version("ktlint"))
}

kotlin {
    compilerOptions.freeCompilerArgs.addAll("-Xcontext-parameters", "-Xannotation-default-target=param-property")
}
