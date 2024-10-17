package net.clahey.golfscore.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.widgets.compose.Action
import net.clahey.widgets.compose.DialogCard

@Composable
fun PlayerArchiveScreen(
    onNavigateBack: () -> Boolean,
    playerArchiveViewModel: PlayerArchiveViewModel = viewModel(),
) {

    val uiState by playerArchiveViewModel.uiState.collectAsState()
    val commitMsg = if (playerArchiveViewModel.isArchive) {
        "Archive"
    } else {
        "Unarchive"
    }
    DialogCard(
        listOf(Action(
            commitMsg,
            { playerArchiveViewModel.commit(); onNavigateBack() },
            isDefault = true
        ),
            Action("Cancel", { onNavigateBack() }, isCancel = true)
        )
    ) {
        if (playerArchiveViewModel.isArchive) {
            Text("Would you like to archive player ${uiState.name}")
        } else {
            Text("Would you like to unarchive player ${uiState.name}")
        }
    }
}