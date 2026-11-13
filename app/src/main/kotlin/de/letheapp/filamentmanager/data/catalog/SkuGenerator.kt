package de.letheapp.filamentmanager.data.catalog

/**
 * Erzeugt eine deterministische SKU aus Hersteller/Typ/Farbe – dieselbe Kombination
 * ergibt immer dieselbe SKU, egal auf wie viele physische Spulen sie zutrifft (eine
 * einzelne Spule wird stattdessen über die NFC-Tag-UID im Schreib-Log identifiziert,
 * siehe NfcViewModel). Ergebnis passt immer in das 16-Byte-ASCII-Feld des
 * ACE-Tag-Formats (siehe AceTagCodec.TEXT_FIELD_BYTES).
 */
object SkuGenerator {
    private const val MAX_TOTAL = 16
    private const val BRAND_LEN = 4
    private const val TYPE_LEN = 6
    private const val COLOR_LEN = 4

    fun generate(brand: String, type: String, colorName: String): String {
        val parts = listOf(
            compact(brand, BRAND_LEN),
            compact(type, TYPE_LEN),
            compact(colorName, COLOR_LEN),
        ).filter { it.isNotEmpty() }
        if (parts.isEmpty()) return ""
        return parts.joinToString("-").take(MAX_TOTAL)
    }

    private fun compact(text: String, maxLen: Int): String =
        text.uppercase()
            .filter { it.isLetterOrDigit() }
            .take(maxLen)
}
