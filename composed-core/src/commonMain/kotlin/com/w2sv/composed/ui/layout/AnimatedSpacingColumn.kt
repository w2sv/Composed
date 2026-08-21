package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

@Composable
fun AnimatedSpacingColumn(
    spacing: Dp,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable AnimatedSpacingColumnScope.() -> Unit
) {
    require(spacing >= Dp.Hairline) { "spacing must be non-negative" }

    val measurePolicy = remember(spacing, horizontalAlignment) {
        AnimatedSpacingColumnMeasurePolicy(
            spacing = spacing,
            horizontalAlignment = horizontalAlignment
        )
    }

    Layout(
        content = { AnimatedSpacingColumnScopeInstance.content() },
        modifier = modifier,
        measurePolicy = measurePolicy
    )
}

@LayoutScopeMarker
@Stable
interface AnimatedSpacingColumnScope {

    @Stable
    fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier

    @Stable
    fun Modifier.align(alignment: Alignment.Horizontal): Modifier

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

private object AnimatedSpacingColumnScopeInstance : AnimatedSpacingColumnScope {

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        require(weight > 0f) { "weight must be greater than zero" }
        return this then (
            WeightElement(
                weight = weight.coerceAtMost(Float.MAX_VALUE),
                fill = fill
            )
            )
    }

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        this then (HorizontalAlignmentElement(alignment))

    @Composable
    override fun AnimatedVisibility(
        visible: Boolean,
        modifier: Modifier,
        animationSpec: FiniteAnimationSpec<Float>,
        fade: Boolean,
        label: String,
        content: @Composable () -> Unit
    ) {
        val transition = updateTransition(
            targetState = visible,
            label = label
        )

        val presence = transition.animateFloat(
            transitionSpec = { animationSpec },
            label = "$label:presence"
        ) {
            if (it) 1f else 0f
        }

        Layout(
            content = {
                if (transition.currentState || transition.targetState) {
                    content()
                }
            },
            modifier = modifier
                .then(PresenceElement(presence))
                .graphicsLayer {
                    clip = true
                    alpha = if (fade) {
                        presence.value.coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                },
            measurePolicy = remember(presence) { VisibilityMeasurePolicy(presence) }
        )
    }
}

private class VisibilityMeasurePolicy(private val presence: State<Float>) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val childConstraints = constraints.copy(minHeight = 0)

        val placeables = Array<Placeable?>(measurables.size) { index ->
            measurables[index].measure(childConstraints)
        }

        var width = 0
        var fullHeight = 0

        placeables.forEach {
            if (it != null) {
                width = maxOf(width, it.width)
                fullHeight = maxOf(fullHeight, it.height)
            }
        }

        val animatedHeight = (fullHeight * presence.value.coerceIn(0f, 1f)).roundToInt()

        return layout(
            width = constraints.constrainWidth(width),
            height = constraints.constrainHeight(animatedHeight)
        ) {
            placeables.forEach {
                it?.placeRelative(0, 0)
            }
        }
    }
}

