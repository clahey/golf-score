package net.clahey.widgets.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SectionWithHeader(
    title: String,
    actions: @Composable() (RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                title,
                Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                style = MaterialTheme.typography.headlineMedium
            )

            if (actions != null) {
                Row(Modifier.align(Alignment.CenterVertically)) {
                    actions()
                }
            }
        }
        content()
    }
}