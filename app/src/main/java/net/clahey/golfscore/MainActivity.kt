package net.clahey.golfscore

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.clahey.golfscore.ui.AboutScreen
import net.clahey.golfscore.ui.DialogWithResponse
import net.clahey.golfscore.ui.GameConfigScreen
import net.clahey.golfscore.ui.GameListScreen
import net.clahey.golfscore.ui.GameScreen
import net.clahey.golfscore.ui.PlayerArchiveScreen
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

@Serializable
object GameListRoute

@Serializable
data class GameRoute(val id: Int)

/* Pass null to create a new player. */
@Serializable
data class PlayerConfigRoute(val id: Int?)

/* Pass null to create a new game. */
@Serializable
data class GameConfigRoute(val id: Int?)

@Serializable
data class SetScoreRoute(val player: String, val hole: Int, val playerId: Int, val score: Int?)

@Serializable
object PlayerListRoute

@Serializable
data class PlayerArchiveRoute(val playerId: Int, val archive: Boolean)

@Parcelize
data class ScoreUpdate(val hole: Int, val playerId: Int, val score: Int?) : Parcelable

@Serializable
object AboutRoute

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val playerSavedDialog = remember { DialogWithResponse<Int>("player_saved", navController) }
    val scoreUpdateDialog = remember { DialogWithResponse<ScoreUpdate>("score_update", navController) }

    NavHost(navController, startDestination = GameListRoute) {
        composable<GameListRoute> {
            GameListScreen(
                onNavigateToGame = { navController.navigate(GameRoute(it)) },
                onNavigateToGameAdd = { navController.navigate(GameConfigRoute(null)) },
                onNavigateToPlayerAdd = { navController.navigate(PlayerConfigRoute(null)) },
                onNavigateToPlayerEdit = { id -> navController.navigate(PlayerConfigRoute(id)) },
                onNavigateToPlayerArchive = { id: Int ->
                    navController.navigate(
                        PlayerArchiveRoute(
                            id, true
                        )
                    )
                },
                onNavigateToPlayerUnarchive = { id: Int ->
                    navController.navigate(
                        PlayerArchiveRoute(
                            id, false
                        )
                    )
                },
                onNavigateToPlayerList = { navController.navigate(PlayerListRoute) },
                onNavigateToAbout = { navController.navigate(AboutRoute) },
            )
        }
        composable<GameRoute> {
            GameScreen(onNavigateToGameEdit = { navController.navigate(GameConfigRoute(it)) },
                onNavigateBack = { navController.popBackStack() },
                scoreUpdateDialog = scoreUpdateDialog,
                onChangeScore = { player, hole, playerId, score ->
                    navController.navigate(

                        SetScoreRoute(
                            player, hole, playerId, score
                        )
                    )
                })
        }
        composable<AboutRoute> { AboutScreen() }
        dialog<GameConfigRoute> {
            GameConfigScreen(onNavigateBack = { navController.popBackStack() },
                onStartGame = { navController.popBackStack(); navController.navigate(GameRoute(it)) },
                onNavigateToPlayerAdd = { navController.navigate(PlayerConfigRoute(null)) },
                playerAddDialog = playerSavedDialog
            )
        }
        dialog<PlayerConfigRoute> {
            PlayerConfigScreen(onComplete = {
                playerSavedDialog.sendResponse(it)
                navController.popBackStack()
            }, onNavigateBack = { navController.popBackStack() })
        }
        dialog<SetScoreRoute> {
            SetScoreScreen(onSetScore = {
                scoreUpdateDialog.sendResponse(it)
                navController.popBackStack()
            }, onCancel = { navController.popBackStack() })
        }
        composable<PlayerListRoute> {
            PlayerListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayerAdd = { navController.navigate(PlayerConfigRoute(null)) },
                onNavigateToPlayerEdit = { id -> navController.navigate(PlayerConfigRoute(id)) },
                onNavigateToPlayerArchive = { id: Int ->
                    navController.navigate(
                        PlayerArchiveRoute(
                            id, true
                        )
                    )
                },
                onNavigateToPlayerUnarchive = { id: Int ->
                    navController.navigate(
                        PlayerArchiveRoute(
                            id, false
                        )
                    )
                },
            )
        }
        dialog<PlayerArchiveRoute> {
            PlayerArchiveScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

