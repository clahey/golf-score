package net.clahey.dialogs

import androidx.activity.compose.BackHandler
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

class IntegerInputDialogState<T>(extraGenerator: () -> T) {
    fun showDialog(initialValue: Int) {
        val stringVal = initialValue.toString()
        textFieldValue = TextFieldValue(stringVal, TextRange(0, stringVal.length))
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
fun rememberIntegerInputDialogState() = remember { IntegerInputDialogState {} }


@Composable
fun <T> rememberIntegerInputDialogState(extraGenerator: () -> T) =
    remember { IntegerInputDialogState(extraGenerator) }

@Composable
fun <T> IntegerInputDialog(
    integerInputDialogState: IntegerInputDialogState<T>,
    onSetValue: (Int) -> Unit,
    verifyValue: ((Int) -> String)? = null,
    nonIntegerMessage: String = "",
    dismissMessage: String = "Dismiss",
    applyMessage: String = "Apply",
) {
    val focusRequester = remember { FocusRequester() }

    if (integerInputDialogState.dialogShown) {
        val textFieldValue = integerInputDialogState.textFieldValue
        var valid = true
        var errorString = ""
        try {
            val currentValue = textFieldValue.text.trim().toInt()
            if (verifyValue != null) {
                val verifyMessage: String = verifyValue(currentValue)
                if (verifyMessage != "") {
                    valid = false
                    errorString = verifyMessage
                }
            }
        } catch (e: NumberFormatException) {
            valid = false
            errorString = nonIntegerMessage
        }

        fun setValue(): Boolean =
            if (valid) {
                onSetValue(integerInputDialogState.textFieldValue.text.toInt())
                integerInputDialogState.hideDialog()
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
                        value = integerInputDialogState.textFieldValue,
                        onValueChange = { value ->
                            integerInputDialogState.textFieldValue = value
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Enter -> setValue()


                                    Key.Escape -> {
                                        integerInputDialogState.hideDialog(); true
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
                        TextButton(onClick = { integerInputDialogState.hideDialog() }) {
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