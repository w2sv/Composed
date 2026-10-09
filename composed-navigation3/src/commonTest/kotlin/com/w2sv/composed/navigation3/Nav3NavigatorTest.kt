package com.w2sv.composed.navigation3

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Nav3NavigatorTest {
    private data class Route(val name: String) : NavKey

    private class Navigator(backStack: NavBackStack<Route>, log: (() -> String) -> Unit = {}) : Nav3Navigator<Route>(backStack, log) {
        fun launch(route: Route) =
            launchSingleTop(route)

        fun popCurrent(route: Route) =
            popIfCurrent(route)

        fun clearAndLaunchRoute(route: Route) =
            clearAndLaunch(route)

        fun launchNested(stack: NavBackStack<Route>, route: Route) =
            stack.launchSingleTop(route)

        fun popNested(stack: NavBackStack<Route>) =
            stack.pop()

        fun popNestedCurrent(stack: NavBackStack<Route>, route: Route) =
            stack.popIfCurrent(route)

        fun resetNested(
            stack: NavBackStack<Route>,
            first: Route,
            vararg remaining: Route
        ) =
            stack.resetTo(first, *remaining)
    }

    private class NestedNavigator(backStack: NavBackStack<NavKey>, log: (() -> String) -> Unit = {}) :
        Nav3Navigator<NavKey>(backStack, log) {
        fun logDomainSkip() =
            logNavigationSkipped("OpenDetails", "unavailable", current = currentRoute)
    }

    private class FirstHost(override val backStack: NavBackStack<Route>) : NestedNavHost<Route>

    private class SecondHost(override val backStack: NavBackStack<Route>) : NestedNavHost<Route>

    private val root = Route("root")
    private val details = Route("details")
    private val settings = Route("settings")

    @Test
    fun `currentRoute returns the last entry and rejects an empty stack`() {
        val stack = NavBackStack(root, details)
        val navigator = Navigator(stack)

        assertEquals(details, navigator.currentRoute)
        stack.clear()
        assertEquals(
            "Called currentRoute on empty BackStack",
            assertFailsWith<IllegalStateException> { navigator.currentRoute }.message
        )
    }

    @Test
    fun `popBackStack preserves the root`() {
        val stack = NavBackStack(root, details)
        val navigator = Navigator(stack)

        navigator.popBackStack()
        assertEquals(listOf(root), stack.toList())
        navigator.popBackStack()
        assertEquals(listOf(root), stack.toList())
    }

    @Test
    fun `launchSingleTop uses structural equality and pushes different routes`() {
        val stack = NavBackStack(root, details)
        val navigator = Navigator(stack)

        assertFalse(navigator.launch(Route("details")))
        assertEquals(listOf(root, details), stack.toList())
        assertTrue(navigator.launch(settings))
        assertEquals(listOf(root, details, settings), stack.toList())
    }

    @Test
    fun `popIfCurrent rejects stale requests and preserves the root`() {
        val stack = NavBackStack(root, details)
        val navigator = Navigator(stack)

        assertFalse(navigator.popCurrent(root))
        assertEquals(listOf(root, details), stack.toList())
        assertTrue(navigator.popCurrent(Route("details")))
        assertFalse(navigator.popCurrent(root))
        assertEquals(listOf(root), stack.toList())
    }

    @Test
    fun `clearAndLaunch replaces several entries and equivalent reset is a no-op`() {
        val stack = NavBackStack(root, details, settings)
        val navigator = Navigator(stack)

        assertTrue(navigator.clearAndLaunchRoute(details))
        assertEquals(listOf(details), stack.toList())
        assertFalse(navigator.clearAndLaunchRoute(Route("details")))
        assertEquals(listOf(details), stack.toList())
    }

    @Test
    fun `protected operations work on nested stacks`() {
        val nested = NavBackStack(root)
        val navigator = Navigator(NavBackStack(root))

        assertFalse(navigator.popNested(nested))
        assertTrue(navigator.launchNested(nested, details))
        assertFalse(navigator.launchNested(nested, Route("details")))
        assertFalse(navigator.popNestedCurrent(nested, settings))
        assertTrue(navigator.popNestedCurrent(nested, details))
        assertTrue(navigator.resetNested(nested, settings, details, root))
        assertEquals(listOf(settings, details, root), nested.toList())
        assertFalse(navigator.resetNested(nested, Route("settings"), Route("details"), Route("root")))
        assertEquals(listOf(settings, details, root), nested.toList())
    }

    @Test
    fun `logging reports skipped operations`() {
        val messages = mutableListOf<String>()
        val stack = NavBackStack(root)
        val navigator = Navigator(stack) { message -> messages += message() }

        navigator.popBackStack()
        navigator.launch(Route("root"))
        navigator.popCurrent(details)
        navigator.clearAndLaunchRoute(root)

        assertEquals(
            listOf(
                "Navigation skipped: Pop (root preserved), expectedRoute=null, currentRoute=$root",
                "Navigation skipped: LaunchSingleTop (already current), expectedRoute=$root, currentRoute=$root",
                "Navigation skipped: PopIfCurrent (stale source), expectedRoute=$details, currentRoute=$root",
                "Navigation skipped: ResetTo (stack already matches), expectedRoute=null, currentRoute=$root"
            ),
            messages
        )
    }

    @Test
    fun `default logger needs no setup and messages are evaluated lazily`() {
        val stack = NavBackStack(root)
        Navigator(stack).popBackStack()

        var stringConversions = 0
        val counted = object : NavKey {
            override fun toString(): String {
                stringConversions++
                return "counted"
            }
        }
        val countedStack = NavBackStack<NavKey>(counted)
        val silentNavigator = object : Nav3Navigator<NavKey>(countedStack, log = { _ -> }) {}

        silentNavigator.popBackStack()
        assertEquals(0, stringConversions)
    }

    @Test
    fun `back pops the active nested stack before exiting its host`() {
        val nestedRoot = Route("nested root")
        val host = FirstHost(NavBackStack(nestedRoot, details))
        val stack = NavBackStack<NavKey>(root, host)
        val navigator = NestedNavigator(stack)

        navigator.popBackStack()
        assertEquals(listOf(nestedRoot), host.backStack.toList())
        assertEquals(listOf(root, host), stack.toList())

        navigator.popBackStack()
        assertEquals(listOf(root), stack.toList())
    }

    @Test
    fun `back preserves a root entry that is itself a nested host`() {
        val nestedRoot = Route("nested root")
        val host = FirstHost(NavBackStack(nestedRoot, details))
        val stack = NavBackStack<NavKey>(host)
        val navigator = NestedNavigator(stack)

        navigator.popBackStack()
        assertEquals(listOf(nestedRoot), host.backStack.toList())
        navigator.popBackStack()
        assertEquals(listOf(host), stack.toList())
        assertFalse(navigator.popBackStack(nestedRoot))
        assertEquals(listOf(host), stack.toList())
    }

    @Test
    fun `route-aware back pops a current nested destination then exits at the nested root`() {
        val nestedRoot = Route("nested root")
        val host = FirstHost(NavBackStack(nestedRoot, details))
        val stack = NavBackStack<NavKey>(root, host)
        val navigator = NestedNavigator(stack)

        assertTrue(navigator.popBackStack(Route("details")))
        assertEquals(listOf(nestedRoot), host.backStack.toList())
        assertTrue(navigator.popBackStack(Route("nested root")))
        assertEquals(listOf(root), stack.toList())
    }

    @Test
    fun `route-aware back rejects inactive routes and leaves their stacks unchanged`() {
        val firstRoot = Route("first root")
        val firstDetails = Route("first details")
        val secondRoot = Route("second root")
        val secondDetails = Route("second details")
        val first = FirstHost(NavBackStack(firstRoot, firstDetails))
        val second = SecondHost(NavBackStack(secondRoot, secondDetails))
        val stack = NavBackStack<NavKey>(root, first, second)
        val messages = mutableListOf<String>()
        val navigator = NestedNavigator(stack) { message -> messages += message() }

        assertFalse(navigator.popBackStack(firstDetails))
        assertFalse(navigator.popBackStack(first))
        assertFalse(navigator.popBackStack(second))
        assertFalse(navigator.popBackStack(root))
        assertEquals(listOf(root, first, second), stack.toList())
        assertEquals(listOf(firstRoot, firstDetails), first.backStack.toList())
        assertEquals(listOf(secondRoot, secondDetails), second.backStack.toList())
        assertEquals(
            listOf(
                "Navigation skipped: PopFromRoute (stale source), expectedRoute=$firstDetails, currentRoute=$secondDetails",
                "Navigation skipped: PopFromRoute (stale source), expectedRoute=$first, currentRoute=$secondDetails",
                "Navigation skipped: PopFromRoute (stale source), expectedRoute=$second, currentRoute=$secondDetails",
                "Navigation skipped: PopFromRoute (stale source), expectedRoute=$root, currentRoute=$secondDetails"
            ),
            messages
        )

        stack.removeAt(stack.lastIndex)
        assertTrue(navigator.popBackStack(firstDetails))
        assertEquals(listOf(firstRoot), first.backStack.toList())
        assertEquals(listOf(secondRoot, secondDetails), second.backStack.toList())
    }

    @Test
    fun `route-aware back preserves ordinary root-stack behavior and logs stale sources`() {
        val stack = NavBackStack<NavKey>(root, details)
        val messages = mutableListOf<String>()
        val navigator = NestedNavigator(stack) { message -> messages += message() }

        assertFalse(navigator.popBackStack(root))
        assertEquals(
            "Navigation skipped: PopFromRoute (stale source), expectedRoute=$root, currentRoute=$details",
            messages.single()
        )
        assertTrue(navigator.popBackStack(Route("details")))
        assertFalse(navigator.popBackStack(root))
        assertEquals(listOf(root), stack.toList())
        navigator.logDomainSkip()
        assertEquals(
            "Navigation skipped: OpenDetails (unavailable), expectedRoute=null, currentRoute=$root",
            messages.last()
        )
    }
}
