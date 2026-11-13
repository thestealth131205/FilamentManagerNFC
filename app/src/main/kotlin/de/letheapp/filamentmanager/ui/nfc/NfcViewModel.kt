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
    data class WaitingToWrite(val filament: FilamentEntity) : NfcScreenState
    data class WriteSuccess(val tagUid: String, val filament: FilamentEntity) : NfcScreenState
    data class Error(val message: String) : NfcScreenState
}

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

    init {
        viewModelScope.launch {
            NfcBridge.event.collect { event -> handleBridgeEvent(event) }
        }
    }

    fun startReading() {
        _screenState.value = NfcScreenState.WaitingToRead
        NfcBridge.setMode(NfcMode.Reading)
    }

    fun startWriting(filament: FilamentEntity) {
        _screenState.value = NfcScreenState.WaitingToWrite(filament)
        NfcBridge.setMode(NfcMode.Writing(filament.toAceTagData(), filament.id))
    }

    /** Zurück zur Auswahl/Startansicht, Reader-Mode wieder in den Leerlauf versetzen. */
    fun reset() {
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
                val matched = event.data.sku.takeIf { it.isNotBlank() }?.let { dao.findBySku(it) }
                _screenState.value = NfcScreenState.ReadResult(event.tagUid, event.data, matched?.id)
                NfcBridge.clearEvent()
            }
            is NfcEvent.WriteSuccess -> viewModelScope.launch {
                dao.logNfcWrite(NfcWriteLogEntity(tagUid = event.tagUid, filamentId = event.filamentId))
                val filament = dao.getById(event.filamentId)
                if (filament != null) {
                    _screenState.value = NfcScreenState.WriteSuccess(event.tagUid, filament)
                }
                NfcBridge.clearEvent()
            }
            is NfcEvent.Error -> {
                _screenState.value = NfcScreenState.Error(event.message)
                NfcBridge.clearEvent()
            }
        }
    }
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
