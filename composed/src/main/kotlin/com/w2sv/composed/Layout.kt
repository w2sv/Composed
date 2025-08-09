package com.w2sv.composed

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * [Column] whose [elements], rendered through [makeElement], will be divided by [makeDivider]. [makeDivider] will be invoked only in between elements, that is, neither before the first, nor after the last element.
 */
@Composable
fun <T> InterElementDividedColumn(
    @SuppressLint("ComposeUnstableCollections") elements: List<T>,
    makeElement: @Composable ColumnScope.(T) -> Unit,
    modifier: Modifier = Modifier,
    makeDivider: @Composable ColumnScope.() -> Unit = { HorizontalDivider() },
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        elements.forEachIndexed { index, element ->
            makeElement(element)
            if (index != elements.lastIndex) {
                makeDivider()
            }
        }
    }
}

/**
 * [Row] whose [elements], rendered through [makeElement], will be divided by [makeDivider]. [makeDivider] will be invoked only in between elements, that is, neither before the first, nor after the last element.
 */
@Composable
fun <T> InterElementDividedRow(
    @SuppressLint("ComposeUnstableCollections") elements: List<T>,
    makeElement: @Composable RowScope.(T) -> Unit,
    modifier: Modifier = Modifier,
    makeDivider: @Composable RowScope.() -> Unit = { VerticalDivider() },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        elements.forEachIndexed { index, element ->
            makeElement(element)
            if (index != elements.lastIndex) {
                makeDivider()
            }
        }
    }
}
