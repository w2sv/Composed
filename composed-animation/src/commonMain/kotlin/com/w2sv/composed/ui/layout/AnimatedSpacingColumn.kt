package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
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
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingWeight

/**
 * Places children vertically and animates both a child's occupied height and its surrounding spacing when the child
 * is emitted by [AnimatedSpacingColumnScope.AnimatedVisibility].
 *
 * Unlike [androidx.compose.foundation.layout.Column], spacing is derived from the visibility progress of adjacent
 * children. This keeps the gap on both sides of an entering or leaving child visually symmetric. The animation is
 * driven by the same progress that collapses the child. Child-specific horizontal alignment, alignment lines, and
 * [ColumnScope.weight] retain their usual scope semantics. When a weighted animated child disappears, its released
 * allocation is progressively redistributed among the other visible weighted children.
 *
 * This layout is eager and is not a replacement for a lazy list. It supports one fixed, non-negative [spacing] value
 * rather than the complete set of stock `Arrangement.Vertical` strategies. Only children wrapped in
 * [AnimatedSpacingColumnScope.AnimatedVisibility] participate in animated spacing, and the visibility wrapper overlays
 * multiple direct content children at the same origin. Intrinsic measurement is not specialized. Prefer the stock
 * [androidx.compose.foundation.layout.Column] when its spacing does not need to participate in visibility transitions.
 *
 * @param spacing fixed distance between fully visible adjacent children. It must not be negative.
 * @param modifier modifier applied to the layout.
 * @param horizontalAlignment default horizontal alignment for children that do not provide a scope-specific alignment.
 * @param animation default visibility-animation configuration inherited by animated children.
 * @param content children placed by the column. Use [AnimatedSpacingColumnScope.AnimatedVisibility] for children whose
 * occupied height and adjacent spacing should animate.
 */
@Composable
@ExperimentalAnimatedSpacingApi
fun AnimatedSpacingColumn(
    spacing: Dp,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    animation: AnimatedSpacingColumnAnimation = AnimatedSpacingColumnAnimation(),
    content: @Composable AnimatedSpacingColumnScope.() -> Unit
) {
    require(spacing >= 0.dp) { "spacing must be non-negative" }

    val measurePolicy = remember(spacing, horizontalAlignment) {
        AnimatedSpacingColumnMeasurePolicy(spacing, horizontalAlignment)
    }
    val scope = remember(animation) { AnimatedSpacingColumnScopeInstance(animation) }

    Layout(
        content = { scope.content() },
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}

/**
 * Visibility-animation configuration for [AnimatedSpacingColumn]. A child inherits its container's instance unless it
 * supplies this type to [AnimatedSpacingColumnScope.AnimatedVisibility].
 *
 * [fade] is a temporary API for the only additional visual effect currently supported. It is expected to be replaced
 * by Compose-native enter/exit transitions once the project can depend on transition configuration that allows
 * size-changing expand and shrink transitions to be rejected safely.
 *
 * @param animationSpec animation used for the presence progress that drives structural sizing, spacing, and [fade].
 * @param fade whether to apply that progress as alpha in addition to structural clipping.
 * @param expandFrom alignment from which content is structurally revealed while entering.
 * @param shrinkTowards alignment towards which content structurally collapses while exiting.
 */
@ExperimentalAnimatedSpacingApi
data class AnimatedSpacingColumnAnimation(
    val animationSpec: FiniteAnimationSpec<Float> = spring(),
    val fade: Boolean = true,
    val expandFrom: Alignment.Vertical = Alignment.Top,
    val shrinkTowards: Alignment.Vertical = Alignment.Top
)

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
     * @param animation optional cohesive override. `null` inherits the containing column's animation configuration.
     * @param label label used for Compose animation tooling.
     * @param content content shown while the transition's current or target state is visible.
     */
    @Composable
    fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier = Modifier,
        animation: AnimatedSpacingColumnAnimation? = null,
        label: String = "AnimatedVisibility",
        content: @Composable () -> Unit
    )

    /**
     * State-driven counterpart to [AnimatedVisibility] whose transition remains active until the content's structural
     * presence animation has completed.
     *
     * @param visibleState observable current and target visibility state. Its `isIdle` becomes true only after size,
     * spacing, and optional fade have reached the target.
     * @param modifier modifier applied to the visibility wrapper. Column scope modifiers such as `weight`, `align`, and
     * `alignBy` are supported.
     * @param animation optional cohesive override. `null` inherits the containing column's animation configuration.
     * @param label label used for Compose animation tooling.
     * @param content content shown while the transition's current or target state is visible.
     */
    @Composable
    fun AnimatedVisibility(
        visibleState: MutableTransitionState<Boolean>,
        modifier: Modifier = Modifier,
        animation: AnimatedSpacingColumnAnimation? = null,
        label: String = "AnimatedVisibility",
        content: @Composable () -> Unit
    )
}

@OptIn(ExperimentalAnimatedSpacingApi::class)
private class AnimatedSpacingColumnScopeInstance(private val inheritedAnimation: AnimatedSpacingColumnAnimation) :
    AnimatedSpacingColumnScope {

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        animatedSpacingWeight(weight, fill)

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
        animation: AnimatedSpacingColumnAnimation?,
        label: String,
        content: @Composable () -> Unit
    ) {
        val transition = updateTransition(targetState = visible, label = label)
        AnimatedVisibility(transition, modifier, animation, label, content)
    }

    @Composable
    override fun AnimatedVisibility(
        visibleState: MutableTransitionState<Boolean>,
        modifier: Modifier,
        animation: AnimatedSpacingColumnAnimation?,
        label: String,
        content: @Composable () -> Unit
    ) {
        val transition = rememberTransition(transitionState = visibleState, label = label)
        AnimatedVisibility(transition, modifier, animation, label, content)
    }

    @Composable
    private fun AnimatedVisibility(
        transition: androidx.compose.animation.core.Transition<Boolean>,
        modifier: Modifier,
        animation: AnimatedSpacingColumnAnimation?,
        label: String,
        content: @Composable () -> Unit
    ) {
        val resolvedAnimation = animation ?: inheritedAnimation
        AnimatedSpacingColumnVisibility(
            transition = transition,
            modifier = modifier,
            expandFrom = resolvedAnimation.expandFrom,
            shrinkTowards = resolvedAnimation.shrinkTowards,
            animationSpec = resolvedAnimation.animationSpec,
            fade = resolvedAnimation.fade,
            label = label,
            content = content
        )
    }
}
