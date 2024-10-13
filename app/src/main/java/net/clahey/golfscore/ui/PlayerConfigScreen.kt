package net.clahey.golfscore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PlayerConfigScreen(
    onNavigateBack: () -> Unit,
    playerConfigViewModel: PlayerConfigViewModel = viewModel(),
) {

    val playerUiState by playerConfigViewModel.uiState.collectAsState()

    Card {
        Column(Modifier.padding(8.dp)) {
            Row {
                Text("Name", modifier = Modifier.alignByBaseline())
                TextField(
                    playerUiState.name,
                    playerConfigViewModel::setName,
                    modifier = Modifier.alignByBaseline()
                )
            }
            TextButton(onClick = { playerConfigViewModel.commit(); onNavigateBack() }) {
                Text("Commit")
            }
        }
    }
}

