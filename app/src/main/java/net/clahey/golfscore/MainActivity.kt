package net.clahey.golfscore

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.clahey.golfscore.ui.GameConfigScreen
import net.clahey.golfscore.ui.GameListScreen
import net.clahey.golfscore.ui.GameScreen
import net.clahey.golfscore.ui.PlayerConfigScreen
import net.clahey.golfscore.ui.SetScoreScreen
import net.clahey.golfscore.ui.theme.GolfScoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GolfScoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Serializable
object GameList

@Serializable
data class Game(val id: Int)

/* Pass null to create a new player. */
@Serializable
data class PlayerConfig(val id: Int?)

/* Pass null to create a new game. */
@Serializable
data class GameConfig(val id: Int?)

@Serializable
data class SetScore(val player: String, val hole: Int, val playerId: Int, val score: Int?)

@Parcelize
data class ScoreUpdate(val hole: Int, val playerId: Int, val score: Int?) : Parcelable

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = GameList) {
        composable<GameList> {
            GameListScreen(onNavigateToGame = { navController.navigate(Game(it)) },
                onNavigateToGameAdd = { navController.navigate(GameConfig(null)) },
                onNavigateToPlayerAdd = { navController.navigate(PlayerConfig(null)) })
        }
        composable<Game> {
            GameScreen(onNavigateToGameEdit = { navController.navigate(GameConfig(it)) },
                onNavigateBack = { navController.popBackStack() },
                onAddScoreObserver = {
                    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ScoreUpdate>(
                        "score_update"
                    )?.observeForever(it)
                },
                onRemoveScoreObserver = {
                    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ScoreUpdate>(
                        "score_update"
                    )?.removeObserver(it)
                },
                onChangeScore = { player, hole, playerId, score ->
                    navController.navigate(

                        SetScore(
                            player, hole, playerId, score
                        )
                    )
                })
        }
        dialog<GameConfig> { GameConfigScreen(onNavigateBack = { navController.popBackStack() }) }
        dialog<PlayerConfig> { PlayerConfigScreen(onNavigateBack = { navController.popBackStack() }) }
        dialog<SetScore> {
            SetScoreScreen(onSetScore = {
                navController.previousBackStackEntry?.savedStateHandle?.set("score_update", it)
                navController.popBackStack()
            }, onCancel = { navController.popBackStack() })
        }
    }
}

