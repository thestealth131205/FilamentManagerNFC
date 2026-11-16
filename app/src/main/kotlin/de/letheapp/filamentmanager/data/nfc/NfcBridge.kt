package de.letheapp.filamentmanager.data.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import androidx.annotation.StringRes
import de.letheapp.filamentmanager.R
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

    /**
     * "Tools"-Modus (Zahnradmenü im NFC-Screen) – Funktionen jenseits des ACE-Filamentformats,
     * siehe PLAN.md "Passwortschutz": schreiben eine NDEF-URL bzw. verwalten NTAG-Passwortschutz
     * (Standard-NTAG-Feature, nicht ACE-spezifisch). Passwörter sind ASCII, rechtsseitig auf
     * genau 4 Byte mit 0x00 aufgefüllt bzw. abgeschnitten (siehe `String.toTagPassword()`).
     */
    data class WriteUrl(val url: String) : NfcMode

    /** Setzt PWD/PACK neu und aktiviert Schreibschutz (AUTH0) – erfordert einen aktuell entsperrten Tag. */
    data class LockTag(val password: ByteArray) : NfcMode

    /** Authentifiziert mit [password] und hebt den Schreibschutz wieder auf (AUTH0=0xFF). */
    data class UnlockTag(val password: ByteArray) : NfcMode

    /** Schreibt PWD/PACK ohne den Schreibschutz zu ändern – erfordert einen aktuell entsperrten Tag. */
    data class SetPassword(val password: ByteArray) : NfcMode

    /** Setzt PWD/PACK auf Werksvorgabe zurück – erfordert einen aktuell entsperrten Tag. */
    data object ClearPassword : NfcMode
}

/**
 * Lokalisierbarer Meldungstext ohne Context-Zugriff auf Bridge-/Codec-Ebene – die UI löst
 * [resId] mit [args] per `stringResource` in der aktuellen Systemsprache auf.
 */
