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
fun PlayerArchiveScreen(onNavigateBack: () -> Boolean,
                        playerArchiveViewModel: PlayerArchiveViewModel = viewModel(),) {

    val uiState by playerArchiveViewModel.uiState.collectAsState()

    Card {
        Column(Modifier.padding(8.dp)) {
            if (playerArchiveViewModel.isArchive) {
                Text("Would you like to archive player ${uiState.name}")
            } else {
                Text("Would you like to unarchive player ${uiState.name}")
            }
            Row {
                TextButton(onClick = { playerArchiveViewModel.commit(); onNavigateBack() }) {
                    if (playerArchiveViewModel.isArchive) {
                        Text("Archive")
                    } else {
                        Text("Unarchive")
                    }
                }
                TextButton(onClick = { onNavigateBack() }) {
                    Text("Cancel")
                }
            }
        }
    }
}