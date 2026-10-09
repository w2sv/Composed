package com.w2sv.composed.navigation3

import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Provides guarded navigation operations for [backStack], preventing redundant mutations and
 * rejecting stale navigation requests.
 *
 * Supports [NestedNavHost] destinations by prioritizing their nested back stacks during back
 * navigation. Only one level of nesting is handled.
 *
 * Back navigation preserves the root entry of each stack. The root stack must be non-empty,
 * although direct mutations of [backStack] can violate this invariant.
 *
 * @param log Optional callback for lazily logging skipped navigation operations. Messages are
 * evaluated only when the callback invokes them.
 */
abstract class Nav3Navigator<T : NavKey>(val backStack: NavBackStack<T>, private val log: (() -> String) -> Unit = {}) {
    /** The current root route. An empty root stack is a structural navigator error. */
    val currentRoute: T
        get() = checkNotNull(backStack.lastOrNull()) { "Called currentRoute on empty BackStack" }

    private val currentNestedBackStack: NavBackStack<*>?
        get() = (currentRoute as? NestedNavHost<*>)?.backStack

    /** Pops the active nested stack first, then falls back to the root stack. */
    open fun popBackStack() {
        val nested = currentNestedBackStack

        if (nested != null && nested.size > 1) {
            nested.pop()
        } else {
            backStack.pop()
        }
    }

    /**
     * Handles back navigation only when [from] equals the current route of the active stack.
     *
     * If [from] is the root of an active nested stack, exits its owning host.
     * Route equality is structural and does not identify the originating stack.
     *
     * @return whether a stack changed.
     */
    fun popBackStack(from: NavKey): Boolean {
        val host = currentRoute
        val nested = currentNestedBackStack
        val current = if (nested != null) nested.lastOrNull() else host

        return when {
            current != from -> {
                logNavigationSkipped(
                    operation = "PopFromRoute",
                    reason = "stale source",
                    expected = from,
                    current = current
                )
                false
            }

            nested == null -> backStack.pop()

            nested.size > 1 -> nested.pop()

            else -> backStack.popIfCurrent(host)
        }
    }

    /** Adds [target] to the root stack unless it is already current. */
    protected fun launchSingleTop(target: T): Boolean =
        backStack.launchSingleTop(target)

    /**
     * Adds [target] only when it differs from the current top route.
     *
     * @return whether the stack changed.
     */
    protected fun <R : NavKey> NavBackStack<R>.launchSingleTop(target: R): Boolean {
        val current = lastOrNull()

        if (current == target) {
            logNavigationSkipped(
                operation = "LaunchSingleTop",
                reason = "already current",
                expected = target,
                current = current
            )
            return false
        }

        add(target)
        return true
    }

    /**
     * Removes the current top route while preserving the stack's root entry.
     *
     * @return whether the stack changed.
     */
    protected fun <R : NavKey> NavBackStack<R>.pop(): Boolean {
        if (size <= 1) {
            logNavigationSkipped(
                operation = "Pop",
                reason = "root preserved",
                current = lastOrNull()
            )
            return false
        }

        removeAt(lastIndex)
        return true
    }

    /**
     * Pops only when [expectedTop] equals the current route.
     *
     * Route equality is structural and does not distinguish individual occurrences of
     * equivalent routes in the back stack.
     *
     * @return whether the stack changed.
     */
    protected fun <R : NavKey> NavBackStack<R>.popIfCurrent(expectedTop: R): Boolean {
        val current = lastOrNull()

        if (current != expectedTop) {
            logNavigationSkipped(
                operation = "PopIfCurrent",
                reason = "stale source",
                expected = expectedTop,
                current = current
            )
            return false
        }

        return pop()
    }

    /**
     * Replaces the complete stack with [first] and [remaining], unless it already matches.
     *
     * The replacement is performed within a mutable snapshot to avoid publishing an
     * intermediate empty stack.
     *
     * @return whether the stack changed.
     */
    protected fun <R : NavKey> NavBackStack<R>.resetTo(first: R, vararg remaining: R): Boolean {
        val routes = listOf(first, *remaining)

        if (toList() == routes) {
            logNavigationSkipped(
                operation = "ResetTo",
                reason = "stack already matches",
                current = lastOrNull()
            )
            return false
        }

        Snapshot.withMutableSnapshot {
            clear()
            addAll(routes)
        }

        return true
    }

    /** Pops [expectedRoute] only when it identifies the current root route. */
    protected fun popIfCurrent(expectedRoute: T): Boolean =
        backStack.popIfCurrent(expectedRoute)

    /**
     * Replaces the complete root stack with [target].
     *
     * @return whether the root stack changed.
     */
    protected fun clearAndLaunch(target: T): Boolean =
        backStack.resetTo(target)

    /** Lazily records a navigation call that intentionally left its stack unchanged. */
    protected fun logNavigationSkipped(
        operation: String,
        reason: String,
        expected: NavKey? = null,
        current: NavKey? = null
    ) {
        log {
            "Navigation skipped: $operation ($reason), " +
                "expectedRoute=$expected, currentRoute=$current"
        }
    }
}
