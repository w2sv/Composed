<p align="center">
    <img src="assets/composed-banner.webp" alt="Composed — Compose without Overdose.">
</p>

<p align="center">
    <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin Multiplatform">
    <img src="https://img.shields.io/badge/Compose-Multiplatform-4285F4" alt="Compose Multiplatform">

<a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API 21+">
</a>

<a href="https://central.sonatype.com/artifact/io.github.w2sv/composed-core">
    <img src="https://img.shields.io/maven-central/v/io.github.w2sv/composed-core" alt="Maven Central">
</a>

<a href="https://w2sv.github.io/Composed/">
    <img src="https://img.shields.io/badge/docs-API_reference-blue" alt="API Reference">
</a>

<a href="https://github.com/w2sv/Composed/actions/workflows/build.yaml">
    <img src="https://github.com/w2sv/Composed/actions/workflows/build.yaml/badge.svg" alt="Build">
</a>

<img src="https://img.shields.io/github/license/w2sv/Composed" alt="License">
</p>

<p align="center">
    <img src="https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white" alt="Android">
    <img src="https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white" alt="iOS">
    <img src="https://img.shields.io/badge/JVM%2FDesktop-4C2A73?logo=openjdk&logoColor=white" alt="JVM and Desktop">
    <img src="https://img.shields.io/badge/Wasm-654FF0?logo=webassembly&logoColor=white" alt="WebAssembly">
</p>

---

<p align="center">
    <b>Small Compose helpers you keep pasting from project to project.</b>
</p>

Composed is a lightweight utility library for [Compose Multiplatform](https://github.com/jetbrains/compose-multiplatform) that fills in recurring gaps around Compose APIs.
It provides focused helpers for effects, snapshot state and savers, modifiers, gesture coordination, focus handling, and Material 3 behavior — without introducing a framework, design system, or collection of opinionated UI components.

Platform-specific utilities stay explicitly scoped to their respective targets, while the common API remains usable across supported Compose Multiplatform platforms.

See the [API reference](https://w2sv.github.io/Composed/) for the full documentation.

> Web/Wasm support follows Compose Multiplatform's Beta status.

---

## 📦 Modules

| Module | Description |
|---|---|
| `composed-core` | General-purpose Compose utilities for effects, savers, modifiers, collections, colors, and dimensions. Android resource, configuration, and View-system extensions are available on Android only. |
| `composed-material3` | Utilities and extensions for Compose Material 3 layouts, drawers, and snackbars. |

Android permission-state utilities are available separately at [AugmentedPermissions](https://github.com/w2sv/AugmentedPermissions).

## 🚀 Installation

### Inline

```kotlin
dependencies {
    implementation("io.github.w2sv:composed-core:<version>")
    implementation("io.github.w2sv:composed-material3:<version>")
}
```

### Version Catalog (`libs.versions.toml`)

```toml
[versions]
w2sv-composed = "<version>"

[libraries]
w2sv-composed-core = { module = "io.github.w2sv:composed-core", version.ref = "w2sv-composed" }
w2sv-composed-material3 = { module = "io.github.w2sv:composed-material3", version.ref = "w2sv-composed" }
```

**build.gradle.kts:**

```kotlin
dependencies {
    implementation(libs.w2sv.composed.core)
    implementation(libs.w2sv.composed.material3)
}
```

## 📄 License

Designed and developed by w2sv (Janek Zangenberg).

Licensed under the [Apache License 2.0](LICENSE).
