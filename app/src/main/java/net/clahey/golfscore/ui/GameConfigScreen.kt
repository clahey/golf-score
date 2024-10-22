package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.widgets.compose.Action
import net.clahey.widgets.compose.DialogCard
import net.clahey.widgets.compose.DialogCardScope

@Composable
fun GameConfigScreen(
    onNavigateBack: () -> Unit,
    gameConfigViewModel: GameConfigViewModel = viewModel(),
    onNavigateToPlayerAdd: () -> Unit,
    playerAddResponseListener: DialogResponseReceiver<Int>,
) {
    val gameUiState by gameConfigViewModel.uiState.collectAsState()
    val playerList by gameConfigViewModel.playerList.collectAsState(initial = listOf())

    val commitMsg = if (gameConfigViewModel.isAdd) "Add" else "Save"

    DialogParent(playerAddResponseListener, gameConfigViewModel.onPlayerAdded)

    DialogCard(
        listOf(
            Action(commitMsg, { gameConfigViewModel.commit(); onNavigateBack() }, isDefault = true),
            Action("Cancel", { onNavigateBack() }, isCancel = true)
        )
    ) {
        TextFieldHandleDefaults(
            gameUiState.title,
            gameConfigViewModel::setTitle,
            label = { Text("Title") },
            singleLine = true,
            defaultFocus = true,
        )
        NumberTextField(
            gameUiState.holeCount,
            gameConfigViewModel::setHoleCount,
            label = { Text("Hole Count:") },
        )
        for (player in playerList) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(player.id in gameUiState.players, onCheckedChange = {
                    if (it) {
                        gameConfigViewModel.addPlayer(player.id)
                    } else {
                        gameConfigViewModel.removePlayer(player.id)
                    }
                })
                Text(player.name)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton({onNavigateToPlayerAdd()}) {
                Icon(Icons.Filled.Add, "Add Player")
                Text("Add Player")
            }
        }
    }
}

@Composable
fun DialogCardScope.NumberTextField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
) {
    TextFieldHandleDefaults(
        value.toString(), {
            val asInt = it.toIntOrNull()
            if (asInt != null) {
                onValueChange(asInt)
            }
        },
        modifier = modifier,
        label = label,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}
