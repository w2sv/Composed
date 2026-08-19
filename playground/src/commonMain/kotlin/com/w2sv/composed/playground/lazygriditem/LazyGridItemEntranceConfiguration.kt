package com.w2sv.composed.playground.lazygriditem

import com.w2sv.composed.animation.LazyGridItemEntranceDelay
import com.w2sv.composed.animation.LazyGridItemEntranceRepeatMode
import kotlin.time.Duration.Companion.milliseconds

internal data class LazyGridItemEntranceConfiguration(
    val orientation: GridOrientation = GridOrientation.Vertical,
    val repeatMode: LazyGridItemEntranceRepeatMode = LazyGridItemEntranceRepeatMode.OncePerKey,
    val delayMode: EntranceDelayMode = EntranceDelayMode.Diagonal,
    val mainAxisIntervalMillis: Int = 200,
    val crossAxisIntervalMillis: Int = 150,
    val sequentialIntervalMillis: Int = 50,
    val durationMillis: Int = 500,
    val initialScale: Float = 0.8f,
    val initialAlpha: Float = 0f,
    val crossAxisCount: Int = 4
) {
    val delay: LazyGridItemEntranceDelay
        get() = when (delayMode) {
            EntranceDelayMode.None -> LazyGridItemEntranceDelay.None
            EntranceDelayMode.MainAxis -> LazyGridItemEntranceDelay.alongMainAxis(mainAxisIntervalMillis.milliseconds)
            EntranceDelayMode.CrossAxis -> LazyGridItemEntranceDelay.alongCrossAxis(crossAxisIntervalMillis.milliseconds)
            EntranceDelayMode.Sequential -> LazyGridItemEntranceDelay.sequential(sequentialIntervalMillis.milliseconds)
            EntranceDelayMode.Diagonal -> LazyGridItemEntranceDelay.diagonal(
                mainAxisInterval = mainAxisIntervalMillis.milliseconds,
                crossAxisInterval = crossAxisIntervalMillis.milliseconds
            )
        }
}

internal enum class GridOrientation(val label: String) {
    Vertical("Vertical"),
    Horizontal("Horizontal")
}

internal enum class EntranceDelayMode(val label: String) {
    None("None"),
    MainAxis("Main axis"),
    CrossAxis("Cross axis"),
    Sequential("Sequential"),
    Diagonal("Diagonal")
}
