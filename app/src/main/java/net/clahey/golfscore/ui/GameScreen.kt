package net.clahey.golfscore.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
import net.clahey.golfscore.ScoreUpdate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onNavigateToGameEdit: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    scoreUpdateDialog: DialogWithResponse<ScoreUpdate>,
    onChangeScore: (String, Int, Int, Int?) -> Unit,
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val gameConfig by gameViewModel.gameConfig.collectAsState(initial = GameConfig())

    DialogParent(scoreUpdateDialog, gameViewModel.observer)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(gameConfig.title) },

                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.generic_back_icon_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToGameEdit(gameViewModel.gameId) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.main_edit_game_config_icon_description)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar { RecordButtons(gameConfig, gameUiState, onChangeScore) }
        },
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            GameScoreDisplay(gameConfig,
                gameUiState.holes,
                onShowScoreUpdateDialog = { playerId: Int, hole: Int ->
                    val player = gameConfig.getPlayerById(playerId)
                    onChangeScore(
                        player?.name ?: "",
                        hole,
                        playerId,
                        gameUiState.holes[hole].getPlayerScore(playerId)
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
        item(key = 0) {
            TextBox(
                stringResource(R.string.game_screen_hole_column_header),
                textAlign = CenterEnd
            )
        }
        for (player in gameConfig.players) {
            item(key = Pair(0, player.id)) { TextBox(player.name) }
        }
        val runningTotals = mutableMapOf<Int, Int>()
        for (i in 0..<gameConfig.holeCount) {
            val hole = holes.getOrElse(i) { Hole(mapOf(), (i + 1).toString()) }
            item(key = i + 1) {
                TextBox(
                    hole.label,
                    textAlign = CenterEnd,
                )
            }
            for ((player, score) in hole.scores) {
                runningTotals[player] = (runningTotals[player] ?: 0) + score
            }
            for (player in gameConfig.players) {
                val runningTotal = runningTotals[player.id]
                item(key = Pair(i + 1, player.id)) {
                    ScoreDisplay(hole.scores[player.id],
                        runningTotal ?: 0,
                        onClick = onShowScoreUpdateDialog?.let { f -> { f(player.id, i) } })
                }
            }
        }
    }
}

@Composable
fun ScoreDisplay(score: Int?, runningTotal: Int, onClick: (() -> Unit)?) {
    TextBox(
        if (score == null) "" else stringResource(
            R.string.game_screen_score_with_running_total,
            score,
            runningTotal
        ), onClick = onClick
    )
}

@Composable
fun RecordButtons(
    gameConfig: GameConfig,
    gameUiState: GameState,
    onChangeScore: (String, Int, Int, Int?) -> Unit,
) {
    fun calculateNextHole(playerId: Int? = null): Int? {
        var nextHole = 0
        for (i in 0..<gameConfig.holeCount) {
            if (playerId != null) {
                if (gameUiState.holes[i].scores[playerId] != null) {
                    nextHole = i + 1
                }
            } else {
                if (gameConfig.players.all { player -> gameUiState.holes[i].scores[player.id] != null }) {
                    nextHole = i + 1
                }
            }
        }
        return if (nextHole < gameConfig.holeCount) {
            nextHole
        } else {
            null
        }
    }

    val nextHole = calculateNextHole()

    fun record(playerId: Int) {
        val hole = calculateNextHole(playerId) ?: (gameConfig.holeCount - 1)
        val player = gameConfig.getPlayerById(playerId)
        onChangeScore(
            player?.name ?: "",
            hole,
            playerId,
            gameUiState.holes[hole].getPlayerScore(playerId)
        )

    }

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        for (player in gameConfig.players) {
            item {
                val modifier = Modifier.padding(8.dp, 0.dp)
                when (calculateNextHole(player.id)) {
                    null -> OutlinedButton({ }, modifier, enabled = false) {
                        Text(player.name)
                    }

                    nextHole -> Button({ record(player.id) }, modifier) {
                        Text(player.name)
                    }

                    else -> OutlinedButton({ record(player.id) }, modifier) {
                        Text(player.name)
                    }
                }
            }
        }

    }
}

@Composable
fun Modifier.outline(): Modifier = border(0.5.dp, MaterialTheme.colorScheme.primary, RectangleShape)

@Composable
fun TextBox(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: Alignment? = null,
    onClick: (() -> Unit)? = null,
) {
    val boxModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    Box(modifier = boxModifier.outline()) {
        val textModifier = if (textAlign != null) {
            Modifier.align(textAlign)
        } else {
            Modifier
        }
        Text(
            text, textModifier.padding(8.dp, 2.dp)
        )
    }
}
