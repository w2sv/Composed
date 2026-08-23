package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
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
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingRowAlign
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingRowAlignBy
import com.w2sv.composed.ui.layout.animatedspacing.animatedSpacingWeight

/**
 * Places children horizontally and animates both a child's occupied width and its surrounding spacing when the child
 * is emitted by [AnimatedSpacingRowScope.AnimatedVisibility].
 *
 * Unlike [androidx.compose.foundation.layout.Row], spacing is derived from the visibility progress of adjacent children.
 * This keeps the gap on both sides of an entering or leaving child visually symmetric. The animation is driven by the
 * same progress that collapses the child. Child-specific vertical alignment, baselines, alignment lines, and
 * [RowScope.weight] retain their usual scope semantics. When a weighted animated child disappears, its released
 * allocation is progressively redistributed among the other visible weighted children. Placement follows layout
 * direction, including RTL order.
 *
 * This layout is eager and is not a replacement for a lazy list. It supports one fixed, non-negative [spacing] value
 * rather than the complete set of stock `Arrangement.Horizontal` strategies. Only children wrapped in
 * [AnimatedSpacingRowScope.AnimatedVisibility] participate in animated spacing, and the visibility wrapper overlays
 * multiple direct content children at the same origin. Intrinsic measurement is not specialized. Prefer the stock
 * [androidx.compose.foundation.layout.Row] when its spacing does not need to participate in visibility transitions.
 *
 * @param spacing fixed distance between fully visible adjacent children. It must not be negative.
 * @param modifier modifier applied to the layout.
 * @param verticalAlignment default vertical alignment for children that do not provide a scope-specific alignment.
 * @param animation default visibility-animation configuration inherited by animated children.
 * @param content children placed by the row. Use [AnimatedSpacingRowScope.AnimatedVisibility] for children whose
 * occupied width and adjacent spacing should animate.
 */
@Composable
@ExperimentalAnimatedSpacingApi
fun AnimatedSpacingRow(
    spacing: Dp,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    animation: AnimatedSpacingRowAnimation = AnimatedSpacingRowAnimation(),
    content: @Composable AnimatedSpacingRowScope.() -> Unit
) {
    require(spacing >= 0.dp) { "spacing must be non-negative" }

    val measurePolicy = remember(spacing, verticalAlignment) {
        AnimatedSpacingRowMeasurePolicy(spacing, verticalAlignment)
    }
    val scope = remember(animation) { AnimatedSpacingRowScopeInstance(animation) }

    Layout(
        content = { scope.content() },
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}

/**
 * Visibility-animation configuration for [AnimatedSpacingRow]. A child inherits its container's instance unless it
 * supplies this type to [AnimatedSpacingRowScope.AnimatedVisibility].
 *
 * [fade] is a temporary API for the only additional visual effect currently supported. It is expected to be replaced
 * by Compose-native enter/exit transitions once the project can depend on transition configuration that allows
 * size-changing expand and shrink transitions to be rejected safely.
 *
 * @param animationSpec animation used for the presence progress that drives structural sizing, spacing, and [fade].
 * @param fade whether to apply that progress as alpha in addition to structural clipping.
 * @param expandFrom logical or absolute alignment from which content is structurally revealed while entering.
 * @param shrinkTowards logical or absolute alignment towards which content structurally collapses while exiting.
 */
@ExperimentalAnimatedSpacingApi
data class AnimatedSpacingRowAnimation(
    val animationSpec: FiniteAnimationSpec<Float> = spring(),
    val fade: Boolean = true,
    val expandFrom: Alignment.Horizontal = Alignment.Start,
    val shrinkTowards: Alignment.Horizontal = Alignment.Start
)

/**
 * Receiver scope for [AnimatedSpacingRow]. It provides the standard [RowScope] parent-data modifiers together with
 * visibility whose size, spacing, fade, and weighted allocation share one animation progress.
 */
@LayoutScopeMarker
@Immutable
@ExperimentalAnimatedSpacingApi
interface AnimatedSpacingRowScope : RowScope {

    /**
     * Emits [content] while entering or leaving and animates its occupied width between zero and the measured content
     * width. The parent row uses the same progress to animate spacing symmetrically on both sides.
     *
     * The content is clipped to the animated width and can optionally fade on a graphics layer. If this modifier has
     * [RowScope.weight], the visible share of its allocation follows the animation progress and released space is
     * redistributed among visible weighted siblings. Multiple direct children are measured as an overlay rather than
     * arranged in a row; wrap them in a layout when more than one child is required.
     *
     * @param visible whether the content should occupy its full measured width.
     * @param modifier modifier applied to the visibility wrapper. Row scope modifiers such as `weight`, `align`, and
     * `alignBy` are supported.
     * @param animation optional cohesive override. `null` inherits the containing row's animation configuration.
     * @param label label used for Compose animation tooling.
     * @param content content shown while the transition's current or target state is visible.
     */
    @Composable
    fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier = Modifier,
        animation: AnimatedSpacingRowAnimation? = null,
        label: String = "AnimatedVisibility",
        content: @Composable () -> Unit
    )

    /**
     * State-driven counterpart to [AnimatedVisibility] whose transition remains active until the content's structural
     * presence animation has completed.
     *
     * @param visibleState observable current and target visibility state. Its `isIdle` becomes true only after size,
     * spacing, and optional fade have reached the target.
     * @param modifier modifier applied to the visibility wrapper. Row scope modifiers such as `weight`, `align`, and
     * `alignBy` are supported.
     * @param animation optional cohesive override. `null` inherits the containing row's animation configuration.
     * @param label label used for Compose animation tooling.
     * @param content content shown while the transition's current or target state is visible.
     */
    @Composable
    fun AnimatedVisibility(
        visibleState: MutableTransitionState<Boolean>,
        modifier: Modifier = Modifier,
        animation: AnimatedSpacingRowAnimation? = null,
        label: String = "AnimatedVisibility",
        content: @Composable () -> Unit
    )
}

@OptIn(ExperimentalAnimatedSpacingApi::class)
private class AnimatedSpacingRowScopeInstance(private val inheritedAnimation: AnimatedSpacingRowAnimation) : AnimatedSpacingRowScope {

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        animatedSpacingWeight(weight, fill)

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
        animation: AnimatedSpacingRowAnimation?,
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
        animation: AnimatedSpacingRowAnimation?,
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
        animation: AnimatedSpacingRowAnimation?,
        label: String,
        content: @Composable () -> Unit
    ) {
        val resolvedAnimation = animation ?: inheritedAnimation
        AnimatedSpacingRowVisibility(
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
