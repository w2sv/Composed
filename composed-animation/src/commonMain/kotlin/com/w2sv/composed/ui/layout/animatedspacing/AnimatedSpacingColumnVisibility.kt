package com.w2sv.composed.ui.layout.animatedspacing

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

@Composable
internal fun AnimatedSpacingColumnVisibility(
    visible: Boolean,
    modifier: Modifier,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    content: @Composable () -> Unit
) {
    AnimatedSpacingVisibility(visible, modifier, animationSpec, fade, label, VisibilityAxis.Vertical, content)
}

@Composable
internal fun AnimatedSpacingRowVisibility(
    visible: Boolean,
    modifier: Modifier,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    content: @Composable () -> Unit
) {
    AnimatedSpacingVisibility(visible, modifier, animationSpec, fade, label, VisibilityAxis.Horizontal, content)
}

@Composable
private fun AnimatedSpacingVisibility(
    visible: Boolean,
    modifier: Modifier,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    axis: VisibilityAxis,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = visible, label = label)
    val presence = transition.animateFloat(
        transitionSpec = { animationSpec },
        label = "$label:presence"
    ) { isVisible ->
        if (isVisible) 1f else 0f
    }
    val fillWeightedSpace = modifier.findAnimatedSpacingColumnWeightFill()

    Layout(
        content = { if (transition.currentState || transition.targetState) content() },
        modifier = modifier
            .animatedSpacingColumnPresence(presence)
            .graphicsLayer {
                clip = true
                if (fade) alpha = presence.value.coerceIn(0f, 1f)
            },
        measurePolicy = remember(presence, fillWeightedSpace, axis) {
            VisibilityMeasurePolicy(presence, fillWeightedSpace == true, axis)
        }
    )
}

internal enum class VisibilityAxis { Horizontal, Vertical }

internal class VisibilityMeasurePolicy(
    private val presence: State<Float>,
    private val fillWeightedSpace: Boolean,
    private val axis: VisibilityAxis = VisibilityAxis.Vertical
) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val childConstraints = when (axis) {
            VisibilityAxis.Horizontal -> constraints.copy(
                minWidth = if (fillWeightedSpace && constraints.maxWidth != Constraints.Infinity) constraints.maxWidth else 0
            )

            VisibilityAxis.Vertical -> constraints.copy(
                minHeight = if (fillWeightedSpace && constraints.maxHeight != Constraints.Infinity) constraints.maxHeight else 0
            )
        }
        val placeables = Array(measurables.size) { index -> measurables[index].measure(childConstraints) }
        val contentSize = placeables.maxContentSize()
        val progress = presence.value.coerceIn(0f, 1f)
        val animatedWidth = if (axis == VisibilityAxis.Horizontal) (contentSize.width * progress).roundToInt() else contentSize.width
        val animatedHeight = if (axis == VisibilityAxis.Vertical) (contentSize.height * progress).roundToInt() else contentSize.height

        return layout(
            width = constraints.constrainWidth(animatedWidth),
            height = constraints.constrainHeight(animatedHeight)
        ) {
            placeables.forEach { it.placeRelative(0, 0) }
        }
    }
}

private fun Array<Placeable>.maxContentSize(): IntSize {
    var width = 0
    var height = 0

    forEach { placeable ->
        width = maxOf(width, placeable.width)
        height = maxOf(height, placeable.height)
    }

    return IntSize(width, height)
}
