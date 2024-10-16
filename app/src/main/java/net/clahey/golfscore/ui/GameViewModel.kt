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

data class GameState (val holes: List<Hole>, val playerScores: Map<Int, Int>)

class GameViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application.applicationContext)
    val gameId: Int = savedStateHandle.toRoute<net.clahey.golfscore.Game>().id
    private val _uiState = MutableStateFlow(GameState(listOf(), mapOf()))
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()
    val gameConfig: Flow<GameConfig> = db.gameDao().getWithPlayersAndScores(gameId).map {
        GameConfig(it.players.map { Player(it.name, it.id) }, it.game.title, it.game.holeCount)
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
            val totals = mutableMapOf<Int, Int>()
            for (score in scores) {
                holeMaps[score.hole][score.player] = score.score
                totals[score.player] = (totals[score.player] ?: 0) + score.score
            }
            _uiState.update {
                GameState(holeMaps.mapIndexed { index, map -> Hole(map, (index + 1).toString()) }, totals)
            }
        }
    }

    private fun setScore(player: Int, hole: Int, score: Int) {
        launchDb {

            // check parameters
            val total = uiState.value.holes.sumOf { it.scores[player] ?: 0 } - (uiState.value.holes[hole].scores[player] ?: 0) + score
            db.gameDao().setScore(gameId, player, hole, score, total)
            _uiState.update {
                it.copy(holes = it.holes.copy(
                    hole to it.holes[hole].setPlayerScore(player, score)),
                        playerScores = it.playerScores.copy(player to total))
            }
        }
    }

    private fun clearScore(playerId: Int, hole: Int) {
        val total = uiState.value.holes.sumOf { it.scores[playerId] ?: 0 } - (uiState.value.holes[hole].scores[playerId] ?: 0)
        launchDb {
            // check parameters
            db.gameDao().clearScore(gameId, playerId, hole, total)
            _uiState.update {
                it.copy(holes = it.holes.copy(
                    hole to it.holes[hole].clearPlayerScore(playerId)),
                    playerScores = it.playerScores.copy(playerId to total))
            }
        }
    }

}
