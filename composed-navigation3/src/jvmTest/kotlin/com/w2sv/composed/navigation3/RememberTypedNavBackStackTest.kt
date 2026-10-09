package com.w2sv.composed.navigation3

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ControlledComposition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class RememberTypedNavBackStackTest {
    @Serializable(with = RouteSerializer::class)
    private data class Route(val name: String) : NavKey {
        companion object {
            fun serializer(): KSerializer<Route> =
                RouteSerializer
        }
    }

    private object RouteSerializer : KSerializer<Route> {
        override val descriptor = PrimitiveSerialDescriptor("Route", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Route) =
            encoder.encodeString(value.name)

        override fun deserialize(decoder: Decoder) =
            Route(decoder.decodeString())
    }

    @Test
    fun `remembers concrete route type and initial entries`() {
        val composition = ControlledComposition(EmptyApplier(), Recomposer(EmptyCoroutineContext))
        lateinit var backStack: NavBackStack<Route>

        try {
            composition.composeContent {
                backStack = rememberTypedNavBackStack(Route("home"), Route("details"))
            }
            composition.applyChanges()

            assertEquals(listOf(Route("home"), Route("details")), backStack.toList())
        } finally {
            composition.dispose()
        }
    }

    @Test
    fun `restores back stack when composition is recreated`() {
        val firstRegistry = SaveableStateRegistry(null) { true }
        lateinit var firstStack: NavBackStack<Route>
        val firstComposition = composeBackStack(firstRegistry) { firstStack = it }

        val saved = try {
            firstStack.add(Route("details"))
            firstRegistry.performSave()
        } finally {
            firstComposition.dispose()
        }

        val restoredRegistry = SaveableStateRegistry(saved) { true }
        lateinit var restoredStack: NavBackStack<Route>
        val restoredComposition = composeBackStack(restoredRegistry) { restoredStack = it }

        try {
            assertEquals(listOf(Route("home"), Route("details")), restoredStack.toList())
        } finally {
            restoredComposition.dispose()
        }
    }

    @Test
    fun `typed back stack serializer restores entries in order with custom configuration`() {
        val stack = NavBackStack(Route("home"), Route("details"))
        val configuration = SavedStateConfiguration { encodeDefaults = true }
        val serializer = NavBackStackSerializer<Route>()

        val saved = encodeToSavedState(serializer, stack, configuration)
        val restored: NavBackStack<Route> = decodeFromSavedState(serializer, saved, configuration)

        assertEquals(stack.toList(), restored.toList())
    }

    private fun composeBackStack(registry: SaveableStateRegistry, onStack: (NavBackStack<Route>) -> Unit): ControlledComposition =
        ControlledComposition(EmptyApplier(), Recomposer(EmptyCoroutineContext)).also { composition ->
            composition.composeContent {
                CompositionLocalProvider(LocalSaveableStateRegistry provides registry) {
                    onStack(rememberTypedNavBackStack<Route>(Route("home")))
                }
            }
            composition.applyChanges()
        }

    private class EmptyApplier : AbstractApplier<Unit>(Unit) {
        override fun insertTopDown(index: Int, instance: Unit) =
            error("No nodes expected")

        override fun insertBottomUp(index: Int, instance: Unit) =
            error("No nodes expected")

        override fun remove(index: Int, count: Int) =
            error("No nodes expected")

        override fun move(
            from: Int,
            to: Int,
            count: Int
        ) =
            error("No nodes expected")

        override fun onClear() =
            Unit
    }
}
