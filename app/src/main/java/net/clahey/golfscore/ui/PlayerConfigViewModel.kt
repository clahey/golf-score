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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.ui.PlayerConfigViewModel.PlayerConfigState

class PlayerConfigViewModel (
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    data class PlayerConfigState(
        val loading: Boolean,
        val name: String,
    ) {
        constructor(loading: Boolean) : this(loading, "")
    }

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val _uiState = MutableStateFlow(PlayerConfigState(true))
    val uiState: StateFlow<PlayerConfigState> = _uiState.asStateFlow()

    private var playerId: Int? = savedStateHandle.toRoute<net.clahey.golfscore.PlayerConfig>().id

    init {
        val id = playerId
        if (id == null) {
            _uiState.update {
                PlayerConfigState(false)
            }
        } else {
            launchDb {
                val player = db.playerDao().get(id)
                if (player == null) {
                    _uiState.update { PlayerConfigState(false) }
                } else {
                    _uiState.update { PlayerConfigState(false, player.name) }
                }
            }
        }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun commit() {
        if (uiState.value.loading) {
            return
        }
        _uiState.update { it.copy(loading = true) }
        launchDb {
            val id = playerId
            if (id != null) {
                db.playerDao().updatePlayerConfig(id, uiState.value.name)
            } else {
                playerId = db.playerDao().insert(uiState.value.name)
            }
            _uiState.update { it.copy(loading = false) }
        }
    }

    private fun launchDb(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }
}
