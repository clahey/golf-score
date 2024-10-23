package net.clahey.golfscore.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
import net.clahey.widgets.compose.Action
import net.clahey.widgets.compose.DialogCard

@Composable
fun PlayerArchiveScreen(
    onNavigateBack: () -> Boolean,
    playerArchiveViewModel: PlayerArchiveViewModel = viewModel(),
) {

    val uiState by playerArchiveViewModel.uiState.collectAsState()
    val commitMsg = if (playerArchiveViewModel.isArchive) {
        stringResource(R.string.player_archive_archive_button)
    } else {
        stringResource(R.string.player_archive_unarchive_button)
    }
    DialogCard(
        listOf(Action(
            commitMsg,
            { playerArchiveViewModel.commit(); onNavigateBack() },
            isDefault = true
        ),
            Action(stringResource(R.string.dialog_cancel_button), { onNavigateBack() }, isCancel = true)
        )
    ) {
        if (playerArchiveViewModel.isArchive) {
            Text(stringResource(R.string.player_archive_archive_dialog_text, uiState.name))
        } else {
            Text(stringResource(R.string.player_archive_unarchive_dialog_text, uiState.name))
        }
    }
}