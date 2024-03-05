<h1 align="center">Compose Utils</h1>

<p align="center">    
    <a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API">
</a>
<img src="https://img.shields.io/github/v/release/w2sv/Compose-Utils?include_prereleases" alt="GitHub release (latest by date including pre-releases)">
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

## Contents

- [State Savers](#state-savers)
- [Styled Text](#styled-text)
- [Modifiers](#modifiers)
- [Flow Collectors](#flow-collectors)
- [Lifecycle Observers](#lifecycle-observers)
- [Orientation Utilities](#orientation-utilities)
- [Dimension Conversion](#dimension-conversion)
- [Color Conversion](#color-conversion)
- [Drawer State](#drawer-state)

## State Savers

### `colorSaver()`

Returns a `rememberSavable` state saver for `Color`.

```kotlin
fun colorSaver(): Saver<Color, Int>
```

### `nullableColorSaver()`

Returns a `rememberSavable` state saver for an optional `Color`.

```kotlin
fun nullableColorSaver(): Saver<Color?, Float>
```

### `nullableListSaver()`

`listSaver` for an optional object, enabling handling of non-null instances only.

```kotlin
fun <Original, Saveable> nullableListSaver(
    saveNonNull: SaverScope.(value: Original) -> List<Saveable>,
    restoreNonNull: (list: List<Saveable>) -> Original?
): Saver<Original?, Any>
```

### `nullableMapSaver()`

`mapSaver` for an optional object, enabling handling of non-null instances only.

```kotlin
fun <T> nullableMapSaver(
    saveNonNull: SaverScope.(value: T) -> Map<String, Any?>,
    restoreNonNull: (Map<String, Any?>) -> T
): Saver<T, Any>
```

## Styled Text

### `rememberStyledTextResource()`

Convert a HTML-styled string resource text to an `AnnotatedString` and remember it.

```kotlin
@Composable
fun rememberStyledTextResource(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString
```

## Modifiers

### `Modifier.thenIf()`

Applies modifiers depending on a condition.

```kotlin
inline fun Modifier.thenIf(
    condition: Boolean,
    onFalse: Modifier.() -> Modifier = { this },
    onTrue: Modifier.() -> Modifier = { this },
): Modifier
```

## Flow Collectors

### `CollectFromFlow()`

Collects from a flow and emits values into a collector.

```kotlin
@Composable
fun <T> CollectFromFlow(
    flow: Flow<T>,
    collector: FlowCollector<T>,
    key1: Any? = null,
    key2: Any? = null
)
```

### `CollectLatestFromFlow()`

Collects latest from a flow with given action.

```kotlin
@Composable
fun <T> CollectLatestFromFlow(
    flow: Flow<T>,
    action: suspend (value: T) -> Unit,
    key1: Any? = null,
    key2: Any? = null
)
```

## Lifecycle Observers

### `OnLifecycleEvent()`

Runs a callback whenever the lifecycleOwner reaches the given lifecycleEvent.

```kotlin
@Composable
fun OnLifecycleEvent(
    callback: () -> Unit,
    lifecycleEvent: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    key1: Any? = null,
    key2: Any? = null
)
```

### `OnRemoveFromComposition()`

Runs a callback when removed from composition.

```kotlin
@Composable
fun OnRemoveFromComposition(callback: () -> Unit)
```

## Orientation Utilities

### `isLandscapeModeActive`

```kotlin
val isLandscapeModeActive: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
```

### `isPortraitModeActive`

```kotlin
val isPortraitModeActive: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
```

## Dimension Conversion

### `toPx()`

Converts `Dp` to pixels.

```kotlin
@Composable
@ReadOnlyComposable
fun Dp.toPx(): Float
```

### `toDp()`

Converts pixels to `Dp`.

```kotlin
@Composable
@ReadOnlyComposable
fun Int.toDp(): Dp
```

### `toDp()`

Converts pixels to `Dp`.

```kotlin
@Composable
@ReadOnlyComposable
fun Float.toDp(): Dp
```

## Color Conversion

### `toComposeColor()`

Converts a hex color string to `Color`.

```kotlin
fun String.toComposeColor(): Color
```

## Drawer State

### `DrawerState.visibilityPercentage()`

Returns a `State<Float>` whose value ranges from 0.0 (drawer closed) to 1.0 (drawer fully open).

```kotlin
fun DrawerState.visibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float): State<Float>
```

### `DrawerState.rememberVisibilityPercentage()`

```kotlin
@Composable
fun DrawerState.rememberVisibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float = DrawerDefaults.MaximumDrawerWidth.toPx()): State<Float> =
```
