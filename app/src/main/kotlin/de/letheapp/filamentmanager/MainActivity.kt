package de.letheapp.filamentmanager

import android.Manifest
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.letheapp.filamentmanager.data.nfc.NfcBridge
import de.letheapp.filamentmanager.data.prefs.OnboardingPrefs
import de.letheapp.filamentmanager.ui.AppNavigation
import de.letheapp.filamentmanager.ui.onboarding.PermissionOnboardingScreen
import de.letheapp.filamentmanager.ui.theme.FilamentManagerTheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this
        val database = (application as FilamentManagerApp).database
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val hasNfc = nfcAdapter != null

        setContent {
            FilamentManagerTheme {
                var onboardingDone by remember {
                    mutableStateOf(OnboardingPrefs.hasSeenPermissionOnboarding(context))
                }
                val requestCameraPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) {
                    // Ergebnis (erlaubt/abgelehnt) wird direkt beim Kamera-Scan-Versuch
                    // erneut geprüft — hier zählt nur, dass die Abfrage einmalig lief.
                    OnboardingPrefs.setSeenPermissionOnboarding(context)
                    onboardingDone = true
                }

                if (onboardingDone) {
                    AppNavigation(database = database)
                } else {
                    PermissionOnboardingScreen(
                        hasNfc = hasNfc,
                        onContinue = { requestCameraPermission.launch(Manifest.permission.CAMERA) },
                    )
                }
            }
        }
    }

    // Reader-Mode statt klassischem Foreground-Dispatch: einfacherer Lifecycle,
    // läuft auf einem Binder-Thread (nicht dem UI-Thread) -> siehe NfcBridge.
    private fun registerReaderMode() {
        nfcAdapter?.disableReaderMode(this)
        nfcAdapter?.enableReaderMode(
            this,
            { tag -> NfcBridge.onTagDiscovered(tag) },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null,
        )
    }

    override fun onResume() {
        super.onResume()
        NfcBridge.registerReaderResetCallback { registerReaderMode() }
        registerReaderMode()
    }

    override fun onPause() {
        super.onPause()
        NfcBridge.registerReaderResetCallback(null)
        nfcAdapter?.disableReaderMode(this)
    }
}
