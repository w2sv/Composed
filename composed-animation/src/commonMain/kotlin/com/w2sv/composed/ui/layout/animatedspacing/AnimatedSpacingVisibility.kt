package com.w2sv.composed.ui.layout.animatedspacing

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

@Composable
internal fun AnimatedSpacingColumnVisibility(
    visible: Boolean,
    modifier: Modifier,
    expandFrom: Alignment.Vertical,
    shrinkTowards: Alignment.Vertical,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    content: @Composable () -> Unit
) {
    val structuralAlignment = remember(expandFrom, shrinkTowards) {
        StructuralAlignment.Vertical(expandFrom, shrinkTowards)
    }
    AnimatedSpacingVisibility(
        visible = visible,
        modifier = modifier,
        animationSpec = animationSpec,
        fade = fade,
        label = label,
        structuralAlignment = structuralAlignment,
        content = content
    )
}

@Composable
internal fun AnimatedSpacingRowVisibility(
    visible: Boolean,
    modifier: Modifier,
    expandFrom: Alignment.Horizontal,
    shrinkTowards: Alignment.Horizontal,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    content: @Composable () -> Unit
) {
    val structuralAlignment = remember(expandFrom, shrinkTowards) {
        StructuralAlignment.Horizontal(expandFrom, shrinkTowards)
    }
    AnimatedSpacingVisibility(
        visible = visible,
        modifier = modifier,
        animationSpec = animationSpec,
        fade = fade,
        label = label,
        structuralAlignment = structuralAlignment,
        content = content
    )
}

@Composable
private fun AnimatedSpacingVisibility(
    visible: Boolean,
    modifier: Modifier,
    animationSpec: FiniteAnimationSpec<Float>,
    fade: Boolean,
    label: String,
    structuralAlignment: StructuralAlignment,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = visible, label = label)
    val presence = transition.animateFloat(
        transitionSpec = { animationSpec },
        label = "$label:presence"
    ) { isVisible ->
        if (isVisible) 1f else 0f
    }
    val fillWeightedSpace = modifier.findAnimatedSpacingWeightFill()

    Layout(
        content = { if (transition.currentState || transition.targetState) content() },
        modifier = modifier
            .animatedSpacingPresence(presence)
            .graphicsLayer {
                clip = true
                if (fade) alpha = presence.value.coerceIn(0f, 1f)
            },
        measurePolicy = remember(presence, fillWeightedSpace, structuralAlignment, transition.targetState) {
            VisibilityMeasurePolicy(
                presence = presence,
                fillWeightedSpace = fillWeightedSpace == true,
                structuralAlignment = structuralAlignment,
                expanding = transition.targetState
            )
        }
    )
}

internal enum class VisibilityAxis { Horizontal, Vertical }

internal sealed interface StructuralAlignment {
    val axis: VisibilityAxis

    fun mainAxisOffset(
        contentSize: Int,
        animatedSize: Int,
        layoutDirection: LayoutDirection,
        expanding: Boolean
    ): Int

    data class Vertical(val expandFrom: Alignment.Vertical, val shrinkTowards: Alignment.Vertical) : StructuralAlignment {
        override val axis = VisibilityAxis.Vertical

        override fun mainAxisOffset(
            contentSize: Int,
            animatedSize: Int,
            layoutDirection: LayoutDirection,
            expanding: Boolean
        ): Int =
            (if (expanding) expandFrom else shrinkTowards).align(contentSize, animatedSize)
    }

    data class Horizontal(val expandFrom: Alignment.Horizontal, val shrinkTowards: Alignment.Horizontal) : StructuralAlignment {
        override val axis = VisibilityAxis.Horizontal

        override fun mainAxisOffset(
            contentSize: Int,
            animatedSize: Int,
            layoutDirection: LayoutDirection,
            expanding: Boolean
        ): Int =
            (if (expanding) expandFrom else shrinkTowards).align(contentSize, animatedSize, layoutDirection)
    }
}

internal class VisibilityMeasurePolicy(
    private val presence: State<Float>,
    private val fillWeightedSpace: Boolean,
    private val structuralAlignment: StructuralAlignment = StructuralAlignment.Vertical(Alignment.Top, Alignment.Top),
    private val expanding: Boolean = true
) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val childConstraints = when (structuralAlignment.axis) {
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
        val animatedWidth = if (structuralAlignment.axis == VisibilityAxis.Horizontal) {
            (contentSize.width * progress).roundToInt()
        } else {
            contentSize.width
        }
        val animatedHeight = if (structuralAlignment.axis == VisibilityAxis.Vertical) {
            (contentSize.height * progress).roundToInt()
        } else {
            contentSize.height
        }
        val width = constraints.constrainWidth(animatedWidth)
        val height = constraints.constrainHeight(animatedHeight)
        val mainAxisOffset = structuralAlignment.mainAxisOffset(
            contentSize = if (structuralAlignment.axis == VisibilityAxis.Horizontal) contentSize.width else contentSize.height,
            animatedSize = if (structuralAlignment.axis == VisibilityAxis.Horizontal) width else height,
            layoutDirection = layoutDirection,
            expanding = expanding
        )

        return layout(width, height) {
            placeables.forEach {
                if (structuralAlignment.axis == VisibilityAxis.Horizontal) {
                    it.place(mainAxisOffset, 0)
                } else {
                    it.place(0, mainAxisOffset)
                }
            }
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
