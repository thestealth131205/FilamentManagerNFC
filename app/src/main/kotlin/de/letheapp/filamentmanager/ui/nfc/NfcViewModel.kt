package de.letheapp.filamentmanager.ui.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.letheapp.filamentmanager.data.catalog.toCatalogHex
import de.letheapp.filamentmanager.data.db.AppDatabase
import de.letheapp.filamentmanager.data.db.FilamentEntity
import de.letheapp.filamentmanager.data.db.NfcWriteLogEntity
import de.letheapp.filamentmanager.data.nfc.AceTagData
import de.letheapp.filamentmanager.data.nfc.NfcBridge
import de.letheapp.filamentmanager.data.nfc.NfcEvent
import de.letheapp.filamentmanager.data.nfc.NfcMessage
import de.letheapp.filamentmanager.data.nfc.NfcMode
import de.letheapp.filamentmanager.ui.editor.FilamentFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface NfcScreenState {
    data object Idle : NfcScreenState
    data object WaitingToRead : NfcScreenState
    data class ReadEmpty(val tagUid: String) : NfcScreenState
    data class ReadResult(val tagUid: String, val data: AceTagData, val matchedFilamentId: Long?) : NfcScreenState
    data class WaitingToWrite(val filament: FilamentEntity, val copyIndex: Int = 1, val totalCopies: Int = 1) : NfcScreenState

    /** Erster von zwei Chips erfolgreich beschrieben – wartet auf Bestätigung, bevor der zweite Chip angefordert wird. */
    data class FirstTagWritten(val filament: FilamentEntity, val firstTagUid: String, val totalCopies: Int) : NfcScreenState
    data class WriteSuccess(val tagUids: List<String>, val filament: FilamentEntity) : NfcScreenState

    /** Wartet auf den Tag-Kontakt für eine Tools-Aktion ([ToolAction]) – siehe [de.letheapp.filamentmanager.data.nfc.NfcMode]. */
    data class WaitingTool(val action: ToolAction) : NfcScreenState
    data class ToolActionDone(val message: NfcMessage) : NfcScreenState
    data class Error(val message: NfcMessage) : NfcScreenState
}

/** Welche Tools-Aktion gerade auf einen Tag wartet – bestimmt den Warte-Text im Screen. */
enum class ToolAction { WRITE_URL, LOCK, UNLOCK, SET_PASSWORD, CLEAR_PASSWORD }

/**
 * Bindeglied zwischen [NfcBridge] (reine Hardware-/Byte-Ebene) und der Datenbank:
 * SKU-Abgleich beim Lesen, Schreib-Log beim Schreiben (PLAN.md "NfcWriteLog").
 */
class NfcViewModel(private val database: AppDatabase) : ViewModel() {

    private val dao = database.filamentDao()

    val filaments: StateFlow<List<FilamentEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _screenState = MutableStateFlow<NfcScreenState>(NfcScreenState.Idle)
    val screenState: StateFlow<NfcScreenState> = _screenState

    /** UID des ersten Chips im "2 Chips identisch beschreiben"-Modus, bis der zweite fertig ist. */
    private var firstTagUidOfCurrentWrite: String? = null

    /** Nicht-blockierende Capability-Container-Warnung des zuletzt kontaktierten Tags. */
    val ccWarning: StateFlow<NfcMessage?> = NfcBridge.ccWarning

    init {
        viewModelScope.launch {
            NfcBridge.event.collect { event -> handleBridgeEvent(event) }
        }
    }

    fun startReading() {
        _screenState.value = NfcScreenState.WaitingToRead
        NfcBridge.setMode(NfcMode.Reading)
    }

    /**
     * [writeTwoTags]: optional für Spulen, bei denen der RFID-Chip je nach Einlegerichtung im
     * ACE 2 Pro links oder rechts vom Lesekopf liegt – schreibt dieselben Daten danach auf einen
     * zweiten, physisch getrennten Chip (siehe [continueWithSecondTag]), statt nur einen zu beschreiben.
     */
    fun startWriting(filament: FilamentEntity, writeTwoTags: Boolean = false) {
        val totalCopies = if (writeTwoTags) 2 else 1
        _screenState.value = NfcScreenState.WaitingToWrite(filament, copyIndex = 1, totalCopies = totalCopies)
        NfcBridge.setMode(NfcMode.Writing(filament.toAceTagData(), filament.id, copyIndex = 1, totalCopies = totalCopies))
    }

