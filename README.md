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

Composed is a lightweight utility library
for [Compose Multiplatform](https://github.com/jetbrains/compose-multiplatform) that fills in
recurring gaps around Compose APIs.
It provides focused helpers for **effects, snapshot state and savers, modifier composition, layout,
gesture coordination, focus handling, animations, and Material 3 behavior** — without introducing a
framework, design system, or collection of opinionated UI components.

Platform-specific utilities stay explicitly scoped to their respective targets, while the common API
remains usable across supported Compose Multiplatform platforms.

See the [API reference](https://w2sv.github.io/Composed/) for the full documentation.

> Web/Wasm support follows Compose Multiplatform's Beta status.

## ✨ API highlights

### Modifier composition

Conditionally build modifier chains without repeatedly starting from `Modifier`:

```kotlin
Modifier
    .padding(16.dp)
    .then {
        when {
            isSelected -> background(Color.Green)
            isDisabled -> alpha(0.5f)
            else -> this
        }
    }
    .thenIf(isFocused) {
        border(1.dp, Color.Blue)
    }
    .thenIfNotNull(backgroundColor) {
        background(it)
    }
```

### Shake animation

Apply a configurable horizontal shake animation:

```kotlin
val shakeController = rememberShakeController(
    amplitude = 20.dp,
    durationMillis = 400,
    frequencyHz = 8f,
    decay = 0.5f
)

Box(modifier = Modifier.shakenBy(shakeController))

scope.launch {
    shakeController.shake()
}
```

### Snackbar launching

Show snackbars from event handlers without manually carrying around a `SnackbarHostState`, `CoroutineScope`, and, on Android, a `Context`:

```kotlin
val snackbarLauncher = rememberSnackbarLauncher(snackbarHostState)

Button(
    onClick = {
        snackbarLauncher.show { MySnackbarVisuals(message = getString(R.string.saved)) }
    }
) {
    Text("Save")
}
```

Compared to the usual pattern:

```kotlin
val scope = rememberCoroutineScope()
val context = LocalContext.current

Button(
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                MySnackbarVisuals(
                    message = context.getString(R.string.saved)
                )
            )
        }
    }
) {
    Text("Save")
}
```

`SnackbarLauncher` keeps coroutine launching and snackbar presentation behind one non-suspending API while still exposing the current snackbar state and explicit replacement or dismissal operations.
For suspending snackbar display, you may use the `SnackbarController`.

### Focus clearing

Coordinate focus clearing from anywhere in the composition without passing around a `FocusManager`:

```kotlin
val focusClearingController = rememberFocusClearingController()

focusClearingController.Bind()

Column(modifier = Modifier.clearFocusOnTap(focusClearingController)) {
    // ...
}

Button(onClick = focusClearingController::requestClearFocus) {
    Text("Clear focus")
}
```

`FocusClearingController` can also automatically clear focus when the IME transitions from visible
to hidden.

## 📦 Modules

| Module               | Description                                                                      |
|----------------------|----------------------------------------------------------------------------------|
| `composed-core`      | General-purpose Compose utilities. Contains also Android-only utilities.         |
| `composed-material3` | Utilities and extensions for Compose Material 3 layouts, drawers, and snackbars. |

Android permission-state utilities are available separately
at [AugmentedPermissions](https://github.com/w2sv/AugmentedPermissions).

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

## 🖥️ Desktop playground

The [`playground`](playground) module contains a Compose Desktop app for interactively
testing visual and behavioral APIs.

Run it with:

```bash
./gradlew :playground:run
```

Or launch it with Compose Hot Reload:

```bash
./gradlew :playground:hotRunJvm --auto
```

## 📄 License

Designed and developed by w2sv (Janek Zangenberg).

Licensed under the [Apache License 2.0](LICENSE).
