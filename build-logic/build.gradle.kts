plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
}

dependencies {
    // make version catalog available in plugins
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

dependencies {
    implementation(plugin(libs.plugins.android.kmp.library))
    implementation(plugin(libs.plugins.jetbrains.compose))
    implementation(plugin(libs.plugins.jetbrains.kotlin.multiplatform))
    implementation(plugin(libs.plugins.kotlin.compose.compiler))
    implementation(plugin(libs.plugins.ktlint))
    implementation(plugin(libs.plugins.vanniktech.maven.publish))
}

private fun plugin(dependency: Provider<PluginDependency>): Provider<String> =
    dependency.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
