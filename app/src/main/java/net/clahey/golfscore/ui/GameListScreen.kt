package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
import net.clahey.widgets.compose.ListCard
import net.clahey.widgets.compose.SectionWithHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onNavigateToGame: (Int) -> Unit,
    onNavigateToGameAdd: () -> Unit,
    gameListViewModel: GameListViewModel = viewModel(),
    onNavigateToPlayerList: () -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    val gameListState by gameListViewModel.appState.collectAsState(
        initial = GameListViewModel.AppState(
            listOf()
        )
    )
    val playersState by gameListViewModel.players.collectAsState(
        initial = listOf()
    )
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
            Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionWithHeader(stringResource(R.string.main_players_header), actions = {
                    IconButton(onNavigateToPlayerList) {
                        Icon(
                            Icons.Filled.Edit,
                            stringResource(R.string.main_edit_players_icon_description)
                        )
                    }
                }) {
                    ListCard(playersState, Modifier.fillMaxWidth()) { player, padding ->
                        Text(player.name, Modifier.padding(padding))
                    }
                }
                SectionWithHeader(stringResource(R.string.main_games_header)) {
                    ListCard(gameListState.games, Modifier.fillMaxWidth()) { game, padding ->
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

