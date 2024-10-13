package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameConfigScreen(
    onNavigateBack: () -> Unit,
    gameConfigViewModel: GameConfigViewModel = viewModel(),
) {
    val gameUiState by gameConfigViewModel.uiState.collectAsState()
    val playerList by gameConfigViewModel.playerList.collectAsState(initial = listOf())

    Card {
        Column(Modifier.padding(8.dp)) {
            Row {
                Text("Title", modifier = Modifier.alignByBaseline())
                TextField(gameUiState.title, gameConfigViewModel::setTitle, modifier = Modifier.alignByBaseline())
            }
            Row(verticalAlignment = Alignment.CenterVertically){
                Text("Hole Count:", modifier = Modifier.alignByBaseline())
                NumberTextField(gameUiState.holeCount, gameConfigViewModel::setHoleCount, modifier = Modifier.alignByBaseline())
            }
            Column {
                for (player in playerList) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Text(player.name)
                        Checkbox(player.id in gameUiState.players, onCheckedChange = {
                            if (it) {
                                gameConfigViewModel.addPlayer(player.id)
                            } else {
                                gameConfigViewModel.removePlayer(player.id)
                            }
                        })
                    }
                }
                TextButton(onClick = { gameConfigViewModel.commit(); onNavigateBack() }) {
                    Text("Commit")
                }
            }
        }
    }
}

@Composable
fun NumberTextField(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier) {
    TextField(
        value.toString(), {
            val asInt = it.toIntOrNull()
            if (asInt != null) {
                onValueChange(asInt)
            }
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}
