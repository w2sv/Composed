package com.w2sv.composed.playground.animatedspacing

internal data class AnimatedSpacingConfiguration(
    val orientation: AnimatedSpacingOrientation = AnimatedSpacingOrientation.Column,
    val spacingDp: Int = 24,
    val durationMillis: Int = 500,
    val animation: AnimatedSpacingAnimation = AnimatedSpacingAnimation.Tween,
    val expandFrom: AnimatedSpacingAnchor = AnimatedSpacingAnchor.Start,
    val shrinkTowards: AnimatedSpacingAnchor = AnimatedSpacingAnchor.Start,
    val fade: Boolean = true,
    val weighted: Boolean = false
)

internal data class AnimatedSpacingVisibility(val items: List<Boolean> = List(ITEM_COUNT) { true }) {
    val allVisible: Boolean get() = items.all { it }

    fun toggle(index: Int) =
        copy(items = items.mapIndexed { itemIndex, visible -> if (itemIndex == index) !visible else visible })

    companion object {
        const val ITEM_COUNT = 4

        fun none() =
            AnimatedSpacingVisibility(List(ITEM_COUNT) { false })
    }
}

internal enum class AnimatedSpacingOrientation(val label: String) {
    Column("Column"),
    Row("Row")
}

internal enum class AnimatedSpacingAnimation(val label: String) {
    Tween("Tween"),
    Spring("Spring")
}

internal enum class AnimatedSpacingAnchor(val label: String) {
    Start("Start"),
    Center("Center"),
    End("End")
}
