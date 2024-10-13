package net.clahey.golfscore.data.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "players")
data class Player(val name: String, @PrimaryKey(autoGenerate = true) val id: Int = 0)

@Dao
interface PlayerDao {

    data class Id(val id: Int)

    @Query("SELECT * from players where players.id = :id")
    fun get(id: Int): Player?

    @Insert
    fun insert(player: Player): Long

    fun insert(name: String): Int {
        val rowid = insert(Player(name))
        return lookupId(rowid).id
    }

    @Query("SELECT id from players where rowid = :rowid")
    fun lookupId(rowid: Long): Id

    @Query("SELECT * from players")
    fun getAll(): List<Player>

    @Query("SELECT * from players")
    fun getAllFlow(): Flow<List<Player>>

    @Delete(entity = Player::class)
    fun delete(id: Id)

    fun delete(id: Int) {
        delete(Id(id))
    }

    @Query("UPDATE players set name = :name where id = :id")
    fun updatePlayerConfig(id: Int, name: String)
}

@Entity(tableName = "games")
data class Game(
    @ColumnInfo(defaultValue = "") val title: String,
    val holeCount: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)

@Entity(tableName = "game_to_player", primaryKeys = ["game", "player"])
data class GameToPlayer(val game: Int, val player: Int)

@Entity(tableName = "scores", primaryKeys = ["game", "player", "hole"])
data class Score(val game: Int, val player: Int, val hole: Int, val score: Int)

data class GameWithPlayers(val game: Game, val players: List<Player>)

@Dao
interface GameDao {

    data class Id(val id: Int)

    @Insert
    fun insert(game: Game): Long

    fun insert(title: String, holeCount: Int): Int {
        val rowid = insert(
            Game(
                title, holeCount
            )
        )
        return lookupId(rowid).id
    }

    @Transaction
    fun insert(title: String, holeCount: Int, players: List<Int>): Int {
        val gameId = insert(title, holeCount)
        for (player in players) {
            addPlayer(gameId, player)
        }
        return gameId
    }

    @Query("SELECT id from games where rowid = :rowid")
    fun lookupId(rowid: Long): Id

    @Query("SELECT * from games")
    fun getAll(): Flow<List<Game>>

    @Query(
        "SELECT * from games " + "join players, game_to_player " + "where games.id = game_to_player.game and players.id = game_to_player.player"
    )
    fun getAllWithPlayersInternal(): Flow<Map<Game, List<Player>>>

    fun getAllWithPlayers(): Flow<List<GameWithPlayers>> = getAllWithPlayersInternal().map {
        buildList {
            for ((game, players) in it) {
                add(GameWithPlayers(game, players))
            }
        }
    }

    @Query(
        "SELECT * from games JOIN players, game_to_player " + "where games.id = :gameId " + "and games.id = game_to_player.game " + "and players.id = game_to_player.player"
    )
    fun getWithPlayersInternal(gameId: Int): Flow<Map<Game, List<Player>>>

    fun getWithPlayers(gameId: Int): Flow<GameWithPlayers?> = getWithPlayersInternal(gameId).map {
        if (it.size == 1) {
            for ((game, players: List<Player>) in it) {
                return@map (GameWithPlayers(game, players))
            }
        }
        null
    }


    @Query("SELECT * from games LIMIT 1")
    fun getOne(): Game?

    @Query("SELECT * from games where games.id = :id")
    fun get(id: Int): Game?

    @Query(
        "SELECT players.* from players" + " INNER JOIN game_to_player on players.id = game_to_player.player" + " where game_to_player.game = :game"
    )
    fun getPlayers(game: Int): List<Player>


    @Query(
        "SELECT players.id from players" + " INNER JOIN game_to_player on players.id = game_to_player.player" + " where game_to_player.game = :game"
    )
    fun getPlayerIds(game: Int): List<Int>

    @Delete(entity = Game::class)
    fun delete(id: Id)

    fun delete(id: Int) {
        delete(Id(id))
    }

    @Query("INSERT INTO game_to_player (game, player) VALUES (:game, :player)")
    fun addPlayer(game: Int, player: Int)

    @Query(
        "INSERT OR REPLACE INTO scores (game, player, hole, score)" + "values (:game, :player, :hole, :score)"
    )
    fun setScore(game: Int, player: Int, hole: Int, score: Int)

    @Query(
        "DELETE FROM scores where game = :game and hole = :hole and player = :player"
    )
    fun clearScore(game: Int, player: Int, hole: Int)

    @Query(
        "UPDATE games SET " + "title = :title, " + "holeCount = :holeCount where id = :gameId"
    )
    fun setMetadata(
        gameId: Int,
        title: String,
        holeCount: Int,
    )

    @Query("SELECT * from scores where game = :gameId")
    fun getScores(gameId: Int): List<Score>

    @Query("DELETE FROM game_to_player WHERE game = :gameId")
    fun deleteAllPlayers(gameId: Int)

    @Transaction
    fun updateGameConfig(gameId: Int, title: String, holeCount: Int, players: List<Int>) {
        setMetadata(
            gameId, title, holeCount
        )
        deleteAllPlayers(gameId)
        for (player in players) {
            addPlayer(gameId, player)
        }
    }
}