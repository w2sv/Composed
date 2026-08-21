package com.w2sv.composed.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.lerp
import kotlin.time.Duration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Controls whether a lazy grid item can run its entrance animation more than once. */
enum class LazyGridItemEntranceRepeatMode {
    /** Animates whenever the item's modifier node enters composition. */
    OnComposition,

    /**
     * Animates a key only once for the lifetime of [LazyGridItemEntranceState], unless it is
     * explicitly reset.
     *
     * A key is marked as entered when its animation run starts, before its configured entrance
     * delay. Detaching or cancelling the animation afterward does not allow it to run again.
     */
    OncePerKey
}

/**
 * Retains the keys that have started an entrance animation for a particular lazy grid state.
 *
 * Keys are retained for this state's lifetime until removed with [reset]. This allows
 * [LazyGridItemEntranceRepeatMode.OncePerKey] to survive lazy-layout disposal and recomposition.
 */
@Stable
class LazyGridItemEntranceState internal constructor(internal val gridState: LazyGridState) {
    private val enteredKeys = mutableSetOf<Any>()
    private var resetAsInitialLayout = false

    internal fun hasEntered(key: Any) =
        key in enteredKeys

    internal fun markEntered(key: Any) {
        enteredKeys += key
    }

    internal fun scrollContext(
        detectedScrollContext: LazyGridItemEntranceScrollContext = gridState.currentEntranceScrollContext()
    ): LazyGridItemEntranceScrollContext =
        if (resetAsInitialLayout) {
            LazyGridItemEntranceScrollContext.InitialLayout
        } else {
            detectedScrollContext
        }

    internal fun onScrollStarted() {
        resetAsInitialLayout = false
    }

    /**
     * Allows [key] to animate again the next time it enters composition.
     *
     * This does not restart or otherwise affect a currently attached animation.
     *
     * @param key The item key to remove from this state's entered-key history.
     */
    fun reset(key: Any) {
        enteredKeys -= key
    }

    /**
     * Allows all keys to animate again the next time they enter composition.
     *
     * This does not restart or otherwise affect currently attached animations.
     * Entrances following a reset use [LazyGridItemEntranceScrollContext.InitialLayout] until the
     * grid starts scrolling again, regardless of the direction of the preceding scroll.
     */
    fun reset() {
        enteredKeys.clear()
        resetAsInitialLayout = true
    }
}

/**
 * Remembers entrance history associated with [gridState].
 *
 * Replacing [gridState] creates a new entrance state with empty key history.
 *
 * @param gridState The state of the lazy grid whose items use the returned entrance state.
 * @return A state that coordinates visible-item lookup and per-key entrance history.
 */
@Composable
fun rememberLazyGridItemEntranceState(gridState: LazyGridState): LazyGridItemEntranceState {
    val state = remember(gridState) { LazyGridItemEntranceState(gridState) }
    LaunchedEffect(state) {
        snapshotFlow { gridState.isScrollInProgress }
            .filter { it }
            .collect { state.onScrollStarted() }
    }
    return state
}

/**
 * Scales and fades a lazy grid item into place after its computed entrance delay.
 *
 * The modifier waits until [itemKey] appears in [state]'s visible item information, computes its
 * delay from that layout snapshot, then animates scale and alpha to `1f`. Delay, animation spec,
 * initial scale, and initial alpha are captured when an animation run starts; updates to those
 * parameters apply to the next run rather than altering one already in progress.
 *
 * With [LazyGridItemEntranceRepeatMode.OncePerKey], [itemKey] is recorded as soon as the run starts
 * and before the computed delay. Cancellation or disposal after that point does not make the key
 * eligible again; call [LazyGridItemEntranceState.reset] to do so explicitly.
 *
 * @receiver The modifier applied to an item of the lazy grid represented by [state].
 * @param itemKey The same stable and unique key supplied to the lazy grid item.
 * @param state Coordinates visible-item lookup and remembers entered keys.
 * @param repeatMode Determines whether the item animates on every composition or once per key.
 * @param delay Computes how long the visible item waits before its transition begins. Negative
 * computed durations are treated as [Duration.ZERO].
 * @param animationSpec The finite animation used for both scale and alpha progress.
 * @param initialScale The scale on both axes at progress zero. It animates to `1f`.
 * @param initialAlpha The alpha at progress zero. It animates to `1f`.
 * @return This modifier followed by the lazy-grid entrance modifier.
 */
