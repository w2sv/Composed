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
    implementation(plugin(libs.plugins.android.library))
    implementation(plugin(libs.plugins.kotlin.android))
    implementation(plugin(libs.plugins.kotlin.compose.compiler))
    implementation(plugin(libs.plugins.ktlint))
}

private fun plugin(dependency: Provider<PluginDependency>): Provider<String> =
    dependency.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
