package net.clahey.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

class StringInputDialogState<T>(extraGenerator: () -> T) {
    fun showDialog(initialValue: String) {
        textFieldValue = TextFieldValue(initialValue, TextRange(0, initialValue.length))
        dialogShown = true
    }

    fun hideDialog() {
        dialogShown = false
    }

    var textFieldValue by mutableStateOf(TextFieldValue(""))
    var dialogShown by mutableStateOf(false)
    var extra by mutableStateOf(extraGenerator())
}

@Composable
fun rememberStringInputDialogState() = remember { StringInputDialogState {} }


@Composable
fun <T> rememberStringInputDialogState(extraGenerator: () -> T) =
    remember { StringInputDialogState(extraGenerator) }

@Composable
fun <T> StringInputDialog(
    StringInputDialogState: StringInputDialogState<T>,
    onSetValue: (String) -> Unit,
    verifyValue: ((String) -> String)? = null,
    dismissMessage: String = "Dismiss",
    applyMessage: String = "Apply",
) {
    val focusRequester = remember { FocusRequester() }
    if (StringInputDialogState.dialogShown) {
        val textFieldValue = StringInputDialogState.textFieldValue
        var valid = true
        var errorString = ""
        val currentValue = textFieldValue.text
        if (verifyValue != null) {
            val verifyMessage: String = verifyValue(currentValue)
            if (verifyMessage != "") {
                valid = false
                errorString = verifyMessage
            }
        }

        fun setValue(): Boolean =
            if (valid) {
                onSetValue(StringInputDialogState.textFieldValue.text)
                StringInputDialogState.hideDialog()
                true
            } else {
                false
            }

        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = StringInputDialogState.textFieldValue,
                        onValueChange = { value ->
                            StringInputDialogState.textFieldValue = value
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Enter -> setValue()


                                    Key.Escape -> {
                                        StringInputDialogState.hideDialog(); true
                                    }

                                    else -> false
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = { setValue() }),
                        singleLine = true
                    )
                    Text(errorString, color = MaterialTheme.colorScheme.error)
                    Row {
                        TextButton(onClick = { StringInputDialogState.hideDialog() }) {
                            Text(dismissMessage)
                        }
                        TextButton(enabled = valid, onClick = { setValue() }) {
                            Text(applyMessage)
                        }
                    }
                }
                LaunchedEffect("") {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}