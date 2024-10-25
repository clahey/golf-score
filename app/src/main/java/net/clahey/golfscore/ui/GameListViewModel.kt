package net.clahey.golfscore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.data.database.Player

data class MainScreenState(val playerEditActive: Boolean = false, val archivedPlayersExpanded: Boolean = false)

class GameListViewModel(application: Application) : AndroidViewModel(application) {

    data class PlayerGameData(val id: Int, val name: String, val score: Int)
    data class GameData(val id: Int, val title: String, val players: List<PlayerGameData>)
    data class AppState(val games: List<GameData>)

    private val db = AppDatabase.getInstance(application.applicationContext)

    val appState: Flow<AppState> = db.gameDao().getAllWithPlayersAndScores().map {
        AppState(it.map {
            val playerScores = buildMap { for (score in it.scores) put(score.player, score.score) }
            GameData(it.game.id,
                it.game.title,
                it.players.filter { !it.archived }
                    .map { PlayerGameData(it.id, it.name, playerScores[it.id] ?: 0) })
        })
    }

    val players: Flow<List<Player>> = db.playerDao().getAllFlow()

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState = _uiState.asStateFlow()

    fun setPlayerEditState(playerEditActive: Boolean) {
        _uiState.update { it.copy(playerEditActive = playerEditActive) }
    }
    fun setArchivedPlayersExpanded(archivedPlayersExpanded: Boolean) {
        _uiState.update { it.copy(archivedPlayersExpanded = archivedPlayersExpanded) }
    }
}
