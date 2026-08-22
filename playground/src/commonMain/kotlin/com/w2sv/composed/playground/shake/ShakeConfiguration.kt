package com.w2sv.composed.playground.shake

internal data class ShakeConfiguration(
    val amplitudeDp: Float = 20f,
    val durationMillis: Int = 400,
    val frequencyHz: Float = 8f,
    val decay: Float = 0.5f
) {
    val cycleCount: Float
        get() = durationMillis / 1_000f * frequencyHz
}
