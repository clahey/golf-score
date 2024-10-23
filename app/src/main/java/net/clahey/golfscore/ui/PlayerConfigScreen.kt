package net.clahey.golfscore.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
import net.clahey.widgets.compose.Action
import net.clahey.widgets.compose.DialogCard

@Composable
fun PlayerConfigScreen(
    onComplete: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    playerConfigViewModel: PlayerConfigViewModel = viewModel(),
) {
    val playerUiState by playerConfigViewModel.uiState.collectAsState()

    if (playerUiState.saved) {
        LaunchedEffect(true) {
            val id = playerUiState.playerId
            if (id != null) {
                onComplete(id)
            } else {
                onNavigateBack()
            }
        }
    }

    val commitMsg =
        if (playerUiState.isAdd) stringResource(R.string.dialog_create_button) else stringResource(
            R.string.dialog_save_button)

    DialogCard(
        listOf(
            Action(
                commitMsg,
                { playerConfigViewModel.commit() },
                isDefault = true
            ),
            Action(stringResource(R.string.dialog_cancel_button), { onNavigateBack() }, isCancel = true)
        )
    ) {
        TextFieldHandleDefaults(
            playerUiState.name,
            playerConfigViewModel::setName,
            label = { Text(stringResource(R.string.player_config_name_label)) },
            singleLine = true,
            defaultFocus = true,
        )
    }
}
