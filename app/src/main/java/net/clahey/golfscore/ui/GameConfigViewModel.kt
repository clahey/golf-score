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

class GameConfigViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    data class GameConfigState(
        val loading: Boolean,
        val title: String = "",
        val holeCount: Int = 18,
        val players: List<Int> = listOf(),
        val gameId: Int? = null,
        val saving: Boolean = false,
        val saved: Boolean = false,)

    val onPlayerAdded: (Int) -> Unit = {
        addPlayer(it)
    }

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val _uiState = MutableStateFlow(GameConfigState(true, gameId = savedStateHandle.toRoute<net.clahey.golfscore.GameConfigRoute>().id))
    val uiState: StateFlow<GameConfigState> = _uiState.asStateFlow()
    val isAdd = uiState.value.gameId == null

    val playerList: Flow<List<Player>> = db.playerDao().getAllFlow().map {
        it.filter { !it.archived }.map { Player(it.name, it.id) }
    }

    init {
        val id = uiState.value.gameId
        if (id == null) {
            _uiState.update {
                GameConfigState(false)
            }
        } else {
            launchDb {
                val game = db.gameDao().get(id)
                if (game == null) {
                    _uiState.update { GameConfigState(false) }
                } else {
                    val title = game.title
                    val holeCount = game.holeCount
                    val players = db.gameDao().getPlayerIds(id)
                    _uiState.update { GameConfigState(false, title, holeCount, players, id) }
                }
            }
        }
    }

    fun setHoleCount(holeCount: Int) {
        _uiState.update { it.copy(holeCount = holeCount) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun addPlayer(player: Int) {
        if (_uiState.value.players.contains(player)) {
            return
        }
        _uiState.update { it.copy(players = it.players + listOf(player)) }
    }

    fun removePlayer(player: Int) {
        if (!_uiState.value.players.contains(player)) {
            return
        }
        _uiState.update { it.copy(players = it.players.filter { it != player }) }
    }



    fun commit() {
        if (uiState.value.loading) {
            return
        }
        _uiState.update { it.copy(saving = true) }
        launchDb {
            val id = uiState.value.gameId
            if (id != null) {
                db.gameDao().updateGameConfig(
                    id,
                    uiState.value.title,
                    uiState.value.holeCount,
                    uiState.value.players
                )
                _uiState.update { it.copy(saving = false, saved = true) }
            } else {
                val gameId = db.gameDao()
                    .insert(uiState.value.title, uiState.value.holeCount, uiState.value.players)
                _uiState.update { it.copy(saving = false, saved = true, gameId = gameId) }
            }
        }
    }

    private fun launchDb(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

}
