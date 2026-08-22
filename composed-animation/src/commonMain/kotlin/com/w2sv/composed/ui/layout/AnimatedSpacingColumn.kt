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
 * Places children vertically and animates both a child's occupied height and its surrounding spacing when the child
 * is emitted by [AnimatedSpacingColumnScope.AnimatedVisibility].
 *
 * Unlike [androidx.compose.foundation.layout.Column], spacing is derived from the visibility progress of adjacent
 * children. This keeps the gap on both sides of an entering or leaving child visually symmetric. The animation is
 * driven during measurement: it does not use lookahead and does not recompose the layout for each animation frame.
 * Child-specific horizontal alignment, alignment lines, and [ColumnScope.weight] retain their usual scope semantics.
 * When a weighted animated child disappears, its released allocation is progressively redistributed among the other
 * visible weighted children.
 *
 * This layout is eager and is not a replacement for a lazy list. It supports one fixed, non-negative [spacing] value
 * rather than the complete set of stock `Arrangement.Vertical` strategies. Only children wrapped in
 * [AnimatedSpacingColumnScope.AnimatedVisibility] participate in animated spacing, and the visibility wrapper overlays
 * multiple direct content children at the same origin. Intrinsic measurement is not specialized.
 *
 * With no visibility-controlled children, measurement and ordinary weight allocation are O(n), matching the
 * asymptotic cost of a stock [androidx.compose.foundation.layout.Column], though this custom layout still has its own
 * parent-data and measurement overhead. Animated spacing remains O(n). Visibility-controlled weighted children use
 * O(n²) redistribution so each disappearing child's released share can account for every eligible recipient, and
 * fading/clipping adds a graphics layer per animated child.
 *
 * @param spacing fixed distance between fully visible adjacent children. It must not be negative.
 * @param modifier modifier applied to the layout.
 * @param horizontalAlignment default horizontal alignment for children that do not provide a scope-specific alignment.
 * @param content children placed by the column. Use [AnimatedSpacingColumnScope.AnimatedVisibility] for children whose
 * occupied height and adjacent spacing should animate.
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

/**
 * Receiver scope for [AnimatedSpacingColumn]. It provides the standard [ColumnScope] parent-data modifiers together
 * with visibility whose size, spacing, fade, and weighted allocation share one animation progress.
 */
@LayoutScopeMarker
@Immutable
@ExperimentalAnimatedSpacingApi
interface AnimatedSpacingColumnScope : ColumnScope {

    /**
     * Emits [content] while entering or leaving and animates its occupied height between zero and the measured content
     * height. The parent column uses the same progress to animate spacing symmetrically on both sides.
     *
     * The content is clipped to the animated height and can optionally fade on a graphics layer. If this modifier has
     * [ColumnScope.weight], the visible share of its allocation follows the animation progress and released space is
     * redistributed among visible weighted siblings. Multiple direct children are measured as an overlay rather than
     * stacked; wrap them in a layout when more than one child is required.
     *
     * @param visible whether the content should occupy its full measured height.
     * @param modifier modifier applied to the visibility wrapper. Column scope modifiers such as `weight`, `align`, and
     * `alignBy` are supported.
     * @param animationSpec animation used for the shared visibility-progress value.
     * @param fade whether to apply the visibility progress as alpha in addition to clipping the height.
     * @param label label used for Compose animation tooling.
     * @param content content shown while the transition's current or target state is visible.
     */
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
