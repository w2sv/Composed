package com.w2sv.composed.animation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Immutable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Describes the grid's scroll context when an item begins its entrance animation. */
enum class LazyGridItemEntranceScrollContext {
    /** The item entered before the grid recorded a forward or backward scroll. */
    InitialLayout,

    /** The item entered after the grid was most recently scrolled forward. */
    AfterForwardScroll,

    /** The item entered after the grid was most recently scrolled backward. */
    AfterBackwardScroll
}

/**
 * Describes an entering item's position relative to the currently visible grid items.
 *
 * Offsets start at zero at the edge from which the entrance sequence proceeds. Their direction
 * accounts for the grid orientation and [scrollContext], so delay implementations can be shared
 * by vertical and horizontal grids.
 *
 * @property itemIndex The item's absolute index in the lazy grid content.
 * @property row The item's absolute grid row, coerced to zero when unavailable.
 * @property column The item's absolute grid column, coerced to zero when unavailable.
 * @property mainAxisOffset The item's line offset along the scrolling axis.
 * @property crossAxisOffset The item's slot offset along the axis perpendicular to scrolling.
 * @property itemOffset The item's flattened, main-axis-first slot offset. Spanned or unoccupied
 * grid slots can produce gaps between offsets.
 * @property scrollContext The grid's most recently observed scrolling context.
 */
@Immutable
@ConsistentCopyVisibility
data class LazyGridItemEntranceDelayScope internal constructor(
    val itemIndex: Int,
    val row: Int,
    val column: Int,
    val mainAxisOffset: Int,
    val crossAxisOffset: Int,
    val itemOffset: Int,
    val scrollContext: LazyGridItemEntranceScrollContext
)

/** Computes how long an item waits before starting its entrance transition. */
fun interface LazyGridItemEntranceDelay {
    /**
     * Computes the delay for the item represented by [scope].
     *
     * Negative results are treated as [Duration.ZERO] by [animateLazyGridItemEntrance].
     *
     * @param scope Position and scroll context for the entering item.
     * @return How long the item waits before its transition starts.
     */
    fun compute(scope: LazyGridItemEntranceDelayScope): Duration

    companion object {
        /** Starts every item without an additional entrance delay. */
        val None: LazyGridItemEntranceDelay = LazyGridItemEntranceDelay { Duration.ZERO }

        /**
         * Staggers items along the grid's scrolling axis. Items on the same main-axis line start
         * together.
         *
         * @param interval Delay between adjacent main-axis lines. Must not be negative.
         */
        fun alongMainAxis(interval: Duration = 200.milliseconds): LazyGridItemEntranceDelay {
            require(interval >= Duration.ZERO)
            return MainAxisDelay(interval)
        }

        /**
         * Staggers items along the axis perpendicular to scrolling. Items at the same cross-axis
         * position start together.
         *
         * @param interval Delay between adjacent cross-axis positions. Must not be negative.
         */
        fun alongCrossAxis(interval: Duration = 150.milliseconds): LazyGridItemEntranceDelay {
            require(interval >= Duration.ZERO)
            return CrossAxisDelay(interval)
        }

        /**
         * Staggers items one grid slot at a time in main-axis-first traversal order.
         *
         * Spanned or unoccupied grid slots can introduce gaps of more than one [interval].
         *
         * @param interval Delay between adjacent grid slots. Must not be negative.
         */
        fun sequential(interval: Duration = 50.milliseconds): LazyGridItemEntranceDelay {
            require(interval >= Duration.ZERO)
            return SequentialDelay(interval)
        }

        /**
         * Staggers items diagonally by combining their main-axis and cross-axis offsets.
         *
         * @param mainAxisInterval Delay between adjacent main-axis lines. Must not be negative.
         * @param crossAxisInterval Delay between adjacent cross-axis positions. Must not be
         * negative.
         */
        fun diagonal(
            mainAxisInterval: Duration = 200.milliseconds,
            crossAxisInterval: Duration = 150.milliseconds
        ): LazyGridItemEntranceDelay {
            require(mainAxisInterval >= Duration.ZERO)
            require(crossAxisInterval >= Duration.ZERO)
            return DiagonalDelay(mainAxisInterval, crossAxisInterval)
        }
    }
}

