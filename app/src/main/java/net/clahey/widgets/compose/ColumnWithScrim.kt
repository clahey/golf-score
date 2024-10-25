package net.clahey.widgets.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import kotlin.math.max

interface ColumnWithScrimScope {
    fun Modifier.aboveScrim(): Modifier
}

object ColumnWithScrimActiveScope : ColumnWithScrimScope {
    override fun Modifier.aboveScrim() = this then Modifier.zIndex(2f)
}

object ColumnWithScrimInactiveScope : ColumnWithScrimScope {
    override fun Modifier.aboveScrim() = this
}

@Composable
fun ColumnWithScrim(
    scrimActive: Boolean,
    modifier: Modifier = Modifier,
    onComplete: (() -> Unit)? = null,
    scrimColor: Color = Color.White.copy(alpha = 0.5f),
    spacing: Dp = 0.dp,
    padding: Dp = 0.dp,
    block: @Composable() (ColumnWithScrimScope.() -> Unit),
) {
    fun MeasureScope.measure(
        blockMeasurables: List<Measurable>,
        scrimMeasurable: Measurable?,
        constraints: Constraints,
    ): MeasureResult {
        val paddingPx = padding.roundToPx()
        val paddedConstraints = constraints.offset(-paddingPx * 2, -paddingPx * 2)
        val blockPlaceables = blockMeasurables.map { it.measure(paddedConstraints) }
        val totalWidth = (blockPlaceables.maxOfOrNull { it.width } ?: 0) + paddingPx * 2
        val totalHeight =
            blockPlaceables.sumOf { it.height } + paddingPx * 2 + spacing.roundToPx() * max(
                (blockPlaceables.size - 1), 0
            )
        val scrimPlaceable = scrimMeasurable?.measure(Constraints.fixed(totalWidth, totalHeight))
        return layout(totalWidth, totalHeight) {
            scrimPlaceable?.place(0, 0)
            var yPosition = 0
            for (placeable in blockPlaceables) {
                placeable.place(paddingPx, yPosition + paddingPx)
                yPosition += placeable.height
                yPosition += spacing.roundToPx()
            }
        }
    }
    if (scrimActive) {
        Layout(
            listOf({ ColumnWithScrimActiveScope.block() }, {
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .clickable { onComplete?.invoke() }) { drawRect(scrimColor) }
            }), modifier
        ) { (blockMeasurables, scrimMeasurables), constraints ->
            measure(blockMeasurables, scrimMeasurables.first(), constraints)
        }
    } else {
        Layout(
            { ColumnWithScrimInactiveScope.block() }, modifier
        ) { blockMeasurables, constraints ->
            measure(blockMeasurables, null, constraints)
        }
    }
}