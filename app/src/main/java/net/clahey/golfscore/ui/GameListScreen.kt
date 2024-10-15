package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onNavigateToGame: (Int) -> Unit,
    onNavigateToGameAdd: () -> Unit,
    gameListViewModel: GameListViewModel = viewModel(),
    onNavigateToPlayerAdd: () -> Unit,
) {
    val gameListState by gameListViewModel.appState.collectAsState(
        initial = GameListViewModel.AppState(
            listOf()
        )
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
        Column(Modifier.padding(innerPadding)) {
            Button(onClick = onNavigateToPlayerAdd) {
                Text("Add Player", style = MaterialTheme.typography.labelMedium)
            }
            LazyColumn {
                for (game in gameListState.games) {
                    item {
                        Row(Modifier.clickable(onClick = { onNavigateToGame(game.id) })) {
                            Text(
                                game.title,
                                modifier = Modifier.alignByBaseline(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "(${game.players.joinToString(", ") { it.name }})",
                                modifier = Modifier.alignByBaseline(),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
}