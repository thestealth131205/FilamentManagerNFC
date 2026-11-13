package de.letheapp.filamentmanager.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.letheapp.filamentmanager.data.db.AppDatabase
import de.letheapp.filamentmanager.data.db.FilamentColorEntity
import de.letheapp.filamentmanager.data.db.FilamentEntity
import de.letheapp.filamentmanager.data.db.FilamentTypeEntity
import de.letheapp.filamentmanager.data.db.ManufacturerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Formular-Zustand für eine Spule. Enthält bewusst nur die Kernfelder aus
 * PLAN.md "Datenmodell" als Strings (Validierung/Parsing erst bei Speichern) –
 * Erweiterung um Slicer-Felder ist vorbereitet, aber als Zweitschritt gedacht.
 */
data class FilamentFormState(
    val id: Long = 0,
    val sku: String = "",
    val brand: String = "",
    val type: String = "",
    val colorName: String = "",
    val diameterMm: String = "1.75",
    val spoolWeightG: String = "",
    val lengthTotalM: String = "",
    val lengthRemainingM: String = "",
    val extruderTempMin: String = "",
    val extruderTempMax: String = "",
    val hotbedTempMin: String = "",
    val hotbedTempMax: String = "",
    val density: String = "",
    val flowRatePercent: String = "",
    val retractionDistanceMm: String = "",
    val retractionSpeedMmS: String = "",
    val fanSpeedPercent: String = "",
    val printSpeedMmS: String = "",
    val purchaseUrl: String = "",
    val pricePerKg: String = "",
    val notes: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis(),
)

class FilamentEditorViewModel(
    private val database: AppDatabase,
    filamentId: Long?,
) : ViewModel() {

    private val dao = database.filamentDao()
    private val manufacturerDao = database.manufacturerDao()
    private val typeDao = database.filamentTypeDao()
    private val colorDao = database.filamentColorDao()

    val form = MutableStateFlow(FilamentFormState())

    val manufacturerTop3: StateFlow<List<String>> = manufacturerDao.observeTop3()
        .map { it.map(ManufacturerEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val manufacturerAllAlpha: StateFlow<List<String>> = manufacturerDao.observeAllAlpha()
        .map { it.map(ManufacturerEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val typeTop3: StateFlow<List<String>> = typeDao.observeTop3()
        .map { it.map(FilamentTypeEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val typeAllAlpha: StateFlow<List<String>> = typeDao.observeAllAlpha()
        .map { it.map(FilamentTypeEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val colorTop3: StateFlow<List<String>> = colorDao.observeTop3()
        .map { it.map(FilamentColorEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val colorAllAlpha: StateFlow<List<String>> = colorDao.observeAllAlpha()
        .map { it.map(FilamentColorEntity::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        if (filamentId != null) {
            viewModelScope.launch {
                dao.getById(filamentId)?.let { form.value = it.toFormState() }
            }
        }
    }

    fun update(transform: (FilamentFormState) -> FilamentFormState) {
        form.value = transform(form.value)
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val state = form.value
            markUsed(manufacturerDao::findByName, manufacturerDao::markUsed, manufacturerDao::insert, state.brand) { ManufacturerEntity(name = it) }
            markUsed(typeDao::findByName, typeDao::markUsed, typeDao::insert, state.type) { FilamentTypeEntity(name = it) }
            if (state.colorName.isNotBlank()) {
                markUsed(colorDao::findByName, colorDao::markUsed, colorDao::insert, state.colorName) { FilamentColorEntity(name = it) }
            }
            dao.upsert(state.toEntity())
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val state = form.value
            if (state.id != 0L) {
                dao.getById(state.id)?.let { dao.delete(it) }
            }
            onDeleted()
        }
    }

    private suspend inline fun <T> markUsed(
        findByName: suspend (String) -> T?,
        crossinline markUsedFn: suspend (Long) -> Unit,
        crossinline insert: suspend (T) -> Long,
        name: String,
        crossinline create: (String) -> T,
    ) {
        if (name.isBlank()) return
        val existing = findByName(name)
        val id = if (existing != null) {
            idOf(existing)
        } else {
            insert(create(name))
        }
        markUsedFn(id)
    }

    private fun idOf(entry: Any): Long = when (entry) {
        is ManufacturerEntity -> entry.id
        is FilamentTypeEntity -> entry.id
        is FilamentColorEntity -> entry.id
        else -> 0
    }
}

private fun FilamentEntity.toFormState() = FilamentFormState(
    id = id,
    sku = sku.orEmpty(),
    brand = brand,
    type = type,
    colorName = colorName.orEmpty(),
    diameterMm = diameterMm.toString(),
    spoolWeightG = spoolWeightG?.toString().orEmpty(),
    lengthTotalM = lengthTotalM?.toString().orEmpty(),
    lengthRemainingM = lengthRemainingM?.toString().orEmpty(),
    extruderTempMin = extruderTempMin?.toString().orEmpty(),
    extruderTempMax = extruderTempMax?.toString().orEmpty(),
    hotbedTempMin = hotbedTempMin?.toString().orEmpty(),
    hotbedTempMax = hotbedTempMax?.toString().orEmpty(),
    density = density?.toString().orEmpty(),
    flowRatePercent = flowRatePercent?.toString().orEmpty(),
    retractionDistanceMm = retractionDistanceMm?.toString().orEmpty(),
    retractionSpeedMmS = retractionSpeedMmS?.toString().orEmpty(),
    fanSpeedPercent = fanSpeedPercent?.toString().orEmpty(),
    printSpeedMmS = printSpeedMmS?.toString().orEmpty(),
    purchaseUrl = purchaseUrl.orEmpty(),
    pricePerKg = pricePerKg?.toString().orEmpty(),
    notes = notes.orEmpty(),
    createdAtEpochMs = createdAtEpochMs,
)

private fun FilamentFormState.toEntity() = FilamentEntity(
    id = id,
    sku = sku.ifBlank { null },
    brand = brand,
    type = type,
    colorArgb = 0xFF000000.toInt(),
    colorName = colorName.ifBlank { null },
    diameterMm = diameterMm.toFloatOrNull() ?: 1.75f,
    spoolWeightG = spoolWeightG.toIntOrNull(),
    lengthTotalM = lengthTotalM.toFloatOrNull(),
    lengthRemainingM = lengthRemainingM.toFloatOrNull(),
    extruderTempMin = extruderTempMin.toIntOrNull(),
    extruderTempMax = extruderTempMax.toIntOrNull(),
    hotbedTempMin = hotbedTempMin.toIntOrNull(),
    hotbedTempMax = hotbedTempMax.toIntOrNull(),
    density = density.toFloatOrNull(),
    flowRatePercent = flowRatePercent.toIntOrNull(),
    retractionDistanceMm = retractionDistanceMm.toFloatOrNull(),
    retractionSpeedMmS = retractionSpeedMmS.toFloatOrNull(),
    fanSpeedPercent = fanSpeedPercent.toIntOrNull(),
    printSpeedMmS = printSpeedMmS.toIntOrNull(),
    purchaseUrl = purchaseUrl.ifBlank { null },
    pricePerKg = pricePerKg.toFloatOrNull(),
    notes = notes.ifBlank { null },
    updatedAtEpochMs = System.currentTimeMillis(),
)
