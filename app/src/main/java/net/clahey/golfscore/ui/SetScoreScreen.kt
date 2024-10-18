package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.clahey.golfscore.ScoreUpdate

@Composable
fun SetScoreScreen(
    onSetScore: (ScoreUpdate) -> Unit, onCancel: () -> Unit,
    setScoreViewModel: SetScoreViewModel = viewModel(),
) {
    val focusRequester = remember { FocusRequester() }

    val uiState by setScoreViewModel.uiState.collectAsState()


    var valid = true
    var errorString = ""
    val text = uiState.scoreTextField.text
    if (text != "") {
        try {
            text.trim().toInt()
        } catch (e: NumberFormatException) {
            valid = false
            errorString = "Not an Integer"
        }
    }
    fun setValue(): Boolean = if (valid) {
        onSetScore(
            ScoreUpdate(
                setScoreViewModel.setScore.hole,
                setScoreViewModel.setScore.playerId,
                if (text == "") null else text.toInt()
            )
        )
        true
    } else {
        false
    }

    Card(
    ) {
        Column (modifier = Modifier.padding(8.dp)){
            Text("Set Score", style = MaterialTheme.typography.titleLarge)
            TextField(value = uiState.scoreTextField,
                onValueChange = { value ->
                    setScoreViewModel.setScoreText(value)
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        when (it.key) {
                            Key.Enter -> setValue()
                            Key.Escape -> {
                                onCancel(); true
                            }
                            else -> false
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { setValue() }),
                singleLine = true,
                label = { Text("${setScoreViewModel.setScore.player}: Hole ${setScoreViewModel.setScore.hole + 1}") }
            )
            Text(errorString, color = MaterialTheme.colorScheme.error)
            Row {
                TextButton(onClick = { onCancel() }) {
                    Text("Cancel")
                }
                TextButton(enabled = valid, onClick = { setValue() }) {
                    Text("Set Score")
                }
            }
        }
        LaunchedEffect("") {
            focusRequester.requestFocus()
        }
    }
}