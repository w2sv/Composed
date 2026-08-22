package com.w2sv.composed.playground.shake

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.w2sv.composed.animation.rememberShakeController
import com.w2sv.composed.animation.shakenBy
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.PlaygroundVerticalScrollbar
import com.w2sv.composed.playground.shared.SamplePreviewCard
import com.w2sv.composed.playground.shared.toFixed
import kotlinx.coroutines.launch

@Composable
internal fun ShakeLayout(
    configuration: ShakeConfiguration,
    onConfigurationChange: (ShakeConfiguration) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val wideLayout = maxWidth >= ShakeDimens.WideLayoutBreakpoint

        if (wideLayout) {
            WideShakeLayout(configuration, onConfigurationChange, onReset)
        } else {
            CompactShakeLayout(configuration, onConfigurationChange, onReset)
        }
    }
}

@Composable
private fun WideShakeLayout(
    configuration: ShakeConfiguration,
    onConfigurationChange: (ShakeConfiguration) -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(PlaygroundDefaults.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
    ) {
        ShakeConfigurationPanel(
            configuration = configuration,
            onConfigurationChange = onConfigurationChange,
            onReset = onReset,
            modifier = Modifier.fillMaxHeight().weight(1f)
        )
        ShakePreview(configuration, Modifier.fillMaxHeight().weight(1f))
    }
}

@Composable
private fun CompactShakeLayout(
    configuration: ShakeConfiguration,
    onConfigurationChange: (ShakeConfiguration) -> Unit,
    onReset: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(PlaygroundDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
        ) {
            ShakeConfigurationPanel(
                configuration = configuration,
                onConfigurationChange = onConfigurationChange,
                onReset = onReset,
                modifier = Modifier.fillMaxWidth()
            )
            ShakePreview(configuration, Modifier.fillMaxWidth())
        }

        PlaygroundVerticalScrollbar(
            state = scrollState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(
                    top = PlaygroundDefaults.ContentPadding,
                    end = PlaygroundDefaults.ScrollbarEdgePadding,
                    bottom = PlaygroundDefaults.ContentPadding
                ).width(PlaygroundDefaults.ScrollbarThickness)
        )
    }
}

@Composable
private fun ShakePreview(configuration: ShakeConfiguration, modifier: Modifier = Modifier) {
    val controller = rememberShakeController(
        amplitude = configuration.amplitudeDp.dp,
        durationMillis = configuration.durationMillis,
        frequencyHz = configuration.frequencyHz,
        decay = configuration.decay
    )
    val scope = rememberCoroutineScope()

    SamplePreviewCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(PlaygroundDefaults.ControlSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing, alignment = Alignment.CenterVertically)
        ) {
            Text(
                text = "${configuration.durationMillis} ms / 1_000 * ${configuration.frequencyHz.toFixed(1)} Hz = ${
                    configuration.cycleCount.toFixed(
                        1
                    )
                } cycles",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            ElevatedButton(
                modifier = Modifier
                    .widthIn(
                        min = ShakeDimens.ButtonMinWidth,
                        max = ShakeDimens.ButtonMaxWidth
                    ).height(ShakeDimens.ButtonHeight)
                    .shakenBy(controller),
                shape = MaterialTheme.shapes.large,
                onClick = { scope.launch { controller.shake() } }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)
                ) {
                    Icon(Icons.Rounded.Handshake, null)
                    Text(
                        text = if (controller.isShaking) "Shaking…" else "Shake",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                OutlinedButton(
                    onClick = { scope.launch { controller.cancel() } },
                    enabled = controller.isShaking,
                    shape = ButtonGroupDefaults.connectedLeadingButtonShape
                ) {
                    Text("Cancel")
                }
                OutlinedButton(
                    onClick = { scope.launch { controller.cancel(animated = true) } },
                    enabled = controller.isShaking,
                    shape = ButtonGroupDefaults.connectedTrailingButtonShape
                ) {
                    Text("Ease to rest")
                }
            }
        }
    }
}

private object ShakeDimens {
    val WideLayoutBreakpoint = 840.dp
    val ButtonMinWidth = 220.dp
    val ButtonMaxWidth = 320.dp
    val ButtonHeight = 80.dp
}
