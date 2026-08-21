package com.w2sv.composed.playground

enum class Sample(val id: String, val title: String) {
    AnimatedSpacing(
        id = "animated-spacing",
        title = "Animated Spacing"
    ),
    Shake(
        id = "shake",
        title = "Shake animation"
    ),
    LazyGridItemEntrance(
        id = "lazy-grid-item-entrance",
        title = "Lazy grid entrances"
    );

    companion object {
        fun fromId(id: String): Sample? =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}