data class NfcMessage(@StringRes val resId: Int, val args: List<String> = emptyList())

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

    /** Ergebnis einer Tools-Aktion ([NfcMode.WriteUrl]/[NfcMode.LockTag]/etc.) – [message] trägt den Erfolgstext. */
    data class ToolSuccess(val tagUid: String, val message: NfcMessage) : NfcEvent
    data class Error(val message: NfcMessage) : NfcEvent
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
    private val _ccWarning = MutableStateFlow<NfcMessage?>(null)
    val ccWarning: StateFlow<NfcMessage?> = _ccWarning

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
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_error_unsupported_tag))
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
                is NfcMode.WriteUrl -> handleWriteUrl(nfcA, uid, currentMode)
                is NfcMode.LockTag -> handleLockTag(nfcA, uid, currentMode)
                is NfcMode.UnlockTag -> handleUnlockTag(nfcA, uid, currentMode)
                is NfcMode.SetPassword -> handleSetPassword(nfcA, uid, currentMode)
                NfcMode.ClearPassword -> handleClearPassword(nfcA, uid)
                NfcMode.Idle -> Unit
            }
        } catch (e: Exception) {
            _event.value = NfcEvent.Error(
                NfcMessage(R.string.nfc_error_communication, listOf(e.message ?: e::class.simpleName.orEmpty())),
            )
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
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_error_same_chip, listOf(uid)))
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
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_error_verify_failed))
        }
    }

    private fun handleWriteUrl(nfcA: NfcA, uid: String, mode: NfcMode.WriteUrl) {
        val pages = try {
            NdefUriCodec.encodeUrlMessage(mode.url)
        } catch (e: NdefUriCodec.UrlTooLongException) {
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_url_too_long, listOf(e.maxUrlLength.toString())))
            return
        }
        AceTagIo.writePages(nfcA, pages)
        val verifyRaw = AceTagIo.readAllPages(nfcA)
        if (AceTagCodec.verify(verifyRaw, pages)) {
            _event.value = NfcEvent.ToolSuccess(uid, NfcMessage(R.string.nfc_tool_url_success))
        } else {
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_error_verify_failed))
        }
    }

    /**
     * Schreibreihenfolge wichtig: PWD/PACK/ACCESS zuerst, AUTH0 (Page [NtagType.cfgStartPage]) zuletzt –
     * so bleibt der Tag bis zum letzten Write unverändert "offen" (AUTH0=0xFF) und keiner dieser
     * vorbereitenden Writes braucht selbst schon eine Authentifizierung.
     */
    private fun handleLockTag(nfcA: NfcA, uid: String, mode: NfcMode.LockTag) {
        val result = runCatching {
            val type = AceTagIo.detectNtagType(nfcA)
            val cfgStart = type.cfgStartPage
            AceTagIo.writePage(nfcA, cfgStart + 2, mode.password)
            val currentPack = AceTagIo.readPage(nfcA, cfgStart + 3)
            AceTagIo.writePage(nfcA, cfgStart + 3, byteArrayOf(0, 0, currentPack[2], currentPack[3]))
            val currentAccess = AceTagIo.readPage(nfcA, cfgStart + 1)
            AceTagIo.writePage(nfcA, cfgStart + 1, byteArrayOf(0x00, currentAccess[1], currentAccess[2], currentAccess[3]))
            val currentCfg0 = AceTagIo.readPage(nfcA, cfgStart)
            AceTagIo.writePage(nfcA, cfgStart, byteArrayOf(currentCfg0[0], currentCfg0[1], currentCfg0[2], 0x04))
        }
        _event.value = if (result.isSuccess) {
            NfcEvent.ToolSuccess(uid, NfcMessage(R.string.nfc_tool_lock_success))
        } else {
            NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_locked_or_auth))
        }
    }

    private fun handleUnlockTag(nfcA: NfcA, uid: String, mode: NfcMode.UnlockTag) {
        if (!AceTagIo.authenticate(nfcA, mode.password)) {
            _event.value = NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_wrong_password))
            return
        }
        val result = runCatching {
            val cfgStart = AceTagIo.detectNtagType(nfcA).cfgStartPage
            val currentCfg0 = AceTagIo.readPage(nfcA, cfgStart)
            AceTagIo.writePage(nfcA, cfgStart, byteArrayOf(currentCfg0[0], currentCfg0[1], currentCfg0[2], 0xFF.toByte()))
        }
        _event.value = if (result.isSuccess) {
            NfcEvent.ToolSuccess(uid, NfcMessage(R.string.nfc_tool_unlock_success))
        } else {
            NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_locked_or_auth))
        }
    }

    private fun handleSetPassword(nfcA: NfcA, uid: String, mode: NfcMode.SetPassword) {
        val result = runCatching {
            val cfgStart = AceTagIo.detectNtagType(nfcA).cfgStartPage
            AceTagIo.writePage(nfcA, cfgStart + 2, mode.password)
            val currentPack = AceTagIo.readPage(nfcA, cfgStart + 3)
            AceTagIo.writePage(nfcA, cfgStart + 3, byteArrayOf(0, 0, currentPack[2], currentPack[3]))
        }
        _event.value = if (result.isSuccess) {
            NfcEvent.ToolSuccess(uid, NfcMessage(R.string.nfc_tool_set_password_success))
        } else {
            NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_locked_or_auth))
        }
    }

    private fun handleClearPassword(nfcA: NfcA, uid: String) {
        val result = runCatching {
            val cfgStart = AceTagIo.detectNtagType(nfcA).cfgStartPage
            AceTagIo.writePage(nfcA, cfgStart + 2, byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()))
            val currentPack = AceTagIo.readPage(nfcA, cfgStart + 3)
            AceTagIo.writePage(nfcA, cfgStart + 3, byteArrayOf(0, 0, currentPack[2], currentPack[3]))
        }
        _event.value = if (result.isSuccess) {
            NfcEvent.ToolSuccess(uid, NfcMessage(R.string.nfc_tool_clear_password_success))
        } else {
            NfcEvent.Error(NfcMessage(R.string.nfc_tool_error_locked_or_auth))
        }
    }
}
