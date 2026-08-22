package com.w2sv.composed.ui.layout

import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density

internal data class AnimatedSpacingColumnParentData(
    val weight: Float? = null,
    val fill: Boolean = true,
    val crossAxisAlignment: CrossAxisAlignment? = null,
    val presence: State<Float>? = null,
    val visibilityControlled: Boolean = false
)

internal sealed interface CrossAxisAlignment {
    data class Horizontal(val alignment: Alignment.Horizontal) : CrossAxisAlignment

    data class Vertical(val alignment: Alignment.Vertical) : CrossAxisAlignment

    data class Relative(val provider: AlignmentLineProvider) : CrossAxisAlignment
}

internal sealed interface AlignmentLineProvider {
    fun position(placeable: Placeable): Int

    data class Value(val alignmentLine: AlignmentLine) : AlignmentLineProvider {
        override fun position(placeable: Placeable): Int =
            placeable[alignmentLine]
    }

    data class Block(val block: (Measured) -> Int) : AlignmentLineProvider {
        override fun position(placeable: Placeable): Int =
            block(placeable)
    }
}

internal fun Modifier.findAnimatedSpacingColumnWeightFill(): Boolean? =
    foldIn(null) { current, element ->
        if (element is WeightElement) element.fill else current
    }

internal fun Modifier.animatedSpacingColumnWeight(weight: Float, fill: Boolean): Modifier {
    require(weight > 0f) { "weight must be greater than zero" }
    return then(WeightElement(weight.coerceAtMost(Float.MAX_VALUE), fill))
}

internal fun Modifier.animatedSpacingColumnAlign(alignment: Alignment.Horizontal): Modifier =
    then(HorizontalAlignmentElement(alignment))

internal fun Modifier.animatedSpacingRowAlign(alignment: Alignment.Vertical): Modifier =
    then(VerticalAlignmentElement(alignment))

internal fun Modifier.animatedSpacingColumnAlignBy(provider: AlignmentLineProvider): Modifier =
    then(AlignmentLineElement(provider))

internal fun Modifier.animatedSpacingRowAlignBy(provider: AlignmentLineProvider): Modifier =
    then(AlignmentLineElement(provider))

internal fun Modifier.animatedSpacingColumnPresence(presence: State<Float>): Modifier =
    then(PresenceElement(presence))

private fun Any?.animatedSpacingColumnParentData(): AnimatedSpacingColumnParentData =
    this as? AnimatedSpacingColumnParentData ?: AnimatedSpacingColumnParentData()

private data class WeightElement(val weight: Float, val fill: Boolean) : ModifierNodeElement<WeightNode>() {
    override fun create(): WeightNode =
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
        parentData.animatedSpacingColumnParentData().copy(weight = weight, fill = fill)
}

private data class HorizontalAlignmentElement(val alignment: Alignment.Horizontal) : ModifierNodeElement<HorizontalAlignmentNode>() {

    override fun create(): HorizontalAlignmentNode =
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
        parentData.animatedSpacingColumnParentData().copy(
            crossAxisAlignment = CrossAxisAlignment.Horizontal(alignment)
        )
}

private data class VerticalAlignmentElement(val alignment: Alignment.Vertical) : ModifierNodeElement<VerticalAlignmentNode>() {

    override fun create(): VerticalAlignmentNode =
        VerticalAlignmentNode(alignment)

    override fun update(node: VerticalAlignmentNode) {
        node.alignment = alignment
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "align"
        value = alignment
    }
}

private class VerticalAlignmentNode(var alignment: Alignment.Vertical) :
    Modifier.Node(),
    ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?): Any =
        parentData.animatedSpacingColumnParentData().copy(
            crossAxisAlignment = CrossAxisAlignment.Vertical(alignment)
        )
}

private data class AlignmentLineElement(val provider: AlignmentLineProvider) : ModifierNodeElement<AlignmentLineNode>() {

    override fun create(): AlignmentLineNode =
        AlignmentLineNode(provider)

    override fun update(node: AlignmentLineNode) {
        node.provider = provider
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "alignBy"
        value = provider
    }
}

private class AlignmentLineNode(var provider: AlignmentLineProvider) :
    Modifier.Node(),
    ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?): Any =
        parentData.animatedSpacingColumnParentData().copy(
            crossAxisAlignment = CrossAxisAlignment.Relative(provider)
        )
}

private data class PresenceElement(val presence: State<Float>) : ModifierNodeElement<PresenceNode>() {
    override fun create(): PresenceNode =
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
        parentData.animatedSpacingColumnParentData().copy(
            presence = presence,
            visibilityControlled = true
        )
}
