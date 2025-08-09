import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versionCatalogUpdate)
}

versionCatalogUpdate {
    versionSelector(VersionSelectors.PREFER_STABLE)
}
