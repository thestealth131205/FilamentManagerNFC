package de.letheapp.filamentmanager.ui.scan

import de.letheapp.filamentmanager.data.catalog.BrandCatalog
import de.letheapp.filamentmanager.data.catalog.CatalogColor
import de.letheapp.filamentmanager.data.db.SeedData
import de.letheapp.filamentmanager.ui.editor.FilamentFormState

/**
 * Heuristische Auswertung des per ML-Kit-OCR erkannten Etikett-Texts
 * (PLAN.md, Schritt 5): gleicht den Rohtext gegen den Hersteller-/Material-
 * Katalog ([BrandCatalog]) und die Typ-Stammdaten ([SeedData.types]) ab und
 * extrahiert zusätzlich Durchmesser, Gewicht und Temperaturbereiche per Regex.
 *
 * Nichts davon wird ungeprüft übernommen — das Ergebnis landet nur als
 * Vorschlag im Editor (FilamentEditorViewModel/ScanDraftHolder), nicht
 * angekommene Pflichtfelder bleiben leer und werden dort rot umrandet.
 */

// Generische Farbwörter (DE/EN) als Fallback, falls Hersteller/Typ nicht
// erkannt wurden oder deren Katalog-Farbliste keinen Treffer liefert.
private val genericColors = listOf(
    CatalogColor("Schwarz", "#1A1A1A"),
    CatalogColor("Black", "#1A1A1A"),
    CatalogColor("Weiß", "#FFFFFF"),
    CatalogColor("White", "#FFFFFF"),
    CatalogColor("Grau", "#808080"),
    CatalogColor("Grey", "#808080"),
    CatalogColor("Gray", "#808080"),
    CatalogColor("Rot", "#D32F2F"),
    CatalogColor("Red", "#D32F2F"),
    CatalogColor("Blau", "#1976D2"),
    CatalogColor("Blue", "#1976D2"),
    CatalogColor("Grün", "#2E7D32"),
    CatalogColor("Green", "#2E7D32"),
    CatalogColor("Gelb", "#F9A825"),
    CatalogColor("Yellow", "#F9A825"),
    CatalogColor("Orange", "#EF6C00"),
    CatalogColor("Transparent", "#EAEAEA"),
    CatalogColor("Clear", "#EAEAEA"),
    CatalogColor("Silber", "#A9ACB6"),
    CatalogColor("Silver", "#A9ACB6"),
    CatalogColor("Gold", "#D4AF37"),
    CatalogColor("Pink", "#E91E63"),
    CatalogColor("Rosa", "#E91E63"),
    CatalogColor("Lila", "#7B1FA2"),
    CatalogColor("Violett", "#7B1FA2"),
    CatalogColor("Purple", "#7B1FA2"),
    CatalogColor("Braun", "#795548"),
    CatalogColor("Brown", "#795548"),
)

private val nozzleKeywords = listOf(
    "NOZZLE", "EXTRUDER", "DÜSE", "DUSE", "DUESE", "PRINTING TEMP", "PRINT TEMP",
    "DRUCKTEMPERATUR", "HOTEND", "EXTRUSION",
)
private val bedKeywords = listOf(
    "BED", "HEIZBETT", "DRUCKBETT", "PLATTE", "BETTTEMPERATUR", "BUILD PLATE", "PLATTFORM",
)

fun parseLabelText(rawText: String): FilamentFormState {
    val normalized = rawText.replace(Regex("\\s+"), " ").trim()
    val upper = normalized.uppercase()

    val brand = BrandCatalog.brands.firstOrNull { b -> upper.contains(b.name.uppercase()) }?.name.orEmpty()

    // Längere/speziellere Typen zuerst prüfen (z.B. "PETG-CF" vor "PETG"),
    // sonst würde der kürzere Typ fälschlich zuerst matchen.
    val typeCandidates = (SeedData.types + BrandCatalog.brands.flatMap { b -> b.materials.map { it.type } })
        .distinct()
        .sortedByDescending { it.length }
    val type = typeCandidates.firstOrNull { t -> containsAsToken(upper, t.uppercase()) }.orEmpty()

    val catalogColors = if (brand.isNotBlank() && type.isNotBlank()) {
        BrandCatalog.findMaterial(brand, type)?.colors.orEmpty()
    } else {
        emptyList()
    }
    val colorMatch = (catalogColors + genericColors).firstOrNull { c -> upper.contains(c.name.uppercase()) }

    val diameter = Regex("(\\d[.,]\\d{1,2})\\s?MM")
        .findAll(upper)
        .mapNotNull { it.groupValues[1].replace(',', '.').toFloatOrNull() }
        .firstOrNull { it in 0.5f..3.5f }

    val weightGrams = Regex("(\\d{3,5})\\s?G\\b").find(upper)?.groupValues?.get(1)?.toIntOrNull()
        ?: Regex("(\\d(?:[.,]\\d+)?)\\s?KG").find(upper)?.groupValues?.get(1)
            ?.replace(',', '.')?.toFloatOrNull()?.times(1000)?.toInt()

    val temps = extractTempRanges(upper)

    val sku = Regex("\\b[A-Z]{2,10}-\\d{2,6}\\b").find(upper)?.value.orEmpty()

    return FilamentFormState(
        sku = sku,
        brand = brand,
        type = type,
        colorName = colorMatch?.name.orEmpty(),
        colorHex = colorMatch?.hex,
        diameterMm = diameter?.toString() ?: "1.75",
        spoolWeightG = weightGrams?.toString().orEmpty(),
        extruderTempMin = temps.nozzleMin.orEmpty(),
        extruderTempMax = temps.nozzleMax.orEmpty(),
        hotbedTempMin = temps.bedMin.orEmpty(),
        hotbedTempMax = temps.bedMax.orEmpty(),
    )
}

/** Prüft, ob [token] in [haystack] vorkommt, ohne Teil eines größeren alphanumerischen Worts zu sein (auch bei Tokens, die mit Sonderzeichen wie "+" enden, wo \b nicht zuverlässig greift). */
private fun containsAsToken(haystack: String, token: String): Boolean {
    val pattern = Regex("(?<![A-Z0-9])" + Regex.escape(token) + "(?![A-Z0-9])")
    return pattern.containsMatchIn(haystack)
}

private data class TempRanges(
    val nozzleMin: String? = null,
    val nozzleMax: String? = null,
    val bedMin: String? = null,
    val bedMax: String? = null,
)

private fun extractTempRanges(upper: String): TempRanges {
    val rangeRegex = Regex("(\\d{2,3})\\s*(?:[-~/–]|BIS|TO)\\s*(\\d{2,3})\\s*°?\\s*C")
    val matches = rangeRegex.findAll(upper).toList()
    if (matches.isEmpty()) return TempRanges()

    fun hasKeywordBefore(matchRange: IntRange, keywords: List<String>): Boolean {
        val start = (matchRange.first - 40).coerceAtLeast(0)
        val context = upper.substring(start, matchRange.first)
        return keywords.any { context.contains(it) }
    }

    val nozzleMatch = matches.firstOrNull { hasKeywordBefore(it.range, nozzleKeywords) } ?: matches.getOrNull(0)
    val bedMatch = matches.firstOrNull { hasKeywordBefore(it.range, bedKeywords) && it != nozzleMatch }
        ?: matches.getOrNull(1)?.takeIf { it != nozzleMatch }

    return TempRanges(
        nozzleMin = nozzleMatch?.groupValues?.get(1),
        nozzleMax = nozzleMatch?.groupValues?.get(2),
        bedMin = bedMatch?.groupValues?.get(1),
        bedMax = bedMatch?.groupValues?.get(2),
    )
}
