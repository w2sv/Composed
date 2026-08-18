package com.w2sv.composed.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.w2sv.composed.animation.rememberShakeController
import com.w2sv.composed.animation.shakenBy
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val verticalSpacing = 24.dp

@Composable
fun ShakeSample() {
    var amplitude by remember { mutableFloatStateOf(20f) }
    var durationMillis by remember { mutableIntStateOf(400) }
    var frequencyHz by remember { mutableFloatStateOf(8f) }
    var decay by remember { mutableFloatStateOf(0.5f) }

    val controller = rememberShakeController(
        amplitude = amplitude.dp,
        durationMillis = durationMillis,
        frequencyHz = frequencyHz,
        decay = decay
    )

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(scrollState)
            .padding(vertical = verticalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(0.4f).aspectRatio(1.9f).shakenBy(controller),
            onClick = { scope.launch { controller.shake() } }
        ) {
            Text(
                text = if (controller.isShaking) "Shaking" else "Shake me",
                style = MaterialTheme.typography.titleLarge
            )
        }

        ParameterSlider(
            label = "Amplitude",
            valueLabel = "${amplitude.roundToInt()} dp",
            value = amplitude,
            valueRange = 0f..60f,
            onValueChange = { amplitude = it }
        )

        ParameterSlider(
            label = "Duration",
            valueLabel = "$durationMillis ms",
            value = durationMillis.toFloat(),
            valueRange = 100f..1_500f,
            onValueChange = { durationMillis = it.roundToInt() }
        )

        ParameterSlider(
            label = "Frequency",
            valueLabel = "%.1f Hz".format(frequencyHz),
            value = frequencyHz,
            valueRange = 1f..20f,
            onValueChange = { frequencyHz = it }
        )

        ParameterSlider(
            label = "Decay",
            valueLabel = "%.2f".format(decay),
            value = decay,
            valueRange = 0f..1f,
            onValueChange = { decay = it }
        )

        Text(
            text = "Cycles ≈ %.1f".format(durationMillis / 1_000f * frequencyHz),
            style = MaterialTheme.typography.bodyMedium
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { scope.launch { controller.cancel() } }, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            OutlinedButton(onClick = { scope.launch { controller.cancel(animated = true) } }, modifier = Modifier.weight(1f)) {
                Text("Cancel animated")
            }
        }
    }
}

@Composable
private fun ParameterSlider(
    label: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.Center) {
            Text(label)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.Center) {
            Text(valueLabel)
        }
    }
}
