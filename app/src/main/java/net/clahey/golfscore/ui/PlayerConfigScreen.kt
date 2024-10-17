package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.widgets.compose.Action
import net.clahey.widgets.compose.DialogCard

@Composable
fun PlayerConfigScreen(
    onNavigateBack: () -> Unit,
    playerConfigViewModel: PlayerConfigViewModel = viewModel(),
) {
    val playerUiState by playerConfigViewModel.uiState.collectAsState()
    val commitMsg = if (playerConfigViewModel.isAdd) "Create" else "Save"

    DialogCard(listOf(Action(commitMsg, { playerConfigViewModel.commit(); onNavigateBack() }))) {
        Row {
            Text("Name", modifier = Modifier.alignByBaseline())
            TextField(
                playerUiState.name,
                playerConfigViewModel::setName,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}
