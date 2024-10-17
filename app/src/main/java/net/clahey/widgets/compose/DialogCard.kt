package net.clahey.widgets.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Action(
    val text: String,
    val onActivate: () -> Unit,
)

@Composable
fun DialogCard(
    actions: List<Action>,
    modifier: Modifier = Modifier,
    block: @Composable ColumnScope.() -> Unit,
) {
    Card {
        Column(Modifier.padding(8.dp)) {
            block()
            Row {
                for (action in actions) {
                    TextButton(onClick = action.onActivate) {
                        Text(action.text)
                    }
                }
            }
        }
    }
}
