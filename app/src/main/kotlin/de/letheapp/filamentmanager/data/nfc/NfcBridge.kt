package de.letheapp.filamentmanager.data.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Was beim nächsten Tag-Kontakt passieren soll – von [de.letheapp.filamentmanager.ui.nfc.NfcViewModel] gesetzt.
 *
 * [Writing.copyIndex]/[Writing.totalCopies] und [Writing.previousTagUids] tragen den optionalen
 * "2 Chips identisch beschreiben"-Modus (Spule hat je nach Einlegerichtung im ACE 2 Pro einen Chip
 * links oder rechts vom Lesekopf, beide Chips müssen dieselben Daten tragen): bei [totalCopies] == 2
 * läuft der zweite Schreibvorgang mit denselben [AceTagData], aber [previousTagUids] enthält die UID
 * des ersten Chips, damit [NfcBridge] ein versehentliches zweites Beschreiben desselben physischen
 * Chips erkennt und ablehnt statt den zweiten "Chip" nur zu simulieren.
 */
sealed interface NfcMode {
    data object Idle : NfcMode
    data object Reading : NfcMode
    data class Writing(
        val tagData: AceTagData,
        val filamentId: Long,
        val copyIndex: Int = 1,
        val totalCopies: Int = 1,
        val previousTagUids: Set<String> = emptySet(),
    ) : NfcMode
}

/** Ergebnis eines Tag-Kontakts – wird von der UI konsumiert und nach Verarbeitung zurück auf [Idle] gesetzt. */
sealed interface NfcEvent {
    data object Idle : NfcEvent
    data class ReadEmpty(val tagUid: String) : NfcEvent
    data class ReadSuccess(val tagUid: String, val data: AceTagData) : NfcEvent
    data class WriteSuccess(
        val tagUid: String,
        val filamentId: Long,
        val copyIndex: Int = 1,
        val totalCopies: Int = 1,
    ) : NfcEvent
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

    /** Nicht-blockierende Warnung zu Page 3 (Capability Container) des zuletzt kontaktierten Tags. */
    private val _ccWarning = MutableStateFlow<String?>(null)
    val ccWarning: StateFlow<String?> = _ccWarning

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
        _ccWarning.value = null
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
            _ccWarning.value = runCatching { AceTagIo.readCapabilityContainer(nfcA) }
                .getOrNull()
                ?.let { AceTagCodec.ccMismatchWarning(it) }
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
        if (uid in mode.previousTagUids) {
            _event.value = NfcEvent.Error(
                "Das ist derselbe Chip wie beim vorherigen Schreibvorgang (UID $uid). " +
                    "Für den zweiten Chip einen anderen, physisch getrennten Tag auflegen.",
            )
            return
        }
        val pages = AceTagCodec.encode(mode.tagData)
        AceTagIo.writePages(nfcA, pages)
        // Lesevergleich zur Verifikation (PLAN.md Schritt 4) – direkt danach erneut lesen
        // und Byte für Byte mit den geschriebenen Pages abgleichen.
        val verifyRaw = AceTagIo.readAllPages(nfcA)
        if (AceTagCodec.verify(verifyRaw, pages)) {
            _event.value = NfcEvent.WriteSuccess(uid, mode.filamentId, mode.copyIndex, mode.totalCopies)
        } else {
            _event.value = NfcEvent.Error(
                "Schreiben unvollständig – Lesevergleich stimmt nicht überein. Tag erneut auflegen und wiederholen.",
            )
        }
    }
}
