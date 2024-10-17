package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    playerListViewModel: PlayerListViewModel = viewModel(),
    onNavigateBack: () -> Boolean,
    onNavigateToPlayerEdit: (Int) -> Unit,
    onNavigateToPlayerAdd: () -> Unit,
) {
    val players by playerListViewModel.players.collectAsState(initial = listOf())
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateToPlayerAdd() }) {
            Icon(Icons.Filled.Add, "Add Player")
        }
    }, topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text("Players") },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            })
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            for (player in players) {
                Row {
                    Text(player.name)
                    Icon(
                        Icons.Filled.Edit,
                        "Edit ${player.name}",
                        modifier = Modifier.clickable { onNavigateToPlayerEdit(player.id) })
//                    Icon(
//                        Icons.Filled.Archive,
//                        "Archive ${player.name}",
//                        modifier = Modifier.clickable { onNavigateToPlayerArchive(player.id) })
                }
            }
        }
    }
}