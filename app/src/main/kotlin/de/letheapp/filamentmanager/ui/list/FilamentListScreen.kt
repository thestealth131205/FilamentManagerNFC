package de.letheapp.filamentmanager.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R
import de.letheapp.filamentmanager.data.db.FilamentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListScreen(
    viewModel: FilamentListViewModel,
    onAddFilament: () -> Unit,
    onOpenFilament: (Long) -> Unit,
) {
    val filaments by viewModel.filtered.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringRes(R.string.list_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddFilament) {
                Icon(Icons.Filled.Add, contentDescription = stringRes(R.string.list_add))
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onSearchChanged,
                label = { Text(stringRes(R.string.list_search_hint)) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (filaments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringRes(R.string.list_empty))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filaments, key = { it.id }) { filament ->
                        FilamentRow(filament, onClick = { onOpenFilament(filament.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilamentRow(filament: FilamentEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(filament.colorArgb))
                    .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, CircleShape),
            )
            Column {
                Text("${filament.brand} – ${filament.type}")
                Text(filament.colorName ?: "")
                filament.spoolWeightG?.let { Text("$it g") }
            }
        }
    }
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id)
