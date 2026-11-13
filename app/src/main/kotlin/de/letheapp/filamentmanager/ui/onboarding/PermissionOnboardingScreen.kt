package de.letheapp.filamentmanager.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R

/**
 * Wird einmalig beim ersten App-Start gezeigt (siehe [de.letheapp.filamentmanager.data.prefs.OnboardingPrefs]):
 * erklärt, wofür Kamera- und NFC-Zugriff gebraucht werden, bevor die eigentliche
 * Berechtigungsabfrage (Kamera) ausgelöst wird.
 */
@Composable
fun PermissionOnboardingScreen(
    hasNfc: Boolean,
    onContinue: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.onboarding_intro),
                style = MaterialTheme.typography.bodyMedium,
            )

            PermissionExplanationCard(
                icon = Icons.Filled.PhotoCamera,
                title = stringResource(R.string.onboarding_camera_title),
                description = stringResource(R.string.onboarding_camera_desc),
            )
            PermissionExplanationCard(
                icon = Icons.Filled.Nfc,
                title = stringResource(R.string.onboarding_nfc_title),
                description = stringResource(
                    if (hasNfc) R.string.onboarding_nfc_desc else R.string.onboarding_nfc_desc_missing,
                ),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.onboarding_continue))
            }
        }
    }
}

@Composable
private fun PermissionExplanationCard(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icon, contentDescription = null)
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
