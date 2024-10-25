package net.clahey.golfscore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import net.clahey.golfscore.data.database.Player
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
    val uiState by playerListViewModel.uiState.collectAsState()
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateToPlayerAdd() }) {
            Icon(Icons.Filled.Add, stringResource(R.string.player_list_add_player_icon_description))
        }
    }, topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = { Text(stringResource(R.string.player_list_page_title)) }, navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.generic_back_icon_description)
                )
            }
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ListCard {
                    items(players.filter { !it.archived }) { player, padding ->
                        Row(Modifier.padding(padding)) {
                            Text(
                                player.name,
                                Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(1f)
                            )
                            IconButton(
                                onClick = { onNavigateToPlayerEdit(player.id) },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    Icons.Filled.Edit, stringResource(
                                        R.string.player_list_edit_player_icon_description,
                                        player.name
                                    )
                                )
                            }
                            IconButton(
                                onClick = { onNavigateToPlayerArchive(player.id) },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    Icons.Filled.Archive, stringResource(
                                        R.string.player_list_archive_player_icon_description,
                                        player.name
                                    )
                                )
                            }
                        }
                    }
                }
                ArchivedPlayers(
                    players.filter { it.archived },
                    uiState.archiveExpanded,
                    playerListViewModel::setArchiveExpanded,
                    Modifier,
                    onNavigateToPlayerUnarchive
                )
            }
        }
    }
}

@Composable
internal fun ArchivedPlayers(
    archivedPlayers: List<Player>,
    archiveExpanded: Boolean,
    setArchiveExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToPlayerUnarchive: (Int) -> Unit,
) {
    if (archivedPlayers.isNotEmpty()) {
        ListCard(modifier) {
            item { padding ->
                Row(
                    Modifier
                        .padding(padding)
                        .clickable {
                            setArchiveExpanded(!archiveExpanded)
                        }) {
                    Text(
                        stringResource(R.string.player_list_archived_header),
                        Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton({ setArchiveExpanded(!archiveExpanded) }) {
                        if (!archiveExpanded) {
                            Icon(
                                Icons.Filled.ExpandMore,
                                stringResource(R.string.player_list_show_archived_icon_description)
                            )
                        } else {
                            Icon(
                                Icons.Filled.ExpandLess,
                                stringResource(R.string.player_list_hide_archived_icon_description)
                            )
                        }
                    }
                }
            }

            if (archiveExpanded) {
                items(archivedPlayers) { player, padding ->
                    Row(Modifier.padding(padding)) {
                        Text(
                            player.name,
                            Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                        IconButton(onClick = {
                            onNavigateToPlayerUnarchive(player.id)
                        }) {
                            Icon(
                                Icons.Filled.Unarchive, stringResource(
                                    R.string.player_list_unarchive_player_icon_description,
                                    player.name
                                ), Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}