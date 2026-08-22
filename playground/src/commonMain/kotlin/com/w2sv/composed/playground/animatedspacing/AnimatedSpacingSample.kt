package com.w2sv.composed.playground.animatedspacing

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.w2sv.composed.ui.layout.AnimatedSpacingColumn
import com.w2sv.composed.ui.layout.ExperimentalAnimatedSpacingApi

@Composable
@OptIn(ExperimentalAnimatedSpacingApi::class)
fun AnimatedSpacingSample() {
    var firstVisible by remember { mutableStateOf(true) }
    var middleVisible by remember { mutableStateOf(true) }
    var lastVisible by remember { mutableStateOf(true) }

    Column(Modifier.padding(32.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { firstVisible = !firstVisible }) {
                Text("A")
            }

            Button(onClick = { middleVisible = !middleVisible }) {
                Text("B")
            }

            Button(onClick = { lastVisible = !lastVisible }) {
                Text("C")
            }
        }

        Spacer(Modifier.height(32.dp))

        AnimatedSpacingColumn(
            spacing = 24.dp,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium)
        ) {
            AnimatedVisibility(
                visible = firstVisible,
                animationSpec = spring(Spring.DampingRatioHighBouncy)
            ) {
                Item("A")
            }

            AnimatedVisibility(
                visible = middleVisible,
                animationSpec = spring(Spring.DampingRatioHighBouncy)
            ) {
                Item("B")
            }

            AnimatedVisibility(
                visible = lastVisible,
                animationSpec = spring(Spring.DampingRatioHighBouncy)
            ) {
                Item("C")
            }

            Spacer(Modifier.weight(1f))

            Item("Footer")
        }
    }
}

@Composable
private fun Item(label: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label)
        }
    }
}
