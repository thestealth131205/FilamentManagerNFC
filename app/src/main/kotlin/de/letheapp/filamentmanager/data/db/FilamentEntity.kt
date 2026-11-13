package de.letheapp.filamentmanager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Siehe PLAN.md, Abschnitt "Datenmodell (Room, Kotlin)".
 * Felder ohne direkte Tag-Entsprechung (Slicer/Einkauf) sind rein lokal und werden
 * beim NFC-Schreiben (PLAN.md, Schritt 4) nicht auf den Tag gespiegelt.
 */
@Entity(tableName = "filaments")
data class FilamentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String? = null,
    val brand: String = "",
    val type: String = "",
    val colorArgb: Int = 0xFF000000.toInt(),
    val colorName: String? = null,
    val diameterMm: Float = 1.75f,
    val spoolWeightG: Int? = null,
    val lengthTotalM: Float? = null,
    val lengthRemainingM: Float? = null,
    val extruderTempMin: Int? = null,
    val extruderTempMax: Int? = null,
    val hotbedTempMin: Int? = null,
    val hotbedTempMax: Int? = null,
    // Erweiterte/Slicer-Daten, nicht auf dem Tag
    val density: Float? = null,
    val flowRatePercent: Int? = null,
    val retractionDistanceMm: Float? = null,
    val retractionSpeedMmS: Float? = null,
    val fanSpeedPercent: Int? = null,
    val printSpeedMmS: Int? = null,
    val maxBedAdhesionNotes: String? = null,
    val purchaseUrl: String? = null,
    val pricePerKg: Float? = null,
    val purchaseDateEpochDay: Long? = null,
    val notes: String? = null,
    val labelImagePath: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis(),
)

/** Protokoll, welcher Tag-UID wann mit welchem Filament beschrieben wurde (PLAN.md). */
@Entity(tableName = "nfc_write_log")
data class NfcWriteLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagUid: String,
    val filamentId: Long,
    val writtenAtEpochMs: Long = System.currentTimeMillis(),
)
