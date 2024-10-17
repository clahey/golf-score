package net.clahey.widgets.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun<T> ListCard(items: List<T>, modifier: Modifier = Modifier, block: @Composable ColumnScope.(T) -> Unit) {
    OutlinedCard(modifier) {
        Column {
            var first = true
            for (item in items) {
                if (first) {
                    first = false
                } else {
                    HorizontalDivider()
                }
                block(item)
            }
        }
    }
}