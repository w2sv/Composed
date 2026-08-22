package com.w2sv.composed.ui.layout

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
        measurePolicy = remember(presence, fillWeightedSpace) {
            VisibilityMeasurePolicy(presence, fillWeightedSpace == true)
        }
    )
}

internal class VisibilityMeasurePolicy(private val presence: State<Float>, private val fillWeightedSpace: Boolean) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val minHeight = if (fillWeightedSpace && constraints.maxHeight != Constraints.Infinity) constraints.maxHeight else 0
        val childConstraints = constraints.copy(minHeight = minHeight)
        val placeables = Array(measurables.size) { index -> measurables[index].measure(childConstraints) }
        val contentSize = placeables.maxContentSize()
        val animatedHeight = (contentSize.height * presence.value.coerceIn(0f, 1f)).roundToInt()

        return layout(
            width = constraints.constrainWidth(contentSize.width),
            height = constraints.constrainHeight(animatedHeight)
        ) {
            placeables.forEach { it.placeRelative(0, 0) }
        }
    }
}

private data class ContentSize(val width: Int, val height: Int)

private fun Array<Placeable>.maxContentSize(): ContentSize {
    var width = 0
    var height = 0

    forEach { placeable ->
        width = maxOf(width, placeable.width)
        height = maxOf(height, placeable.height)
    }

    return ContentSize(width, height)
}