    /** Nutzer hat den ersten Chip abgelegt/den zweiten aufgelegt – fordert den zweiten Schreibvorgang an. */
    fun continueWithSecondTag(state: NfcScreenState.FirstTagWritten) {
        _screenState.value = NfcScreenState.WaitingToWrite(state.filament, copyIndex = 2, totalCopies = state.totalCopies)
        NfcBridge.setMode(
            NfcMode.Writing(
                tagData = state.filament.toAceTagData(),
                filamentId = state.filament.id,
                copyIndex = 2,
                totalCopies = state.totalCopies,
                previousTagUids = setOf(state.firstTagUid),
            ),
        )
    }

    /** Nutzer bricht nach dem ersten Chip ab – nur dieser eine Tag zählt als Ergebnis. */
    fun finishWithFirstTagOnly(state: NfcScreenState.FirstTagWritten) {
        firstTagUidOfCurrentWrite = null
        _screenState.value = NfcScreenState.WriteSuccess(listOf(state.firstTagUid), state.filament)
        NfcBridge.setMode(NfcMode.Idle)
    }

    /** Schreibt beim nächsten Tag-Kontakt eine NDEF-URL, die sich beim Lesen automatisch öffnet ("Tools"-Modus). */
    fun writeUrl(url: String) {
        _screenState.value = NfcScreenState.WaitingTool(ToolAction.WRITE_URL)
        NfcBridge.setMode(NfcMode.WriteUrl(url))
    }

    /** Aktiviert den NTAG-Schreibschutz mit [password] (ASCII, auf 4 Byte aufgefüllt/gekürzt). */
    fun lockTag(password: String) {
        _screenState.value = NfcScreenState.WaitingTool(ToolAction.LOCK)
        NfcBridge.setMode(NfcMode.LockTag(password.toTagPassword()))
    }

    /** Hebt den Schreibschutz wieder auf – [password] muss das aktuell gültige Passwort sein. */
    fun unlockTag(password: String) {
        _screenState.value = NfcScreenState.WaitingTool(ToolAction.UNLOCK)
        NfcBridge.setMode(NfcMode.UnlockTag(password.toTagPassword()))
    }

    /** Hinterlegt ein neues Passwort, ohne den Schreibschutz zu aktivieren – Tag muss aktuell entsperrt sein. */
    fun setPassword(password: String) {
        _screenState.value = NfcScreenState.WaitingTool(ToolAction.SET_PASSWORD)
        NfcBridge.setMode(NfcMode.SetPassword(password.toTagPassword()))
    }

    /** Setzt das Passwort auf Werksvorgabe zurück – Tag muss aktuell entsperrt sein. */
    fun clearPassword() {
        _screenState.value = NfcScreenState.WaitingTool(ToolAction.CLEAR_PASSWORD)
        NfcBridge.setMode(NfcMode.ClearPassword)
    }

    /** Zurück zur Auswahl/Startansicht, Reader-Mode wieder in den Leerlauf versetzen. */
    fun reset() {
        firstTagUidOfCurrentWrite = null
        _screenState.value = NfcScreenState.Idle
        NfcBridge.setMode(NfcMode.Idle)
    }

