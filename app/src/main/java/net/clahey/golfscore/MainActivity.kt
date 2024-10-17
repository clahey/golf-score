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
import net.clahey.golfscore.ui.PlayerListScreen
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

@Serializable object GameListRoute
@Serializable data class GameRoute(val id: Int)
/* Pass null to create a new player. */
@Serializable data class PlayerConfigRoute(val id: Int?)
/* Pass null to create a new game. */
@Serializable data class GameConfigRoute(val id: Int?)
@Serializable data class SetScoreRoute(val player: String, val hole: Int, val playerId: Int, val score: Int?)
@Serializable object PlayerListRoute

@Parcelize
data class ScoreUpdate(val hole: Int, val playerId: Int, val score: Int?) : Parcelable

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = GameListRoute) {
        composable<GameListRoute> {
            GameListScreen(
                onNavigateToGame = { navController.navigate(GameRoute(it)) },
                onNavigateToGameAdd = { navController.navigate(GameConfigRoute(null)) },
                onNavigateToPlayerAdd = { navController.navigate(PlayerConfigRoute(null)) },
                onNavigateToPlayerList = { navController.navigate(PlayerListRoute) },
            )
        }
        composable<GameRoute> {
            GameScreen(onNavigateToGameEdit = { navController.navigate(GameConfigRoute(it)) },
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

                        SetScoreRoute(
                            player, hole, playerId, score
                        )
                    )
                })
        }
        dialog<GameConfigRoute> { GameConfigScreen(onNavigateBack = { navController.popBackStack() }) }
        dialog<PlayerConfigRoute> { PlayerConfigScreen(onNavigateBack = { navController.popBackStack() }) }
        dialog<SetScoreRoute> {
            SetScoreScreen(onSetScore = {
                navController.previousBackStackEntry?.savedStateHandle?.set("score_update", it)
                navController.popBackStack()
            }, onCancel = { navController.popBackStack() })
        }
        composable<PlayerListRoute> { PlayerListScreen(onNavigateBack = { navController.popBackStack() },
            onNavigateToPlayerAdd = { navController.navigate(PlayerConfigRoute(null)) },
            onNavigateToPlayerEdit = {id -> navController.navigate(PlayerConfigRoute(id)) },) }
    }
}

