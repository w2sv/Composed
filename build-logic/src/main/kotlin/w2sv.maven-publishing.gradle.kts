plugins {
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

// Include every module applying this convention plugin in the root Dokka site.
rootProject.dependencies.add("dokka", project)

mavenPublishing {
    // Use module name as artifactId.
    coordinates(
        artifactId = project.name,
        version = rootProject.version.toString()
    )
}
