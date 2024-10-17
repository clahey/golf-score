package net.clahey.golfscore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.ui.PlayerConfigViewModel.PlayerConfigState

class PlayerArchiveViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val route = savedStateHandle.toRoute<net.clahey.golfscore.PlayerArchiveRoute>()
    val isArchive = route.archive

    data class PlayerArchiveState(val name: String, val loading: Boolean) {
        constructor(loading: Boolean): this("", loading)
    }

    private val _uiState = MutableStateFlow(PlayerArchiveState(true))
    val uiState: StateFlow<PlayerArchiveState> = _uiState.asStateFlow()

    private fun launchDb(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

    init {
        launchDb {
            val player = db.playerDao().get(route.playerId)
            if (player != null) {
                _uiState.update { PlayerArchiveState(player.name, false) }
            }
        }
    }

    fun commit() {
        launchDb {
            if (route.archive) {
                db.playerDao().archivePlayer(route.playerId)
            } else {
                db.playerDao().unarchivePlayer(route.playerId)
            }
        }
    }
}
