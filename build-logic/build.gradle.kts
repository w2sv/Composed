plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(plugin(libs.plugins.android.library))
    implementation(plugin(libs.plugins.kotlin.android))
    implementation(plugin(libs.plugins.ktlint))
}

private fun plugin(dependency: Provider<PluginDependency>): Provider<String> =
    dependency.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