private class AnimatedSpacingColumnMeasurePolicy(private val spacing: Dp, private val horizontalAlignment: Alignment.Horizontal) :
    MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val count = measurables.size

        if (count == 0) {
            return layout(
                constraints.minWidth,
                constraints.minHeight
            ) {}
        }

        val parentData = Array(count) { index ->
            measurables[index].parentData as? AnimatedSpacingColumnParentData
        }

        parentData.forEach {
            require(it?.weight == null || it.presence == null) {
                "weight is not yet supported on AnimatedVisibility"
            }
        }

        val spacingPx = spacing.roundToPx()

        val spacings = calculateSpacings(
            count = count,
            spacing = spacingPx
        ) { index ->
            parentData[index]
                ?.presence
                ?.value
                ?.coerceIn(0f, 1f)
                ?: 1f
        }

        val totalSpacing = spacings.sum()
        val placeables = arrayOfNulls<Placeable>(count)

        var fixedHeight = 0
        var totalWeight = 0f

        parentData.forEach {
            totalWeight += it?.weight ?: 0f
        }

        val boundedHeight = constraints.maxHeight != Constraints.Infinity

        measurables.forEachIndexed { index, measurable ->
            val data = parentData[index]

            if (data?.weight == null || !boundedHeight) {
                val maxHeight = if (boundedHeight) {
                    (
                        constraints.maxHeight -
                            totalSpacing -
                            fixedHeight
                        ).coerceAtLeast(0)
                } else {
                    Constraints.Infinity
                }

                val placeable = measurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                        minHeight = 0,
                        maxHeight = maxHeight
                    )
                )

                placeables[index] = placeable
                fixedHeight += placeable.height
            }
        }

        if (boundedHeight && totalWeight > 0f) {
            var remainingHeight = (
                constraints.maxHeight -
                    totalSpacing -
                    fixedHeight
                ).coerceAtLeast(0)

            var remainingWeight = totalWeight

            measurables.forEachIndexed { index, measurable ->
                val data = parentData[index]
                val weight = data?.weight ?: return@forEachIndexed

                val allocatedHeight = if (weight == remainingWeight) {
                    remainingHeight
                } else {
                    (
                        remainingHeight *
                            weight /
                            remainingWeight
                        ).roundToInt()
                }

                remainingHeight -= allocatedHeight
                remainingWeight -= weight

                placeables[index] = measurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                        minHeight = if (data.fill) allocatedHeight else 0,
                        maxHeight = allocatedHeight
                    )
                )
            }
        }

        var contentWidth = 0
        var contentHeight = totalSpacing

        placeables.forEach {
            if (it != null) {
                contentWidth = maxOf(contentWidth, it.width)
                contentHeight += it.height
            }
        }

        val width = constraints.constrainWidth(contentWidth)
        val height = constraints.constrainHeight(contentHeight)

        return layout(width, height) {
            var y = 0

            placeables.forEachIndexed { index, placeable ->
                placeable ?: return@forEachIndexed

                y += spacings[index]

                val alignment =
                    parentData[index]?.horizontalAlignment
                        ?: horizontalAlignment

                val x = alignment.align(
                    size = placeable.width,
                    space = width,
                    layoutDirection = layoutDirection
                )

                placeable.placeRelative(x, y)
                y += placeable.height
            }
        }
    }
}

private fun calculateSpacings(
    count: Int,
    spacing: Int,
    presence: (Int) -> Float
): IntArray {
    val result = IntArray(count)

    var accumulatedPresence = 0f
    var previousGapCount = 0f

    repeat(count) { index ->
        accumulatedPresence += presence(index)

        val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
        result[index] = (spacing * (gapCount - previousGapCount)).roundToInt()
        previousGapCount = gapCount
    }

    return result
}

private data class AnimatedSpacingColumnParentData(
    val weight: Float? = null,
    val fill: Boolean = true,
    val horizontalAlignment: Alignment.Horizontal? = null,
    val presence: State<Float>? = null
)

private fun Any?.animatedSpacingColumnParentData() =
    this as? AnimatedSpacingColumnParentData
        ?: AnimatedSpacingColumnParentData()

private data class WeightElement(val weight: Float, val fill: Boolean) : ModifierNodeElement<WeightNode>() {

    override fun create() =
        WeightNode(weight, fill)

    override fun update(node: WeightNode) {
        node.weight = weight
        node.fill = fill
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "weight"
        properties["weight"] = weight
        properties["fill"] = fill
    }
}

private class WeightNode(var weight: Float, var fill: Boolean) :
    Modifier.Node(),
    ParentDataModifierNode {

    override fun Density.modifyParentData(parentData: Any?): Any =
        parentData
            .animatedSpacingColumnParentData()
            .copy(
                weight = weight,
                fill = fill
            )
}

private data class HorizontalAlignmentElement(val alignment: Alignment.Horizontal) : ModifierNodeElement<HorizontalAlignmentNode>() {

    override fun create() =
        HorizontalAlignmentNode(alignment)

    override fun update(node: HorizontalAlignmentNode) {
        node.alignment = alignment
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "align"
        value = alignment
    }
}

private class HorizontalAlignmentNode(var alignment: Alignment.Horizontal) :
    Modifier.Node(),
    ParentDataModifierNode {

    override fun Density.modifyParentData(parentData: Any?): Any =
        parentData
            .animatedSpacingColumnParentData()
            .copy(horizontalAlignment = alignment)
}

private data class PresenceElement(val presence: State<Float>) : ModifierNodeElement<PresenceNode>() {

    override fun create() =
        PresenceNode(presence)

    override fun update(node: PresenceNode) {
        node.presence = presence
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "animatedSpacingPresence"
    }
}

private class PresenceNode(var presence: State<Float>) :
    Modifier.Node(),
    ParentDataModifierNode {

    override fun Density.modifyParentData(parentData: Any?): Any =
        parentData
            .animatedSpacingColumnParentData()
            .copy(presence = presence)
}
