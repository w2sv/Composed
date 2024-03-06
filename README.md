<h1 align="center">Composed</h1>

<p align="center">    
    <a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API">
</a>
    
<a href="https://jitpack.io/#w2sv/Composed">
  <img src="https://jitpack.io/v/w2sv/Composed.svg" alt="JitPack">
</a>

<a href="https://github.com/w2sv/Compose-Utils/actions/workflows/workflow.yaml">
    <img src="https://github.com/w2sv/Compose-Utils/actions/workflows/workflow.yaml/badge.svg" alt="Build">
</a>

<img src="https://img.shields.io/github/license/w2sv/Compose-Utils" alt="GitHub License">
</p>

------

<p align="center">
<b>A collection of utils to facilitate development with Jetpack Compose.</b>
</p>

------

# Installation

Make sure you have jitpack added to your dependency resolution repositories by adding the following to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
  repositories {
    maven("https://jitpack.io")
  }
}
```

Then add the dependencies you lust for to your `build.gradle.kts` files:

```kotlin
dependencies {
    // Core utils
    implementation 'com.w2sv.Composed:composed:<latest-version>'
    // Permission utils
    implementation 'com.w2sv.Composed:permissions:<latest-version>'
}
```

# Contents

- [State Savers](#state-savers)
- [Styled Text](#styled-text)
- [Modifiers](#modifiers)
- [Flow Collectors](#flow-collectors)
- [Lifecycle Observers](#lifecycle-observers)
- [Orientation](#orientation)
- [Dimension Conversion](#dimension-conversion)
- [Color Conversion](#color-conversion)
- [Map Conversion](#map-conversion)
- [Drawer State](#drawer-state)
- [Permission States](#permission-states)

## State Savers

```kotlin
/**
 * Returns a rememberSavable state saver for Color.
 */
fun colorSaver(): Saver<Color, Int>

/**
 * Returns a rememberSavable state saver for an optional Color.
 */
fun nullableColorSaver(): Saver<Color?, Float>

/**
 * listSaver for an optional object, enabling handling of non-null instances only.
 */
fun <Original, Saveable> nullableListSaver(
    saveNonNull: SaverScope.(value: Original) -> List<Saveable>,
    restoreNonNull: (list: List<Saveable>) -> Original?
): Saver<Original?, Any>

/**
 * mapSaver for an optional object, enabling handling of non-null instances only.
 */
fun <T> nullableMapSaver(
    saveNonNull: SaverScope.(value: T) -> Map<String, Any?>,
    restoreNonNull: (Map<String, Any?>) -> T
): Saver<T, Any>
```

## Styled Text

```kotlin
/**
 * Converts a HTML-styled string resource text to a remembered AnnotatedString.
 */
@Composable
fun rememberStyledTextResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString
```

## Modifiers

```kotlin
/**
 * Applies modifiers depending on a condition.
 */
inline fun Modifier.thenIf(
    condition: Boolean,
    onFalse: Modifier.() -> Modifier = { this },
    onTrue: Modifier.() -> Modifier = { this },
): Modifier
```

## Flow Collectors

```kotlin
/**
 * Collects from a flow and emits values into a collector.
 */
@Composable
fun <T> CollectFromFlow(
    flow: Flow<T>,
    collector: FlowCollector<T>,
    key1: Any? = null,
    key2: Any? = null
)

/**
 * Collects latest from a flow with given action.
 */
@Composable
fun <T> CollectLatestFromFlow(
    flow: Flow<T>,
    action: suspend (value: T) -> Unit,
    key1: Any? = null,
    key2: Any? = null
)
```

## Lifecycle Observers

```kotlin
/**
 * Runs a callback whenever the lifecycleOwner reaches the given lifecycleEvent.
 */
@Composable
fun OnLifecycleEvent(
    callback: () -> Unit,
    lifecycleEvent: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    key1: Any? = null,
    key2: Any? = null
)

/**
 * Runs a callback when removed from composition.
 */
@Composable
fun OnRemoveFromComposition(callback: () -> Unit)
```

## Orientation

```kotlin
/**
 * Returns true if the landscape mode is active, false otherwise.
 */
val isLandscapeModeActive: Boolean

/**
 * Returns true if the portrait mode is active, false otherwise.
 */
val isPortraitModeActive: Boolean
```

## Dimension Conversion

```kotlin
/**
 * Converts Dp to pixels.
 */
@Composable
@ReadOnlyComposable
fun Dp.toPx(): Float

/**
 * Converts pixels to Dp.
 */
@Composable
@ReadOnlyComposable
fun Int.toDp(): Dp

/**
 * Converts pixels to Dp.
 */
@Composable
@ReadOnlyComposable
fun Float.toDp(): Dp
```

## Color Conversion

```kotlin
/**
 * Converts a hex color string to Color.
 */
fun String.toComposeColor(): Color
```

## Map Conversion

```kotlin
/**
 * Converts a regular Map to a SnapshotStateMap.
 */
fun <K, V> Map<K, V>.toMutableStateMap(): SnapshotStateMap<K, V>
```

## Drawer State

```kotlin
/**
 * Returns a State<Float> whose value ranges from 0.0 (drawer closed) to 1.0 (drawer fully open).
 */
fun DrawerState.visibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float): State<Float>

/**
 * Remembers a visibility percentage for the drawer.
 */
@Composable
fun DrawerState.rememberVisibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float = DrawerDefaults.MaximumDrawerWidth.toPx()): State<Float>
```

## Permission States

```kotlin
/**
 * Permission state which, as opposed to the accompanist ones,
 * - exposes a [grantedFromRequest] shared flow to allow for distributed subscription and callback invocation, instead of only being able to pass a onPermissionResult callback upon instantiation, which needs to cover all granting reactions, possibly impacting various components
 * - allows for callbacks upon permission requesting being suppressed
 */
@Stable
interface ExtendedPermissionState {
    val granted: Boolean
    val grantedFromRequest: SharedFlow<Boolean>
    fun launchRequest(onSuppressed: (() -> Unit)? = null)
}

// With the implementations:

@Stable
open class ExtendedSinglePermissionState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    permissionState: PermissionState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val defaultOnLaunchingSuppressed: () -> Unit = {}
) : PermissionState by permissionState, ExtendedPermissionState

@Composable
fun rememberExtendedSinglePermissionState(
    permission: String,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    defaultOnPermissionResult: (Boolean) -> Unit = {},
    defaultOnLaunchingSuppressed: () -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope()
): ExtendedSinglePermissionState

// And

@Stable
open class ExtendedMultiplePermissionsState(
    private val requestLaunchedBefore: StateFlow<Boolean>,
    multiplePermissionsState: MultiplePermissionsState,
    override val grantedFromRequest: SharedFlow<Boolean>,
    private val defaultOnLaunchingSuppressed: () -> Unit = {}
) : MultiplePermissionsState by multiplePermissionsState, ExtendedPermissionState

@Composable
fun rememberExtendedMultiplePermissionsState(
    permissions: List<String>,
    requestLaunchedBefore: StateFlow<Boolean>,
    saveRequestLaunched: () -> Unit,
    defaultOnPermissionResult: (Map<String, Boolean>) -> Unit = {},
    defaultOnLaunchingSuppressed: () -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope()
): ExtendedMultiplePermissionsState
```

# License
```xml
Designed and developed by 2024 w2sv (Janek Zangenberg)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
