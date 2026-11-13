package de.letheapp.filamentmanager.ui.nfc

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
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R
import de.letheapp.filamentmanager.data.db.FilamentEntity
import de.letheapp.filamentmanager.data.nfc.AceTagData
import de.letheapp.filamentmanager.ui.scan.ScanDraftHolder

/**
 * NFC lesen/schreiben gegen ACE-2-Pro-kompatible Tags (PLAN.md Schritt 3/4).
 * Hardware-Dispatch läuft über `MainActivity.onResume`
 * (`NfcAdapter.enableReaderMode` -> [de.letheapp.filamentmanager.data.nfc.NfcBridge]),
 * dieser Screen setzt nur den gewünschten Modus und reagiert auf das Ergebnis.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScreen(
    viewModel: NfcViewModel,
    onNavigateToEditor: (Long?) -> Unit,
) {
    var tab by remember { mutableStateOf(0) }
    val screenState by viewModel.screenState.collectAsState()

    // Verlässt der Nutzer den Screen (anderer Bottom-Tab), darf der Reader-Mode
    // nicht im "Schreiben"-Modus auf dem nächsten zufällig aufgelegten Tag landen.
    DisposableEffect(Unit) {
        onDispose { viewModel.reset() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.nfc_title)) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0; viewModel.reset() },
                    text = { Text(stringResource(R.string.nfc_tab_read)) },
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1; viewModel.reset() },
                    text = { Text(stringResource(R.string.nfc_tab_write)) },
                )
            }
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (tab == 0) {
                    ReadTab(
                        state = screenState,
                        onStartReading = viewModel::startReading,
                        onCancel = viewModel::reset,
                        onApply = { data, matchedId ->
                            if (matchedId != null) {
                                onNavigateToEditor(matchedId)
                            } else {
                                ScanDraftHolder.set(viewModel.draftFromReadResult(data))
                                onNavigateToEditor(null)
                            }
                            viewModel.reset()
                        },
                    )
                } else {
                    WriteTab(
                        state = screenState,
                        filaments = viewModel.filaments.collectAsState().value,
                        onSelectFilament = viewModel::startWriting,
                        onCancel = viewModel::reset,
                        onDone = viewModel::reset,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadTab(
    state: NfcScreenState,
    onStartReading: () -> Unit,
    onCancel: () -> Unit,
    onApply: (AceTagData, Long?) -> Unit,
) {
    when (state) {
        is NfcScreenState.WaitingToRead -> WaitingCard(
            text = stringResource(R.string.nfc_waiting_hold_tag),
            onCancel = onCancel,
        )
        is NfcScreenState.ReadEmpty -> MessageCard(
            title = stringResource(R.string.nfc_read_empty_title),
            body = stringResource(R.string.nfc_read_empty_body, state.tagUid),
        ) {
            Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.nfc_ok))
            }
        }
        is NfcScreenState.ReadResult -> ReadResultCard(state, onApply, onCancel)
        is NfcScreenState.Error -> ErrorCard(state.message, onCancel)
        else -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.nfc_read_intro), modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = onStartReading, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.nfc_read_start))
            }
        }
    }
}

@Composable
private fun WriteTab(
    state: NfcScreenState,
    filaments: List<FilamentEntity>,
    onSelectFilament: (FilamentEntity) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
) {
    when (state) {
        is NfcScreenState.WaitingToWrite -> WaitingCard(
            text = stringResource(R.string.nfc_waiting_write, "${state.filament.brand} – ${state.filament.type}"),
            onCancel = onCancel,
        )
        is NfcScreenState.WriteSuccess -> MessageCard(
            title = stringResource(R.string.nfc_write_success_title),
            body = stringResource(R.string.nfc_write_success_body, state.tagUid),
        ) {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.nfc_ok))
            }
        }
        is NfcScreenState.Error -> ErrorCard(state.message, onCancel)
        else -> Column(modifier = Modifier.fillMaxSize()) {
            Text(stringResource(R.string.nfc_write_intro), modifier = Modifier.padding(bottom = 12.dp))
            if (filaments.isEmpty()) {
                Text(stringResource(R.string.nfc_write_no_filaments))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filaments, key = { it.id }) { filament ->
                        FilamentPickRow(filament, onClick = { onSelectFilament(filament) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilamentPickRow(filament: FilamentEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
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
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            )
            Column {
                Text("${filament.brand} – ${filament.type}")
                Text(filament.colorName ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun WaitingCard(text: String, onCancel: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Nfc, contentDescription = null, modifier = Modifier.size(64.dp))
        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        Text(text, modifier = Modifier.padding(bottom = 24.dp))
        OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.nfc_cancel)) }
    }
}

@Composable
private fun MessageCard(title: String, body: String, actions: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Text(body, modifier = Modifier.padding(bottom = 24.dp))
        actions()
    }
}

@Composable
private fun ErrorCard(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.nfc_error_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(message, modifier = Modifier.padding(bottom = 24.dp))
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.nfc_ok))
        }
    }
}

@Composable
private fun ReadResultCard(
    state: NfcScreenState.ReadResult,
    onApply: (AceTagData, Long?) -> Unit,
    onCancel: () -> Unit,
) {
    val data = state.data
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.nfc_read_result_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(data.colorArgb))
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    )
                    Column {
                        Text(
                            if (data.brand.isNotBlank() || data.type.isNotBlank()) "${data.brand} – ${data.type}" else stringResource(R.string.nfc_read_result_unknown),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        if (data.sku.isNotBlank()) Text(data.sku, style = MaterialTheme.typography.bodySmall)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                InfoLine(stringResource(R.string.nfc_label_extruder), "${data.extruderTempMin}–${data.extruderTempMax} °C")
                InfoLine(stringResource(R.string.nfc_label_bed), "${data.hotbedTempMin}–${data.hotbedTempMax} °C")
                InfoLine(stringResource(R.string.nfc_label_diameter), "${data.diameterMm} mm")
                if (data.lengthM > 0) InfoLine(stringResource(R.string.nfc_label_length), "${data.lengthM} m")
                if (data.weightG > 0) InfoLine(stringResource(R.string.nfc_label_weight), "${data.weightG} g")
            }
        }
        if (state.matchedFilamentId != null) {
            Text(
                stringResource(R.string.nfc_read_result_matched),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
            )
        } else {
            Text(
                stringResource(R.string.nfc_read_result_new),
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
            )
        }
        Button(
            onClick = { onApply(data, state.matchedFilamentId) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.nfc_read_apply))
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(R.string.nfc_read_again))
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
