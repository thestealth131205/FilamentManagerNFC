package de.letheapp.filamentmanager.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.letheapp.filamentmanager.data.catalog.BrandCatalog
import de.letheapp.filamentmanager.data.catalog.CatalogColor
import de.letheapp.filamentmanager.data.catalog.CatalogMaterial
import de.letheapp.filamentmanager.data.catalog.MaterialCatalog
import de.letheapp.filamentmanager.data.catalog.SkuGenerator
import de.letheapp.filamentmanager.data.catalog.parseCatalogHex
import de.letheapp.filamentmanager.data.catalog.toCatalogHex
import de.letheapp.filamentmanager.data.db.AppDatabase
import de.letheapp.filamentmanager.data.db.FilamentColorEntity
import de.letheapp.filamentmanager.data.db.FilamentEntity
import de.letheapp.filamentmanager.data.db.FilamentTypeEntity
import de.letheapp.filamentmanager.data.db.ManufacturerEntity
import de.letheapp.filamentmanager.ui.scan.ScanDraftHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
    // Hex-Farbwert aus BrandCatalog, wenn colorName einer Katalogfarbe für die
    // aktuelle Hersteller/Typ-Kombination entspricht; sonst null (= Fallback Schwarz).
    val colorHex: String? = null,
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

    /** Katalogeintrag (Temperaturen + bekannte Farben) für die aktuell gewählte Hersteller/Typ-Kombination. */
    private val catalogMaterial: StateFlow<CatalogMaterial?> = form
        .map { BrandCatalog.findMaterial(it.brand, it.type) }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Farbvorschläge oben im Dropdown: Katalogfarben der aktuellen Material-Kombi, sonst die gelernten Top-3. */
    val colorTopEntries: StateFlow<List<String>> = combine(catalogMaterial, colorTop3) { material, learnedTop3 ->
        material?.colors?.map { it.name } ?: learnedTop3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Für die Suche: Katalogfarben der aktuellen Kombi plus alle gelernten Farben, dedupliziert. */
    val colorAllEntries: StateFlow<List<String>> = combine(catalogMaterial, colorAllAlpha) { material, learnedAll ->
        val catalogNames = material?.colors?.map { it.name } ?: emptyList()
        (catalogNames + learnedAll).distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Auswahl für den Farbcode-Picker (Swatch neben dem Namensfeld): Katalogfarben
     * der aktuellen Hersteller/Typ-Kombi zuerst, danach die markenunabhängigen
     * [BrandCatalog.commonColors] (z.B. "Ceramic", "Midnight Black") – damit auch
     * Farben, deren Name die OCR nicht erkennt oder die frei eingetippt wurden,
     * nachträglich einem echten Farbcode zugeordnet werden können statt beim
     * Speichern stillschweigend auf Schwarz zurückzufallen.
     */
    val colorCodeOptions: StateFlow<List<CatalogColor>> = catalogMaterial.map { material ->
        val specific = material?.colors.orEmpty()
        (specific + BrandCatalog.commonColors).distinctBy { it.name.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BrandCatalog.commonColors)

    init {
        if (filamentId != null) {
            viewModelScope.launch {
                dao.getById(filamentId)?.let { form.value = it.toFormState() }
                recomputeSku()
            }
        } else {
            // Kommt der Editor direkt vom Kamera-Scan, liegt hier ein per OCR
            // vorausgefüllter Entwurf bereit (siehe ScanScreen/LabelOcrParser).
            ScanDraftHolder.consume()?.let { form.value = it }
            recomputeSku()
        }
    }

    fun update(transform: (FilamentFormState) -> FilamentFormState) {
        form.value = transform(form.value)
    }

    /** Hersteller gewählt: Feld setzen und ggf. passende Katalog-Temperaturen/-Farbe nachziehen. */
    fun selectBrand(name: String) {
        form.value = form.value.copy(brand = name)
        applyCatalogAutofill()
        recomputeSku()
    }

    /** Typ gewählt: Feld setzen und ggf. passende Katalog-Temperaturen/-Farbe nachziehen. */
    fun selectType(name: String) {
        form.value = form.value.copy(type = name)
        applyCatalogAutofill()
        recomputeSku()
    }

    /**
     * Farbe gewählt: Feld setzen, Hex-Wert aus dem Katalog übernehmen falls bekannt
     * (zuerst die konkrete Hersteller/Typ-Kombi, sonst markenunabhängig per Name
     * z.B. "Ceramic"/"Midnight Black"), sonst null – dann kann der Hex-Wert noch
     * über [selectColorHex] (Farbcode-Picker im Editor) nachträglich gesetzt werden.
     */
    fun selectColor(name: String) {
        val state = form.value
        form.value = state.copy(
            colorName = name,
            colorHex = BrandCatalog.hexFor(state.brand, state.type, name) ?: BrandCatalog.hexForAny(name),
        )
        recomputeSku()
    }

    /**
     * Farbcode (Hex) direkt über den Picker gewählt, unabhängig vom Namensfeld –
     * für Fälle, in denen der eingetragene/erkannte Farbname (z.B. aus OCR oder
     * frei eingetippt) keinem bekannten Katalogeintrag entspricht und sonst beim
     * Speichern stillschweigend auf Schwarz zurückfiele (siehe toEntity()).
     */
    fun selectColorHex(hex: String) {
        form.value = form.value.copy(colorHex = hex)
    }

    /**
     * SKU wird nicht mehr manuell eingetippt, sondern deterministisch aus
     * Hersteller/Typ/Farbe berechnet – gleiche Kombination ergibt immer dieselbe
     * SKU (siehe SkuGenerator). Läuft nach jeder Änderung dieser drei Felder.
     */
    private fun recomputeSku() {
        val state = form.value
        form.value = state.copy(sku = SkuGenerator.generate(state.brand, state.type, state.colorName))
    }

    /**
     * Füllt leere Temperaturfelder aus dem Katalog auf (nur wenn noch nichts eingetragen ist –
     * überschreibt nie eine vom Nutzer schon gesetzte Temperatur) und aktualisiert den Hex-Wert
     * der aktuellen Farbe, falls sie zur neuen Hersteller/Typ-Kombination passt. Fällt für die
     * Temperaturen auf die markenunabhängige [MaterialCatalog] zurück, wenn der gewählte Typ zu
     * keinem Markeneintrag passt (unbekannte Marke, oder Hochleistungs-/Support-Materialien wie
     * PEEK/PVA, die im Markenkatalog nicht hinterlegt sind) – liefert diese ebenfalls nichts
     * (z.B. Resin/Pulver ohne Düsen-/Betttemperatur), bleiben die Felder unverändert.
     */
    private fun applyCatalogAutofill() {
        val state = form.value
        val brandMaterial = BrandCatalog.findMaterial(state.brand, state.type)
        val generic = if (brandMaterial == null && state.type.isNotBlank()) {
            MaterialCatalog.findByKuerzel(state.type)
        } else {
            null
        }
        form.value = state.copy(
            extruderTempMin = state.extruderTempMin.ifBlank {
                (brandMaterial?.nozzleMin ?: generic?.nozzleMin)?.toString() ?: state.extruderTempMin
            },
            extruderTempMax = state.extruderTempMax.ifBlank {
                (brandMaterial?.nozzleMax ?: generic?.nozzleMax)?.toString() ?: state.extruderTempMax
            },
            hotbedTempMin = state.hotbedTempMin.ifBlank {
                (brandMaterial?.bedMin ?: generic?.bedMin)?.toString() ?: state.hotbedTempMin
            },
            hotbedTempMax = state.hotbedTempMax.ifBlank {
                (brandMaterial?.bedMax ?: generic?.bedMax)?.toString() ?: state.hotbedTempMax
            },
            colorHex = BrandCatalog.hexFor(state.brand, state.type, state.colorName)
                ?: BrandCatalog.hexForAny(state.colorName)
                ?: state.colorHex,
        )
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
    colorHex = toCatalogHex(colorArgb),
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
    colorArgb = colorHex?.let { parseCatalogHex(it) } ?: 0xFF000000.toInt(),
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
