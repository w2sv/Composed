package com.w2sv.composed.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.savedstate.serialization.SavedStateConfiguration

/**
 * Remembers a type-safe navigation back stack across configuration changes and process death.
 *
 * Unlike [androidx.navigation3.runtime.rememberNavBackStack], preserves the navigation key type
 * [T] and supports serializable sealed route hierarchies without explicit serializer registration.
 * Specify the hierarchy type explicitly so Kotlin does not infer the concrete initial route type:
 *
 * ```kotlin
 * val backStack = rememberTypedNavBackStack<AppRoute>(
 *     AppRoute.Home
 * )
 * ```
 *
 * @param initialKeys Initial entries of the back stack.
 * @param configuration Optional saved-state serialization configuration.
 */
@Composable
inline fun <reified T : NavKey> rememberTypedNavBackStack(
    vararg initialKeys: T,
    configuration: SavedStateConfiguration = SavedStateConfiguration.DEFAULT
): NavBackStack<T> =
    rememberSerializable(
        serializer = NavBackStackSerializer<T>(),
        configuration = configuration
    ) {
        NavBackStack(*initialKeys)
    }
