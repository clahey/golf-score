package net.clahey.golfscore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.clahey.golfscore.data.database.AppDatabase

class GameListViewModel(application: Application) : AndroidViewModel(application) {

    data class PlayerData(val id: Int, val name: String, val score: Int)
    data class GameData(val id: Int, val title: String, val players: List<PlayerData>)
    data class AppState(val games: List<GameData>)

    private val db = AppDatabase.getInstance(application.applicationContext)

    val appState: Flow<AppState> = db.gameDao().getAllWithPlayersAndScores().map {
        AppState(it.map {
            val playerScores = buildMap {for (score in it.scores) put(score.player, score.score) }
            GameData(it.game.id, it.game.title, it.players.map { PlayerData(it.id, it.name, playerScores[it.id] ?: 0) })
        })
    }
}