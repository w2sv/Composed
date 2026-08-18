package com.w2sv.composed.ui

import androidx.compose.ui.Modifier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Appends a [Modifier] produced by [block].
 *
 * [block] receives an empty [Modifier], allowing modifier extensions to be
 * used without repeating `Modifier` in conditional or dynamic chains.
 *
 * Example:
 * ```
 * modifier.then {
 *     when {
 *         isSelected -> background(Color.Green)
 *         isDisabled -> alpha(0.5f)
 *         else -> this
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
inline fun Modifier.then(block: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return then(block(Modifier))
}

/**
 * Applies [onTrue] or [onFalse] depending on [condition].
 *
 * Inside [onTrue], [condition] is known to be `true`; inside [onFalse], it is
 * known to be `false`.
 *
 * Example:
 * ```
 * Modifier.thenIf(
 *     condition = isSelected,
 *     onTrue = { background(Color.Green) },
 *     onFalse = { background(Color.Gray) }
 * )
 * ```
 */
@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
inline fun Modifier.thenIf(
    condition: Boolean,
    onFalse: Modifier.() -> Modifier = { this },
    onTrue: Modifier.() -> Modifier = { this }
): Modifier {
    contract {
        callsInPlace(onTrue, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFalse, InvocationKind.AT_MOST_ONCE)

        condition holdsIn onTrue
        !condition holdsIn onFalse
    }

    return if (condition) {
        onTrue()
    } else {
        onFalse()
    }
}

/**
 * Applies [onTrue] when [condition] is `true`.
 *
 * Inside [onTrue], [condition] is known to be `true`.
 *
 * Example:
 * ```
 * Modifier.thenIf(isSelected) {
 *     background(Color.Green)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
inline fun Modifier.thenIf(condition: Boolean, onTrue: Modifier.() -> Modifier): Modifier {
    contract {
        callsInPlace(onTrue, InvocationKind.AT_MOST_ONCE)
        condition holdsIn onTrue
    }

    return thenIf(
        condition = condition,
        onFalse = { this },
        onTrue = onTrue
    )
}

/**
 * Applies [onNotNull] when [value] is not null.
 *
 * Inside [onNotNull], [value] is known to be non-null.
 *
 * Example:
 * ```
 * Modifier.thenIfNotNull(backgroundColor) { color ->
 *     background(color)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
inline fun <T> Modifier.thenIfNotNull(value: T?, onNotNull: Modifier.(T) -> Modifier): Modifier {
    contract {
        callsInPlace(onNotNull, InvocationKind.AT_MOST_ONCE)
        (value != null) holdsIn onNotNull
    }

    return value?.let { onNotNull(it) } ?: this
}
