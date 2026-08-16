<h1 align="center">Composed</h1>

<p align="center">
    <a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API">
</a>

<img src="https://img.shields.io/maven-central/v/io.github.w2sv/composed-core" alt="Maven Central Version">

<a href="https://w2sv.github.io/Composed/">
    <img src="https://img.shields.io/badge/docs-API_reference-blue" alt="API Reference">
</a>

<a href="https://github.com/w2sv/Composed/actions/workflows/workflow.yaml">
    <img src="https://github.com/w2sv/Composed/actions/workflows/workflow.yaml/badge.svg" alt="Build">
</a>

<img src="https://img.shields.io/github/license/w2sv/Composed" alt="GitHub License">
</p>

<p align="center">
<img src="https://img.shields.io/badge/Android-3DDC84?logo=android&amp;logoColor=white" alt="Android">
<img src="https://img.shields.io/badge/JVM%2FDesktop-4C2A73?logo=openjdk&amp;logoColor=white" alt="JVM and Desktop">
<img src="https://img.shields.io/badge/iOS-000000?logo=apple&amp;logoColor=white" alt="iOS">
<img src="https://img.shields.io/badge/Wasm-654FF0?logo=webassembly&amp;logoColor=white" alt="WebAssembly">
</p>

---

<p align="center">
<b>A collection of utilities for Compose Multiplatform.</b>
</p>

Composed provides small, focused helpers for common Compose patterns, including effects, savers, modifiers, gesture handling and Material 3 state handling.
It is not a UI component library or design system. It just helps you get the job done with even more streamlined compose code.

Platform-specific utilities remain explicitly scoped to Android.
Web/Wasm support follows Compose Multiplatform's Beta status.

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

Designed and developed by 2024 w2sv (Janek Zangenberg).

Licensed under the [Apache License 2.0](LICENSE).
