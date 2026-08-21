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
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedSpacingColumn(
    spacing: Dp,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable AnimatedSpacingColumnScope.() -> Unit
) {
    require(spacing >= 0.dp) { "spacing must be non-negative" }

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

        return then(
            WeightElement(
                weight = weight.coerceAtMost(Float.MAX_VALUE),
                fill = fill
            )
        )
    }

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        then(HorizontalAlignmentElement(alignment))

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

        val weight = modifier.weightConfig()

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

                    if (fade) {
                        alpha = presence.value.coerceIn(0f, 1f)
                    }
                },
            measurePolicy = remember(presence, weight?.fill) {
                VisibilityMeasurePolicy(
                    presence = presence,
                    fillWeightedSpace = weight?.fill == true
                )
            }
        )
    }
}

internal data class AnimatedSpacingColumnParentData(
    val weight: Float? = null,
    val fill: Boolean = true,
    val horizontalAlignment: Alignment.Horizontal? = null,
    val presence: State<Float>? = null,
    val visibilityControlled: Boolean = false
)

private fun Any?.animatedSpacingColumnParentData() =
    this as? AnimatedSpacingColumnParentData
        ?: AnimatedSpacingColumnParentData()

private data class WeightConfig(val weight: Float, val fill: Boolean)

private fun Modifier.weightConfig(): WeightConfig? =
    foldIn(null) { current, element ->
        if (element is WeightElement) {
            WeightConfig(
                weight = element.weight,
                fill = element.fill
            )
        } else {
            current
        }
    }

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
            .copy(
                presence = presence,
                visibilityControlled = true
            )
}
