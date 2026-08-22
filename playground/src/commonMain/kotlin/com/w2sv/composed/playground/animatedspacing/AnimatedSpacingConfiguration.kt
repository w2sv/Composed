package com.w2sv.composed.playground.animatedspacing

internal data class AnimatedSpacingConfiguration(
    val orientation: AnimatedSpacingOrientation = AnimatedSpacingOrientation.Column,
    val spacingDp: Int = 24,
    val durationMillis: Int = 500,
    val fade: Boolean = true,
    val weighted: Boolean = false
)

internal data class AnimatedSpacingVisibility(val first: Boolean = true, val middle: Boolean = true, val last: Boolean = true) {
    val allVisible: Boolean get() = first && middle && last

    companion object {
        fun none() =
            AnimatedSpacingVisibility(first = false, middle = false, last = false)
    }
}

internal enum class AnimatedSpacingOrientation(val label: String) {
    Column("Column"),
    Row("Row")
}
