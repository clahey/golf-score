package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.widgets.compose.ListCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    playerListViewModel: PlayerListViewModel = viewModel(),
    onNavigateBack: () -> Boolean,
    onNavigateToPlayerEdit: (Int) -> Unit,
    onNavigateToPlayerAdd: () -> Unit,
    onNavigateToPlayerArchive: (Int) -> Unit,
    onNavigateToPlayerUnarchive: (Int) -> Unit,
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
            ListCard(players.filter { !it.archived }, Modifier.fillMaxWidth()) { player ->
                Row {
                    Text(player.name, Modifier.padding(8.dp).align(Alignment.CenterVertically).weight(1f))
                    Icon(
                        Icons.Filled.Edit,
                        "Edit ${player.name}",
                        modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp).clickable { onNavigateToPlayerEdit(player.id) })
                    Icon(
                        Icons.Filled.Archive,
                        "Archive ${player.name}",
                        modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp).clickable { onNavigateToPlayerArchive(player.id) })
                }
            }
            val archived = players.filter { it.archived }
            if (archived.isNotEmpty()) {
                Text("Archived", style = MaterialTheme.typography.titleMedium)
                for (player in players.filter { it.archived }) {
                    Row {
                        Text(player.name)
                        Icon(
                            Icons.Filled.Unarchive,
                            "Unarchive ${player.name}",
                            modifier = Modifier.clickable { onNavigateToPlayerUnarchive(player.id) })
                    }
                }
            }
        }
    }
}