package com.w2sv.composed.playground.shake

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.ResetButton
import com.w2sv.composed.playground.shared.SampleConfigurationCard
import com.w2sv.composed.playground.shared.SampleControlPair
import com.w2sv.composed.playground.shared.SampleControlSection
import com.w2sv.composed.playground.shared.toFixed
import kotlin.math.roundToInt

@Composable
internal fun ShakeConfigurationPanel(
    configuration: ShakeConfiguration,
    onConfigurationChange: (ShakeConfiguration) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    SampleConfigurationCard(
        modifier = modifier,
        actions = {
            ResetButton(
                onClick = onReset,
                enabled = configuration != ShakeConfiguration()
            )
        }
    ) {
        SampleControlSection("Parameters") {
            SampleControlPair(
                first = {
                    ParameterSlider(
                        label = "Amplitude",
                        valueLabel = "${configuration.amplitudeDp.roundToInt()} dp",
                        value = configuration.amplitudeDp,
                        valueRange = 0f..60f,
                        onValueChange = { onConfigurationChange(configuration.copy(amplitudeDp = it)) }
                    )
                },
                second = {
                    ParameterSlider(
                        label = "Duration",
                        valueLabel = "${configuration.durationMillis} ms",
                        value = configuration.durationMillis.toFloat(),
                        valueRange = 100f..1_500f,
                        onValueChange = {
                            onConfigurationChange(configuration.copy(durationMillis = it.roundToInt()))
                        }
                    )
                }
            )
            SampleControlPair(
                first = {
                    ParameterSlider(
                        label = "Frequency",
                        valueLabel = "${configuration.frequencyHz.toFixed(1)} Hz",
                        value = configuration.frequencyHz,
                        valueRange = 1f..20f,
                        onValueChange = { onConfigurationChange(configuration.copy(frequencyHz = it)) }
                    )
                },
                second = {
                    ParameterSlider(
                        label = "Decay",
                        valueLabel = configuration.decay.toFixed(2),
                        value = configuration.decay,
                        valueRange = 0f..1f,
                        onValueChange = { onConfigurationChange(configuration.copy(decay = it)) }
                    )
                }
            )
        }
    }
}
