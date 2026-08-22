package com.w2sv.composed.playground.lazygriditem

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.w2sv.composed.animation.LazyGridItemEntranceState
import com.w2sv.composed.animation.animateLazyGridItemEntrance
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.PlaygroundScrollbar

private const val ITEM_COUNT = 100
private const val CONFIGURATION_KEY = "configuration"

@Composable
internal fun LazyGridItemEntranceGrid(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    configurationExpanded: Boolean,
    onConfigurationExpandedChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    gridState: LazyGridState,
    entranceState: LazyGridItemEntranceState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val orientationTransition = updateTransition(
            targetState = configuration.orientation,
            label = "Grid orientation"
        )
        val configurationPanelWidth by orientationTransition.animateDp(label = "Configuration panel width") { orientation ->
            when (orientation) {
                GridOrientation.Vertical -> maxWidth - PlaygroundDefaults.ContentPadding * 2
                GridOrientation.Horizontal -> maxWidth * 0.5f
            }
        }
        val scrollbarVisible = !orientationTransition.isRunning

        when (configuration.orientation) {
            GridOrientation.Vertical -> Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(configuration.crossAxisCount),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = PlaygroundDefaults.ContentPadding,
                        top = PlaygroundDefaults.ContentPadding,
                        end = PlaygroundDefaults.ContentPadding,
                        bottom = LazyGridItemEntranceDimens.FabClearance
                    ),
                    horizontalArrangement = Arrangement.spacedBy(LazyGridItemEntranceDimens.GridSpacing),
                    verticalArrangement = Arrangement.spacedBy(LazyGridItemEntranceDimens.GridSpacing)
                ) {
                    configurationPanel(
                        configuration = configuration,
                        onConfigurationChange = onConfigurationChange,
                        configurationExpanded = configurationExpanded,
                        onConfigurationExpandedChange = onConfigurationExpandedChange,
                        onReset = onReset,
                        horizontalLayout = false,
                        width = configurationPanelWidth,
                        modifier = Modifier.wrapContentWidth(
                            align = Alignment.Start,
                            unbounded = true
                        )
                    )
                    entranceItems(configuration, entranceState)
                }

                if (scrollbarVisible) {
                    PlaygroundScrollbar(
                        state = gridState,
                        orientation = Orientation.Vertical,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(
                                top = PlaygroundDefaults.ContentPadding,
                                end = PlaygroundDefaults.ScrollbarEdgePadding,
                                bottom = LazyGridItemEntranceDimens.FabClearance
                            )
                            .width(PlaygroundDefaults.ScrollbarThickness)
                    )
                }
            }

            GridOrientation.Horizontal -> Box(modifier = Modifier.fillMaxSize()) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(configuration.crossAxisCount),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = PlaygroundDefaults.ContentPadding,
                        end = PlaygroundDefaults.ContentPadding,
                        top = PlaygroundDefaults.ContentPadding,
                        bottom = PlaygroundDefaults.ContentPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(LazyGridItemEntranceDimens.GridSpacing),
                    verticalArrangement = Arrangement.spacedBy(LazyGridItemEntranceDimens.GridSpacing)
                ) {
                    configurationPanel(
                        configuration = configuration,
                        onConfigurationChange = onConfigurationChange,
                        configurationExpanded = configurationExpanded,
                        onConfigurationExpandedChange = onConfigurationExpandedChange,
                        onReset = onReset,
                        horizontalLayout = true,
                        width = configurationPanelWidth,
                        modifier = Modifier.fillMaxHeight()
                    )
                    entranceItems(configuration, entranceState)
                }

                if (scrollbarVisible) {
                    PlaygroundScrollbar(
                        state = gridState,
                        orientation = Orientation.Horizontal,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = PlaygroundDefaults.ContentPadding)
                            .padding(bottom = PlaygroundDefaults.ScrollbarEdgePadding)
                            .height(PlaygroundDefaults.ScrollbarThickness)
                    )
                }
            }
        }
    }
}

private fun LazyGridScope.configurationPanel(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    configurationExpanded: Boolean,
    onConfigurationExpandedChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    horizontalLayout: Boolean,
    width: Dp,
    modifier: Modifier
) {
    item(
        key = CONFIGURATION_KEY,
        span = { GridItemSpan(maxLineSpan) }
    ) {
        LazyGridItemEntranceConfigurationPanel(
            configuration = configuration,
            onConfigurationChange = onConfigurationChange,
            expanded = configurationExpanded,
            onExpandedChange = onConfigurationExpandedChange,
            onReset = onReset,
            horizontalLayout = horizontalLayout,
            width = width,
            modifier = modifier
        )
    }
}

private fun LazyGridScope.entranceItems(configuration: LazyGridItemEntranceConfiguration, entranceState: LazyGridItemEntranceState) {
    items(
        count = ITEM_COUNT,
        key = { it }
    ) { index ->
        GridItem(
            index = index,
            configuration = configuration,
            modifier = Modifier.animateLazyGridItemEntrance(
                itemKey = index,
                state = entranceState,
                repeatMode = configuration.repeatMode,
                delay = configuration.delay,
                animationSpec = tween(
                    durationMillis = configuration.durationMillis,
                    easing = LinearOutSlowInEasing
                ),
                initialScale = configuration.initialScale,
                initialAlpha = configuration.initialAlpha
            )
        )
    }
}

@Composable
private fun GridItem(
    index: Int,
    configuration: LazyGridItemEntranceConfiguration,
    modifier: Modifier = Modifier
) {
    val mainAxisIndex = index / configuration.crossAxisCount
    val crossAxisIndex = index % configuration.crossAxisCount
    val containerColor = when (mainAxisIndex % 3) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = index.toString(), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Main $mainAxisIndex · Cross $crossAxisIndex",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
