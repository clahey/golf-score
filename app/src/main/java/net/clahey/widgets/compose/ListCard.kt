package net.clahey.widgets.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ListCardScope() {
    internal val items = mutableListOf<@Composable ColumnScope.(PaddingValues) -> Unit>()

    fun item(block: @Composable ColumnScope.(PaddingValues) -> Unit) {
        items.add(block)
    }
    fun <T> items(list: List<T>, block: @Composable ColumnScope.(T, PaddingValues) -> Unit) {
        items.addAll(list.map{ t -> { padding -> block(t, padding) } })
    }
}

@Composable
fun ListCard(
    modifier: Modifier = Modifier,
    block: ListCardScope.() -> Unit,
) {
    val padding = PaddingValues(8.dp)
    val scope = ListCardScope()
    scope.block()
    OutlinedCard(modifier) {
        Column {
            var first = true
            for (item in scope.items) {
                if (first) {
                    first = false
                } else {
                    HorizontalDivider()
                }
                item(padding)
            }
        }
    }
}