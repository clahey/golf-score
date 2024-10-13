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
import net.clahey.golfscore.ScoreUpdate
import net.clahey.golfscore.data.database.AppDatabase
import net.clahey.golfscore.data.database.Score
import net.clahey.immutable.copy
import java.lang.Integer.max


class GameViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application.applicationContext)
    val gameId: Int = savedStateHandle.toRoute<net.clahey.golfscore.Game>().id
    private val _uiState = MutableStateFlow(listOf<Hole>())
    val uiState: StateFlow<List<Hole>> = _uiState.asStateFlow()
    val gameConfig: Flow<GameConfig> = db.gameDao().getWithPlayers(gameId).map {
        if (it != null) {
            GameConfig(it.players.map { Player(it.name, it.id) }, it.game.title, it.game.holeCount)
        } else {
            GameConfig()
        }
    }

    private fun launchDb(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block()
        }
    }

    val observer: (ScoreUpdate) -> Unit = {
        if (it.score != null) {
            setScore(it.playerId, it.hole, it.score)
        } else {
            clearScore(it.playerId, it.hole)
        }
    }

    init {
        loadGame()
    }

    private fun loadGame() {
        launchDb {
            val game = db.gameDao().get(gameId)
            val holeCount = game!!.holeCount
            val scores: List<Score> = db.gameDao().getScores(gameId)
            val holeMaps = MutableList(
                max(
                    holeCount, scores.maxOfOrNull { it.hole }?.plus(1) ?: 0
                )
            ) { mutableMapOf<Int, Int>() }
            for (score in scores) {
                holeMaps[score.hole].put(score.player, score.score)
            }
            _uiState.update {
                holeMaps.mapIndexed { index, map -> Hole(map, (index + 1).toString()) }
            }
        }
    }

    fun setScore(player: Int, hole: Int, score: Int) {
        launchDb {
            // check parameters
            db.gameDao().setScore(gameId, player, hole, score)
            _uiState.update {
                it.copy(
                    hole to it[hole].setPlayerScore(player, score)
                )
            }
        }
    }

    private fun clearScore(playerId: Int, hole: Int) {

        launchDb {
            // check parameters
            db.gameDao().clearScore(gameId, playerId, hole)
            _uiState.update {
                it.copy(
                    hole to it[hole].clearPlayerScore(playerId)
                )
            }
        }
    }

}

data class Hole(val scores: Map<Int, Int>, val label: String) {

    fun getPlayerScore(player: Int): Int? {
        return scores[player]
    }

    fun setPlayerScore(player: Int, score: Int): Hole = copy(scores = scores.copy(player to score))

    fun clearPlayerScore(player: Int): Hole = copy(scores = buildMap {
        putAll(scores)
        remove(player)
    })
}

data class Player(val name: String, val id: Int)
data class GameConfig(val players: List<Player>, val title: String, val holeCount: Int) {
    constructor() : this(listOf(), "", 0)
}