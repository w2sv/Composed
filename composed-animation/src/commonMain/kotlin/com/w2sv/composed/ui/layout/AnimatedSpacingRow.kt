package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.w2sv.composed.ui.layout.animatedspacing.AlignmentLineProvider
import com.w2sv.composed.ui.layout.animatedspacing.AnimatedSpacingRowMeasurePolicy
import com.w2sv.composed.ui.layout.animatedspacing.AnimatedSpacingRowVisibility
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingColumnWeight
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingRowAlign
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingRowAlignBy

/**
 * A row whose spacing responds to the visibility progress of children added through
 * [AnimatedSpacingRowScope.AnimatedVisibility].
 */
@Composable
@ExperimentalAnimatedSpacingApi
fun AnimatedSpacingRow(
    spacing: Dp,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable AnimatedSpacingRowScope.() -> Unit
) {
    require(spacing >= 0.dp) { "spacing must be non-negative" }

    val measurePolicy = remember(spacing, verticalAlignment) {
        AnimatedSpacingRowMeasurePolicy(spacing, verticalAlignment)
    }

    Layout(
        content = { AnimatedSpacingRowScopeInstance.content() },
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}

@LayoutScopeMarker
@Immutable
@ExperimentalAnimatedSpacingApi
interface AnimatedSpacingRowScope : RowScope {

    /** Animates a child's width, optional fade, surrounding spacing, and weighted allocation. */
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
private object AnimatedSpacingRowScopeInstance : AnimatedSpacingRowScope {

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        animatedSpacingColumnWeight(weight, fill)

    override fun Modifier.align(alignment: Alignment.Vertical): Modifier =
        animatedSpacingRowAlign(alignment)

    override fun Modifier.alignBy(alignmentLine: HorizontalAlignmentLine): Modifier =
        animatedSpacingRowAlignBy(AlignmentLineProvider.Value(alignmentLine))

    override fun Modifier.alignByBaseline(): Modifier =
        alignBy(androidx.compose.ui.layout.FirstBaseline)

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier =
        animatedSpacingRowAlignBy(AlignmentLineProvider.Block(alignmentLineBlock))

    @Composable
    override fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier,
        animationSpec: FiniteAnimationSpec<Float>,
        fade: Boolean,
        label: String,
        content: @Composable () -> Unit
    ) {
        AnimatedSpacingRowVisibility(visible, modifier, animationSpec, fade, label, content)
    }
}
