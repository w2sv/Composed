package com.w2sv.composed.ui.layout.animatedspacing

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

internal fun measureColumn(
    measurables: List<Measurable>,
    constraints: Constraints,
    spacing: Int = 0,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
): MeasureResult {
    val policy = AnimatedSpacingColumnMeasurePolicy(spacing.dp, horizontalAlignment)
    val scope = TestMeasureScope(layoutDirection)

    return with(policy) { scope.measure(measurables, constraints) }
}

internal fun measureRow(
    measurables: List<Measurable>,
    constraints: Constraints,
    spacing: Int = 0,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
): MeasureResult {
    val policy = AnimatedSpacingRowMeasurePolicy(spacing.dp, verticalAlignment)
    return with(policy) { TestMeasureScope(layoutDirection).measure(measurables, constraints) }
}

internal fun relativeParentData(alignmentLine: VerticalAlignmentLine) =
    AnimatedSpacingColumnParentData(
        crossAxisAlignment = CrossAxisAlignment.Relative(AlignmentLineProvider.Value(alignmentLine))
    )

internal fun relativeParentData(alignmentLine: HorizontalAlignmentLine) =
    AnimatedSpacingColumnParentData(
        crossAxisAlignment = CrossAxisAlignment.Relative(AlignmentLineProvider.Value(alignmentLine))
    )

internal class TestMeasureScope(override val layoutDirection: LayoutDirection) : MeasureScope {
    override val density: Float = 1f
    override val fontScale: Float = 1f
}

internal class TestMeasurable(
    private val preferredWidth: Int,
    private val preferredHeight: Int,
    override val parentData: Any? = null,
    private val alignmentLines: Map<AlignmentLine, Int> = emptyMap(),
    private val size: ((Constraints) -> IntSize)? = null
) : Measurable {

    private var recordedConstraints: Constraints? = null

    val measuredConstraints: Constraints get() = requireNotNull(recordedConstraints)

    lateinit var placeable: TestPlaceable
        private set

    override fun measure(constraints: Constraints): Placeable {
        recordedConstraints = constraints
        val measuredSize = size?.invoke(constraints) ?: IntSize(
            constraints.constrainWidth(preferredWidth),
            constraints.constrainHeight(preferredHeight)
        )

        return TestPlaceable(measuredSize, constraints, alignmentLines).also { placeable = it }
    }

    override fun minIntrinsicWidth(height: Int): Int =
        preferredWidth

    override fun maxIntrinsicWidth(height: Int): Int =
        preferredWidth

    override fun minIntrinsicHeight(width: Int): Int =
        preferredHeight

    override fun maxIntrinsicHeight(width: Int): Int =
        preferredHeight
}

internal class TestPlaceable(measuredSize: IntSize, constraints: Constraints, private val alignmentLines: Map<AlignmentLine, Int>) :
    Placeable() {

    val size: IntSize = measuredSize
    var position: IntOffset? = null
        private set

    init {
        measurementConstraints = constraints
        this.measuredSize = measuredSize
    }

    override fun get(alignmentLine: AlignmentLine): Int =
        alignmentLines[alignmentLine] ?: AlignmentLine.Unspecified

    override fun placeAt(
        position: IntOffset,
        zIndex: Float,
        layerBlock: (GraphicsLayerScope.() -> Unit)?
    ) {
        this.position = position
    }
}

internal val TestAlignmentLine = VerticalAlignmentLine(::minOf)
internal val TestHorizontalAlignmentLine = HorizontalAlignmentLine(::minOf)
