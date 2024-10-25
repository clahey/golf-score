package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
import net.clahey.widgets.compose.ColumnWithScrim
import net.clahey.widgets.compose.ListCard
import net.clahey.widgets.compose.SectionWithHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onNavigateToGame: (Int) -> Unit,
    onNavigateToGameAdd: () -> Unit,
    onNavigateToPlayerAdd: () -> Unit,
    onNavigateToPlayerEdit: (Int) -> Unit,
    onNavigateToPlayerArchive: (Int) -> Unit,
    onNavigateToPlayerUnarchive: (Int) -> Unit,
    onNavigateToAbout: () -> Unit,
    gameListViewModel: GameListViewModel = viewModel(),
    onNavigateToPlayerList: () -> Unit,
) {
    val gameListState by gameListViewModel.appState.collectAsState(
        initial = GameListViewModel.AppState(
            listOf()
        )
    )
    val playersState by gameListViewModel.players.collectAsState(
        initial = listOf()
    )
    val activePlayers = playersState.filter { !it.archived }
    val uiState by gameListViewModel.uiState.collectAsState()
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateToGameAdd() }) {
            Icon(Icons.Filled.Add, stringResource(R.string.main_add_game_icon_description))
        }
    }, topBar = {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text(stringResource(R.string.main_page_title)) },
            actions = {
                IconButton(onClick = { onNavigateToAbout() }) {
                    Icon(
                        Icons.Filled.Info, stringResource(R.string.main_about_icon_description)
                    )
                }
            },
        )
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            ColumnWithScrim(
                uiState.playerEditActive,
                Modifier.verticalScroll(rememberScrollState()),
                onComplete = { gameListViewModel.setPlayerEditState(false) },
                padding = 8.dp,
                spacing = 16.dp
            ) {
                SectionWithHeader(stringResource(R.string.main_players_header),
                    Modifier.aboveScrim(),
                    actions = {
                        IconButton({
                            val useInlineEdit = false
                            if (useInlineEdit) {
                                gameListViewModel.setPlayerEditState(!uiState.playerEditActive)
                            } else {
                                onNavigateToPlayerList()
                            }
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                stringResource(R.string.main_edit_players_icon_description)
                            )
                        }
                    }) {
                    ListCard(Modifier.fillMaxWidth()) {
                        items(activePlayers) { player, padding ->
                            Row(Modifier.padding(padding)) {
                                Text(
                                    player.name,
                                    Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                )
                                if (uiState.playerEditActive) {
                                    IconButton(
                                        onClick = { onNavigateToPlayerEdit(player.id) },
                                        Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(Icons.Filled.Edit, "Edit Player")
                                    }
                                }
                            }
                        }
                        if (uiState.playerEditActive) {
                            item { padding ->
                                Row(Modifier.padding(padding)) {
                                    Text(
                                        "Add Player",
                                        Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(1f)
                                    )
                                    IconButton(
                                        onClick = { onNavigateToPlayerAdd() },
                                        Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(Icons.Filled.Add, "Add Player")
                                    }
                                }
                            }
                        }
                    }
                    if (uiState.playerEditActive) {
                        ArchivedPlayers(
                            playersState.filter { it.archived },
                            uiState.archivedPlayersExpanded,
                            gameListViewModel::setArchivedPlayersExpanded,
                            Modifier,
                            onNavigateToPlayerUnarchive
                        )
                    }
                }

                SectionWithHeader(stringResource(R.string.main_games_header)) {
                    ListCard(Modifier.fillMaxWidth()) {
                        items(gameListState.games) { game, padding ->
                            Column(
                                Modifier
                                    .clickable(onClick = { onNavigateToGame(game.id) })
                                    .fillMaxWidth()
                                    .padding(padding)
                            ) {
                                Text(game.title, style = MaterialTheme.typography.titleMedium)
                                for (player in game.players) {
                                    Text(
                                        stringResource(
                                            R.string.main_game_list_player_plus_score,
                                            player.name,
                                            player.score
                                        ), style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

