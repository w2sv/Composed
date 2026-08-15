package com.w2sv.composed.runtime

import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

/**
 * Suspends until [condition] evaluates to `true`.
 *
 * [condition] should read snapshot-observable state.
 */
suspend inline fun awaitSnapshotCondition(crossinline condition: () -> Boolean) {
    snapshotFlow { condition() }.first { it }
}
