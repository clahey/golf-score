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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.clahey.golfscore.R
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
            Icon(Icons.Filled.Add, stringResource(R.string.player_list_add_player_icon_description))
        }
    }, topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(stringResource(R.string.player_list_page_title)) },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.generic_back_icon_description)
                    )
                }
            })
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ListCard(players.filter { !it.archived }, Modifier.fillMaxWidth()) { player ->
                Row {
                    Text(
                        player.name,
                        Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )
                    IconButton(
                        onClick = { onNavigateToPlayerEdit(player.id) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Edit,
                            stringResource(
                                R.string.player_list_edit_player_icon_description,
                                player.name
                            ))
                    }
                    IconButton(
                        onClick = { onNavigateToPlayerArchive(player.id) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Archive,
                            stringResource(
                                R.string.player_list_archive_player_icon_description,
                                player.name
                            ))
                    }
                }
            }
            val archived = players.filter { it.archived }
            if (archived.isNotEmpty()) {
                Text(stringResource(R.string.player_list_archived_header), style = MaterialTheme.typography.titleMedium)
                for (player in players.filter { it.archived }) {
                    Row {
                        Text(player.name)
                        Icon(
                            Icons.Filled.Unarchive,
                            stringResource(
                                R.string.player_list_unarchive_player_icon_description,
                                player.name
                            ),
                            modifier = Modifier.clickable { onNavigateToPlayerUnarchive(player.id) })
                    }
                }
            }
        }
    }
}