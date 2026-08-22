package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.w2sv.composed.ui.layout.animatedspacing.AlignmentLineProvider
import com.w2sv.composed.ui.layout.animatedspacing.AnimatedSpacingColumnMeasurePolicy
import com.w2sv.composed.ui.layout.animatedspacing.AnimatedSpacingColumnVisibility
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingColumnAlign
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingColumnAlignBy
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingColumnWeight

/**
 * A column whose spacing responds to the visibility progress of children added through
 * [AnimatedSpacingColumnScope.AnimatedVisibility].
 */
@Composable
@ExperimentalAnimatedSpacingApi
fun AnimatedSpacingColumn(
    spacing: Dp,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable AnimatedSpacingColumnScope.() -> Unit
) {
    require(spacing >= 0.dp) { "spacing must be non-negative" }

    val measurePolicy = remember(spacing, horizontalAlignment) {
        AnimatedSpacingColumnMeasurePolicy(spacing, horizontalAlignment)
    }

    Layout(
        content = { AnimatedSpacingColumnScopeInstance.content() },
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}

@LayoutScopeMarker
@Immutable
@ExperimentalAnimatedSpacingApi
interface AnimatedSpacingColumnScope : ColumnScope {

    /** Animates a child's height, optional fade, surrounding spacing, and weighted allocation. */
    @Composable
    fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier = Modifier,
        animationSpec: FiniteAnimationSpec<Float> = spring(),
        fade: Boolean = true,
        label: String = "AnimatedVisibility",
        content: @Composable () -> Unit
    )
}

@OptIn(ExperimentalAnimatedSpacingApi::class)
private object AnimatedSpacingColumnScopeInstance : AnimatedSpacingColumnScope {

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        animatedSpacingColumnWeight(weight, fill)

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        animatedSpacingColumnAlign(alignment)

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier =
        animatedSpacingColumnAlignBy(AlignmentLineProvider.Value(alignmentLine))

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier =
        animatedSpacingColumnAlignBy(AlignmentLineProvider.Block(alignmentLineBlock))

    @Composable
    override fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier,
        animationSpec: FiniteAnimationSpec<Float>,
        fade: Boolean,
        label: String,
        content: @Composable () -> Unit
    ) {
        AnimatedSpacingColumnVisibility(
            visible = visible,
            modifier = modifier,
            animationSpec = animationSpec,
            fade = fade,
            label = label,
            content = content
        )
    }
}
