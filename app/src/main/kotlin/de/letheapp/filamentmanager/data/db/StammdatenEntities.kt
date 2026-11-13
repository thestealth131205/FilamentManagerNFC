package de.letheapp.filamentmanager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Gemeinsames Muster für alle Dropdown-Stammdaten mit "lernender" Sortierung
 * (siehe PLAN.md, Abschnitt "Hersteller- und Stammdaten-Auswahl"):
 * usageCount steigt bei jeder Zuordnung, die drei meistgenutzten Einträge
 * stehen in SearchableDropdown oben.
 */
interface StammdatenEntry {
    val id: Long
    val name: String
    val usageCount: Int
    val lastUsedAtEpochMs: Long
}

@Entity(tableName = "manufacturers")
data class ManufacturerEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    override val name: String,
    override val usageCount: Int = 0,
    override val lastUsedAtEpochMs: Long = System.currentTimeMillis(),
) : StammdatenEntry

@Entity(tableName = "filament_types")
data class FilamentTypeEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    override val name: String,
    override val usageCount: Int = 0,
    override val lastUsedAtEpochMs: Long = System.currentTimeMillis(),
) : StammdatenEntry

@Entity(tableName = "filament_colors")
data class FilamentColorEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    override val name: String,
    override val usageCount: Int = 0,
    override val lastUsedAtEpochMs: Long = System.currentTimeMillis(),
) : StammdatenEntry

/** Seed-Daten, damit die Dropdowns nicht leer starten (PLAN.md). */
object SeedData {
    val manufacturers = listOf("SUNLU", "Anycubic", "eSUN", "Prusament", "Polymaker")
    val types = listOf("PLA", "PETG", "ABS", "TPU", "ASA")
}