private data class MainAxisDelay(val interval: Duration) : LazyGridItemEntranceDelay {
    override fun compute(scope: LazyGridItemEntranceDelayScope) =
        interval * scope.mainAxisOffset
}

private data class CrossAxisDelay(val interval: Duration) : LazyGridItemEntranceDelay {
    override fun compute(scope: LazyGridItemEntranceDelayScope) =
        interval * scope.crossAxisOffset
}

private data class SequentialDelay(val interval: Duration) : LazyGridItemEntranceDelay {
    override fun compute(scope: LazyGridItemEntranceDelayScope) =
        interval * scope.itemOffset
}

private data class DiagonalDelay(val mainAxisInterval: Duration, val crossAxisInterval: Duration) : LazyGridItemEntranceDelay {
    override fun compute(scope: LazyGridItemEntranceDelayScope) =
        mainAxisInterval * scope.mainAxisOffset + crossAxisInterval * scope.crossAxisOffset
}

internal fun LazyGridState.currentEntranceScrollContext() =
    when {
        lastScrolledForward -> LazyGridItemEntranceScrollContext.AfterForwardScroll
        lastScrolledBackward -> LazyGridItemEntranceScrollContext.AfterBackwardScroll
        else -> LazyGridItemEntranceScrollContext.InitialLayout
    }

internal fun LazyGridState.createEntranceDelayScope(
    item: LazyGridItemInfo,
    scrollContext: LazyGridItemEntranceScrollContext = currentEntranceScrollContext()
): LazyGridItemEntranceDelayScope {
    val row = item.row.coerceAtLeast(0)
    val column = item.column.coerceAtLeast(0)

    var minVisibleRow = row
    var maxVisibleRow = row
    var minVisibleColumn = column
    var maxVisibleColumn = column

    layoutInfo.visibleItemsInfo.forEach {
        if (it.row >= 0) {
            minVisibleRow = minOf(minVisibleRow, it.row)
            maxVisibleRow = maxOf(maxVisibleRow, it.row)
        }

        if (it.column >= 0) {
            minVisibleColumn = minOf(minVisibleColumn, it.column)
            maxVisibleColumn = maxOf(maxVisibleColumn, it.column)
        }
    }

    val (mainAxisOffset, crossAxisOffset) = when (layoutInfo.orientation) {
        Orientation.Vertical -> when (scrollContext) {
            LazyGridItemEntranceScrollContext.InitialLayout ->
                row - minVisibleRow to column - minVisibleColumn

            LazyGridItemEntranceScrollContext.AfterForwardScroll ->
                maxVisibleRow - row to column - minVisibleColumn

            LazyGridItemEntranceScrollContext.AfterBackwardScroll ->
                row - minVisibleRow to maxVisibleColumn - column
        }

        Orientation.Horizontal -> when (scrollContext) {
            LazyGridItemEntranceScrollContext.InitialLayout ->
                column - minVisibleColumn to row - minVisibleRow

            LazyGridItemEntranceScrollContext.AfterForwardScroll ->
                maxVisibleColumn - column to row - minVisibleRow

            LazyGridItemEntranceScrollContext.AfterBackwardScroll ->
                column - minVisibleColumn to maxVisibleRow - row
        }
    }
    val coercedMainAxisOffset = mainAxisOffset.coerceAtLeast(0)
    val coercedCrossAxisOffset = crossAxisOffset.coerceAtLeast(0)

    return LazyGridItemEntranceDelayScope(
        itemIndex = item.index,
        row = row,
        column = column,
        mainAxisOffset = coercedMainAxisOffset,
        crossAxisOffset = coercedCrossAxisOffset,
        itemOffset = coercedMainAxisOffset * layoutInfo.maxSpan + coercedCrossAxisOffset,
        scrollContext = scrollContext
    )
}