context(_: LazyGridItemScope)
fun Modifier.animateLazyGridItemEntrance(
    itemKey: Any,
    state: LazyGridItemEntranceState,
    repeatMode: LazyGridItemEntranceRepeatMode = LazyGridItemEntranceRepeatMode.OncePerKey,
    delay: LazyGridItemEntranceDelay = LazyGridItemEntranceDelay.diagonal(),
    animationSpec: FiniteAnimationSpec<Float> = tween(500, easing = LinearOutSlowInEasing),
    initialScale: Float = 0.8f,
    initialAlpha: Float = 0f
): Modifier =
    this then LazyGridItemEntranceElement(
        itemKey = itemKey,
        state = state,
        repeatMode = repeatMode,
        delay = delay,
        animationSpec = animationSpec,
        initialScale = initialScale,
        initialAlpha = initialAlpha
    )

private data class LazyGridItemEntranceElement(
    val itemKey: Any,
    val state: LazyGridItemEntranceState,
    val repeatMode: LazyGridItemEntranceRepeatMode,
    val delay: LazyGridItemEntranceDelay,
    val animationSpec: FiniteAnimationSpec<Float>,
    val initialScale: Float,
    val initialAlpha: Float
) : ModifierNodeElement<LazyGridItemEntranceNode>() {
    override fun create() =
        LazyGridItemEntranceNode(
            itemKey = itemKey,
            state = state,
            repeatMode = repeatMode,
            delay = delay,
            animationSpec = animationSpec,
            initialScale = initialScale,
            initialAlpha = initialAlpha
        )

    override fun update(node: LazyGridItemEntranceNode) {
        node.update(
            itemKey = itemKey,
            state = state,
            repeatMode = repeatMode,
            delay = delay,
            animationSpec = animationSpec,
            initialScale = initialScale,
            initialAlpha = initialAlpha
        )
    }
}

private class LazyGridItemEntranceNode(
    private var itemKey: Any,
    private var state: LazyGridItemEntranceState,
    private var repeatMode: LazyGridItemEntranceRepeatMode,
    private var delay: LazyGridItemEntranceDelay,
    private var animationSpec: FiniteAnimationSpec<Float>,
    private var initialScale: Float,
    private var initialAlpha: Float
) : Modifier.Node(),
    LayoutModifierNode {
    private var animationJob: Job? = null
    private var progress = Animatable(initialProgress())
    private var runningInitialScale = initialScale
    private var runningInitialAlpha = initialAlpha

    override fun onAttach() {
        restartAnimation()
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)

        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                val progress = progress.value
                alpha = lerp(runningInitialAlpha, 1f, progress)
                scaleX = lerp(runningInitialScale, 1f, progress)
                scaleY = lerp(runningInitialScale, 1f, progress)
            }
        }
    }

    fun update(
        itemKey: Any,
        state: LazyGridItemEntranceState,
        repeatMode: LazyGridItemEntranceRepeatMode,
        delay: LazyGridItemEntranceDelay,
        animationSpec: FiniteAnimationSpec<Float>,
        initialScale: Float,
        initialAlpha: Float
    ) {
        val restart =
            this.itemKey != itemKey ||
                this.state !== state ||
                this.repeatMode != repeatMode

        this.itemKey = itemKey
        this.state = state
        this.repeatMode = repeatMode
        this.delay = delay
        this.animationSpec = animationSpec
        this.initialScale = initialScale
        this.initialAlpha = initialAlpha

        if (restart && isAttached) restartAnimation()
    }

    private fun restartAnimation() {
        animationJob?.cancel()
        progress = Animatable(initialProgress())
        runningInitialScale = initialScale
        runningInitialAlpha = initialAlpha

        if (progress.value < 1f) {
            if (repeatMode == LazyGridItemEntranceRepeatMode.OncePerKey) {
                state.markEntered(itemKey)
            }

            val delay = delay
            val animationSpec = animationSpec
            animationJob = coroutineScope.launch { animate(delay, animationSpec) }
        }
    }

    private suspend fun animate(delay: LazyGridItemEntranceDelay, animationSpec: FiniteAnimationSpec<Float>) {
        val item = snapshotFlow {
            state.gridState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == itemKey }
        }
            .filterNotNull()
            .first()

        val entranceDelay = delay
            .compute(state.gridState.createEntranceDelayScope(item, state.scrollContext()))
            .coerceAtLeast(Duration.ZERO)

        if (entranceDelay > Duration.ZERO) delay(entranceDelay)

        progress.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    private fun initialProgress() =
        if (repeatMode == LazyGridItemEntranceRepeatMode.OncePerKey && state.hasEntered(itemKey)) {
            1f
        } else {
            0f
        }
}
