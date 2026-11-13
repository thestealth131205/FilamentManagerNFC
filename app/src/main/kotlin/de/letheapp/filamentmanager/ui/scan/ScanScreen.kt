package de.letheapp.filamentmanager.ui.scan

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
 * Platzhalter — Kamera-Scan (Barcode/OCR) folgt gemäß PLAN.md "Nächste Schritte" 5,
 * da am meisten von echten Etikettenfotos abhängig (bewusst zuletzt umgesetzt).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.scan_title)) }) }) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.scan_placeholder))
        }
    }
}
