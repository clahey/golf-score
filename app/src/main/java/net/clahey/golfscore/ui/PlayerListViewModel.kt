package net.clahey.golfscore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.data.database.Player
import net.clahey.golfscore.ui.PlayerConfigViewModel.PlayerConfigState

class PlayerListViewModel (application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application.applicationContext)

    val _uiState= MutableStateFlow(
        PlayerListState(
            archiveExpanded = false
        )
    )

    val uiState = _uiState.asStateFlow()
    val players: Flow<List<Player>> = db.playerDao().getAllFlow()

    fun setArchiveExpanded(expanded: Boolean) {
        _uiState.update { it.copy(archiveExpanded = expanded) }
    }
}

data class PlayerListState(val archiveExpanded: Boolean)