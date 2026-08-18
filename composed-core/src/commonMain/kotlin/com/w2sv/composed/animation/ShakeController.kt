package com.w2sv.composed.animation

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframesWithSpline
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Controls a horizontal shake animation applied through [Modifier.shakenBy].
 *
 * @param amplitude Maximum horizontal displacement of the shake.
 * @param durationMillis Total duration of the shake animation in milliseconds.
 * @param frequencyHz Desired number of complete oscillations per second.
 * @param decay Amount by which the amplitude decreases between successive
 * shake cycles. `0f` disables decay, while `1f` applies maximum decay.
 */
@Stable
class ShakeController(
    internal val amplitude: Dp = 20.dp,
    @IntRange(0L) private val durationMillis: Int = 400,
    @FloatRange(0.0) private val frequencyHz: Float = 8f,
    @FloatRange(0.0, 1.0) private val decay: Float = 0.7f
) {
    init {
        require(durationMillis > 0) { "durationMillis must be greater than 0" }
        require(frequencyHz > 0f) { "frequencyHz must be greater than 0" }
        require(decay in 0f..1f) { "decay must be between 0 and 1" }
    }

    private val animatable = Animatable(0f)

    /**
     * Current normalized horizontal translation.
     */
    internal val translationFraction: Float
        get() = animatable.value

    /**
     * Whether the shake animation is currently running.
     */
    val isShaking: Boolean
        get() = animatable.isRunning

    /**
     * Runs the shake animation.
     */
    suspend fun shake() {
        val cycles = max(
            1,
            (durationMillis / 1_000f * frequencyHz).roundToInt()
        )
        val retention = 1f - decay
        val peakCount = cycles * 2

        animatable.animateTo(
            targetValue = 0f,
            initialVelocity = 0f,
            animationSpec = keyframesWithSpline {
                durationMillis = this@ShakeController.durationMillis

                repeat(peakCount) { peakIndex ->
                    val cycleIndex = peakIndex / 2
                    val direction = if (peakIndex % 2 == 0) 1f else -1f
                    val peakAmplitude = retention.pow(cycleIndex)
                    val value = direction * peakAmplitude

                    val fraction = (2f * peakIndex + 1f) / (4f * cycles)

                    value atFraction fraction
                }
            }
        )
    }

    /**
     * Cancels an ongoing shake and resets its translation.
     *
     * When [animated] is `true`, the translation animates back to its
     * resting position.
     */
    suspend fun cancel(animated: Boolean = false) {
        if (animated) {
            animatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = durationMillis / 4
                )
            )
        } else {
            animatable.snapTo(0f)
        }
    }
}

/**
 * Creates and remembers a [ShakeController].
 *
 * @param amplitude Maximum horizontal displacement of the shake.
 * @param durationMillis Total duration of the shake animation in milliseconds.
 * @param frequencyHz Desired number of complete oscillations per second.
 * @param decay Amount by which the amplitude decreases between successive
 * shake cycles. `0f` disables decay, while `1f` applies maximum decay.
 */
@Composable
fun rememberShakeController(
    amplitude: Dp = 20.dp,
    @IntRange(0L) durationMillis: Int = 400,
    @FloatRange(0.0) frequencyHz: Float = 8f,
    @FloatRange(0.0, 1.0) decay: Float = 0.7f
): ShakeController =
    remember(amplitude, durationMillis, frequencyHz, decay) {
        ShakeController(
            amplitude = amplitude,
            durationMillis = durationMillis,
            frequencyHz = frequencyHz,
            decay = decay
        )
    }

/**
 * Applies the horizontal shake translation controlled by [controller].
 */
@Stable
fun Modifier.shakenBy(controller: ShakeController): Modifier =
    graphicsLayer {
        translationX =
            controller.translationFraction * controller.amplitude.toPx()
    }
