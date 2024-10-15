package net.clahey.golfscore.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.ScoreUpdate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onNavigateToGameEdit: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onAddScoreObserver: ((ScoreUpdate) -> Unit) -> Unit,
    onRemoveScoreObserver: ((ScoreUpdate) -> Unit) -> Unit,
    onChangeScore: (String, Int, Int, Int?) -> Unit,
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val gameConfig by gameViewModel.gameConfig.collectAsState(initial = GameConfig())

    DisposableEffect(true) {
        onAddScoreObserver(gameViewModel.observer)
        onDispose {
            onRemoveScoreObserver(gameViewModel.observer)
        }
    }
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(gameConfig.title) },

            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }, actions = {
                IconButton(onClick = { onNavigateToGameEdit(gameViewModel.gameId) }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Localized description"
                    )
                }
            })
    }) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            GameScoreDisplay(
                gameConfig,
                gameUiState.holes,
                onShowScoreUpdateDialog = { player: Int, hole: Int ->
                    onChangeScore(
                        gameConfig.players[player].name,
                        hole,
                        player,
                        gameUiState.holes[hole].getPlayerScore(player)
                    )
                })
        }
    }
}

@Composable
fun GameScoreDisplay(
    gameConfig: GameConfig,
    holes: List<Hole>,
    onShowScoreUpdateDialog: ((Int, Int) -> Unit)? = null,
) {
    LazyVerticalGrid(columns = GridCells.Fixed(gameConfig.players.size + 1), Modifier.outline()) {
        item(key = 0) { Text("Hole", Modifier.outline()) }
        for (player in gameConfig.players) {
            item(key = Pair(0, player.id)) { Text(player.name, Modifier.outline()) }
        }
        for (i in 0..<gameConfig.holeCount) {
            val hole = holes.getOrElse(i) { Hole(mapOf(), (i + 1).toString()) }
            item(key = i + 1) { Text(hole.label, Modifier.outline()) }
            for (player in gameConfig.players) {
                item(key = Pair(i + 1, player.id)) {
                    ScoreDisplay(hole.scores[player.id],
                        onShowScoreUpdateDialog = onShowScoreUpdateDialog?.let { f ->
                            { f(player.id, i) }
                        })
                }
            }
        }
    }
}

@Composable
fun ScoreDisplay(score: Int?, onShowScoreUpdateDialog: (() -> Unit)? = null) {
    val modifier = if (onShowScoreUpdateDialog != null) {
        Modifier.clickable(onClick = onShowScoreUpdateDialog)
    } else {
        Modifier
    }
    Text(
        score?.toString() ?: "", modifier.outline()
    )
}

fun Modifier.outline(): Modifier = border(0.5.dp, SolidColor(Color.Black), RectangleShape)