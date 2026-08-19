package com.w2sv.composed.playground

enum class Sample(val id: String, val title: String, val description: String) {
    Shake(
        id = "shake",
        title = "Shake animation",
        description = "Configure and trigger a horizontal shake animation."
    ),
    LazyGridItemEntrance(
        id = "lazy-grid-item-entrance",
        title = "Lazy grid entrances",
        description = "Explore entrance delays across vertical and horizontal lazy grids."
    );

    companion object {
        fun fromId(id: String): Sample? =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}
