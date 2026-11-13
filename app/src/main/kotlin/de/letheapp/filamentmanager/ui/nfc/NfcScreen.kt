package de.letheapp.filamentmanager.ui.nfc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R

/**
 * Platzhalter — NFC-Lesen/Schreiben folgt gemäß PLAN.md "Nächste Schritte" 3/4,
 * sobald ein echter ACE-2-Pro-Tag zum Verifizieren vorliegt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.nfc_title)) }) }) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.nfc_placeholder))
        }
    }
}
