package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.widgets.compose.ListCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onNavigateToGame: (Int) -> Unit,
    onNavigateToGameAdd: () -> Unit,
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
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateToGameAdd() }) {
            Icon(Icons.Filled.Add, "Add Game")
        }
    }, topBar = {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text("Golf Scorecard") },
        )
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(Modifier.padding(8.dp, 8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Players", style = MaterialTheme.typography.titleLarge)
                    Icon(
                        Icons.Filled.Edit,
                        "Edit Players",
                        modifier = Modifier.clickable(onClick = onNavigateToPlayerList)
                    )
                }
                ListCard(playersState, Modifier.fillMaxWidth()) {
                    Text(it.name, modifier = Modifier.padding(8.dp))
                }
                Text("Games", style = MaterialTheme.typography.titleLarge)
                ListCard(gameListState.games, Modifier.fillMaxWidth()) { game ->
                    Column(Modifier.clickable(onClick = { onNavigateToGame(game.id) }).padding(8.dp).fillMaxWidth()) {
                        Text(
                            game.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        for (player in game.players) {
                            Text(
                                "${player.name} : ${player.score}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