    /** Baut aus einem gelesenen Tag ohne SKU-Treffer einen Editor-Entwurf (neue Spule). */
    fun draftFromReadResult(data: AceTagData): FilamentFormState = FilamentFormState(
        sku = data.sku,
        brand = data.brand,
        type = data.type,
        colorHex = toCatalogHex(data.colorArgb),
        diameterMm = data.diameterMm.takeIf { it > 0f }?.toString() ?: "1.75",
        spoolWeightG = data.weightG.takeIf { it > 0 }?.toString().orEmpty(),
        lengthTotalM = data.lengthM.takeIf { it > 0 }?.toString().orEmpty(),
        extruderTempMin = data.extruderTempMin.takeIf { it > 0 }?.toString().orEmpty(),
        extruderTempMax = data.extruderTempMax.takeIf { it > 0 }?.toString().orEmpty(),
        hotbedTempMin = data.hotbedTempMin.takeIf { it > 0 }?.toString().orEmpty(),
        hotbedTempMax = data.hotbedTempMax.takeIf { it > 0 }?.toString().orEmpty(),
    )

    private fun handleBridgeEvent(event: NfcEvent) {
        when (event) {
            NfcEvent.Idle -> Unit
            is NfcEvent.ReadEmpty -> {
                _screenState.value = NfcScreenState.ReadEmpty(event.tagUid)
                NfcBridge.clearEvent()
            }
            is NfcEvent.ReadSuccess -> viewModelScope.launch {
                // Zuerst über die zuletzt auf diese UID geschriebene Spule abgleichen – die SKU
                // ist kein Pflichtfeld und bei den meisten Spulen leer, dann greift nur der
                // SKU-Abgleich nicht, obwohl die Spule über diesen Tag bekannt ist.
                val matchedId = dao.findFilamentIdByTagUid(event.tagUid)?.takeIf { dao.getById(it) != null }
                    ?: event.data.sku.takeIf { it.isNotBlank() }?.let { dao.findBySku(it)?.id }
                _screenState.value = NfcScreenState.ReadResult(event.tagUid, event.data, matchedId)
                NfcBridge.clearEvent()
            }
            is NfcEvent.WriteSuccess -> viewModelScope.launch {
                dao.logNfcWrite(NfcWriteLogEntity(tagUid = event.tagUid, filamentId = event.filamentId))
                val filament = dao.getById(event.filamentId)
                if (filament != null) {
                    _screenState.value = if (event.copyIndex < event.totalCopies) {
                        // Erster von zwei Chips fertig – nicht sofort den zweiten Schreibvorgang
                        // scharf schalten, sonst würde der noch aufliegende erste Tag sofort erneut
                        // erkannt. Wartet auf explizite Bestätigung über continueWithSecondTag().
                        firstTagUidOfCurrentWrite = event.tagUid
                        NfcScreenState.FirstTagWritten(filament, event.tagUid, event.totalCopies)
                    } else {
                        val allUids = listOfNotNull(firstTagUidOfCurrentWrite) + event.tagUid
                        firstTagUidOfCurrentWrite = null
                        NfcScreenState.WriteSuccess(allUids, filament)
                    }
                }
                NfcBridge.clearEvent()
            }
            is NfcEvent.ToolSuccess -> {
                _screenState.value = NfcScreenState.ToolActionDone(event.message)
                NfcBridge.clearEvent()
            }
            is NfcEvent.Error -> {
                _screenState.value = NfcScreenState.Error(event.message)
                NfcBridge.clearEvent()
            }
        }
    }
}

/** ASCII-Passwort auf genau 4 Byte normiert (gekürzt bzw. rechtsseitig mit 0x00 aufgefüllt). */
private fun String.toTagPassword(): ByteArray {
    val bytes = ByteArray(4)
    val src = toByteArray(Charsets.US_ASCII)
    System.arraycopy(src, 0, bytes, 0, minOf(src.size, 4))
    return bytes
}

private fun FilamentEntity.toAceTagData() = AceTagData(
    sku = sku.orEmpty(),
    brand = brand,
    type = type,
    colorArgb = colorArgb,
    extruderTempMin = extruderTempMin ?: 0,
    extruderTempMax = extruderTempMax ?: 0,
    hotbedTempMin = hotbedTempMin ?: 0,
    hotbedTempMax = hotbedTempMax ?: 0,
    diameterMm = diameterMm,
    lengthM = (lengthTotalM ?: 0f).toInt(),
    weightG = spoolWeightG ?: 0,
)
