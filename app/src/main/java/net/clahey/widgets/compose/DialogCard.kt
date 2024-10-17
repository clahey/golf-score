package net.clahey.widgets.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp

data class Action(
    val text: String,
    val onActivate: () -> Unit,
    val isDefault: Boolean = false,
    val isCancel: Boolean = false,
)

/** Scope for the children of [DialogCard]. */
@LayoutScopeMarker
@Immutable
data class DialogCardScope(
    val default: Action?,
    val cancel: Action?,
) {
    @Composable
    fun TextFieldHandleDefaults(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: @Composable (() -> Unit)? = null,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        singleLine: Boolean = false,
    ) {
        TextField(
            value,
            onValueChange,
            modifier.onKeyEvent {
                when (it.key) {
                    Key.Enter -> if (default != null) {
                        default.onActivate(); true
                    } else false

                    Key.Escape -> if (cancel != null) {
                        cancel.onActivate(); true
                    } else false

                    else -> false
                }
            },
            label = label,
            keyboardOptions = keyboardOptions,
            keyboardActions = if (default != null) KeyboardActions(onDone = { default.onActivate }) else KeyboardActions(),
            singleLine = singleLine,
        )
    }
}

@Composable
fun DialogCard(
    actions: List<Action>,
    modifier: Modifier = Modifier,
    block: @Composable DialogCardScope.() -> Unit,
) {
    val default: Action? = actions.filter { it.isDefault }.firstOrNull()
    val cancel: Action? = actions.filter { it.isCancel }.firstOrNull()
    val scope = DialogCardScope(default, cancel)
    Card {
        Column(Modifier.padding(8.dp)) {
            scope.block()
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
