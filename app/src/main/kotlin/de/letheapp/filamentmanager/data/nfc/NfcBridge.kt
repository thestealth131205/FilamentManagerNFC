package de.letheapp.filamentmanager.data.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Was beim nächsten Tag-Kontakt passieren soll – von [de.letheapp.filamentmanager.ui.nfc.NfcViewModel] gesetzt. */
sealed interface NfcMode {
    data object Idle : NfcMode
    data object Reading : NfcMode
    data class Writing(val tagData: AceTagData, val filamentId: Long) : NfcMode
}

/** Ergebnis eines Tag-Kontakts – wird von der UI konsumiert und nach Verarbeitung zurück auf [Idle] gesetzt. */
sealed interface NfcEvent {
    data object Idle : NfcEvent
    data class ReadEmpty(val tagUid: String) : NfcEvent
    data class ReadSuccess(val tagUid: String, val data: AceTagData) : NfcEvent
    data class WriteSuccess(val tagUid: String, val filamentId: Long) : NfcEvent
    data class Error(val message: String) : NfcEvent
}

/**
 * Verbindet [android.nfc.NfcAdapter.enableReaderMode] (in `MainActivity`, läuft auf einem
 * Binder-Thread, nicht dem UI-Thread) mit der Compose-Welt. Reines Objekt statt DI, analog
 * zu `ScanDraftHolder` – hält nur Zustand, keine eigene Geschäftslogik/DB-Zugriffe.
 */
object NfcBridge {
    private val _mode = MutableStateFlow<NfcMode>(NfcMode.Idle)
    val mode: StateFlow<NfcMode> = _mode

    private val _event = MutableStateFlow<NfcEvent>(NfcEvent.Idle)
    val event: StateFlow<NfcEvent> = _event

    /**
     * Von `MainActivity` gesetzt: schaltet den Reader-Mode kurz aus/ein. Nötig, weil Android
     * den Discover-Callback nur bei neu ins Feld kommenden Tags auslöst – liegt der Tag nach
     * einem Schreibvorgang unverändert auf dem Gerät (z.B. direkt danach lesen, ohne ihn
     * abzuheben), feuert sonst kein neuer Event und der nächste Modus wartet ewig.
     */
    private var readerResetCallback: (() -> Unit)? = null

    fun registerReaderResetCallback(callback: (() -> Unit)?) {
        readerResetCallback = callback
    }

    fun setMode(mode: NfcMode) {
        _mode.value = mode
        _event.value = NfcEvent.Idle
        if (mode !is NfcMode.Idle) readerResetCallback?.invoke()
    }

    fun clearEvent() {
        _event.value = NfcEvent.Idle
    }

    /** Wird von `MainActivity`s `ReaderCallback` für jeden erkannten Tag aufgerufen. */
    fun onTagDiscovered(tag: Tag) {
        val currentMode = _mode.value
        if (currentMode is NfcMode.Idle) return

        val nfcA = NfcA.get(tag)
        if (nfcA == null) {
            _event.value = NfcEvent.Error("Tag-Typ nicht unterstützt (kein NFC-A/NTAG-Tag).")
            return
        }
        val uid = tag.id.joinToString("") { "%02X".format(it) }
        try {
            nfcA.connect()
            nfcA.timeout = 2000
            when (currentMode) {
                is NfcMode.Reading -> handleRead(nfcA, uid)
                is NfcMode.Writing -> handleWrite(nfcA, uid, currentMode)
                NfcMode.Idle -> Unit
            }
        } catch (e: Exception) {
            _event.value = NfcEvent.Error("Fehler bei der Tag-Kommunikation: ${e.message ?: e::class.simpleName}")
        } finally {
            runCatching { if (nfcA.isConnected) nfcA.close() }
        }
    }

    private fun handleRead(nfcA: NfcA, uid: String) {
        val raw = AceTagIo.readAllPages(nfcA)
        if (AceTagCodec.isEmptyTag(raw)) {
            _event.value = NfcEvent.ReadEmpty(uid)
        } else {
            _event.value = NfcEvent.ReadSuccess(uid, AceTagCodec.decode(raw))
        }
    }

    private fun handleWrite(nfcA: NfcA, uid: String, mode: NfcMode.Writing) {
        val pages = AceTagCodec.encode(mode.tagData)
        AceTagIo.writePages(nfcA, pages)
        // Lesevergleich zur Verifikation (PLAN.md Schritt 4) – direkt danach erneut lesen
        // und Byte für Byte mit den geschriebenen Pages abgleichen.
        val verifyRaw = AceTagIo.readAllPages(nfcA)
        if (AceTagCodec.verify(verifyRaw, pages)) {
            _event.value = NfcEvent.WriteSuccess(uid, mode.filamentId)
        } else {
            _event.value = NfcEvent.Error(
                "Schreiben unvollständig – Lesevergleich stimmt nicht überein. Tag erneut auflegen und wiederholen.",
            )
        }
    }
}
