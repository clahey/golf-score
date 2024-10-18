package net.clahey.golfscore.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    DialogCard(
        listOf(
            Action(
                commitMsg,
                { playerConfigViewModel.commit(); onNavigateBack() },
                isDefault = true
            ),
            Action("Cancel", { onNavigateBack() }, isCancel = true)
        )
    ) {
        TextFieldHandleDefaults(
            playerUiState.name,
            playerConfigViewModel::setName,
            label = { Text("Name") },
            singleLine = true,
            defaultFocus = true,
        )
    }
}
