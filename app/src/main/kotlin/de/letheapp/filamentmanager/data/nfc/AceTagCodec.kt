package de.letheapp.filamentmanager.data.nfc

/**
 * Byte-genaues Encode/Decode für das ACE-2-Pro-kompatible NTAG-Format,
 * siehe PLAN.md "Tag-Hardware & Format" (gegen zwei unabhängige Referenz-
 * implementierungen verifiziert: ACE-RFID/Android + ACE-RFID/Windows).
 *
 * Reine Byte-Logik ohne Android-NFC-Abhängigkeit, damit sie unabhängig von
 * [AceTagIo]/[NfcBridge] testbar bleibt.
 */
data class AceTagData(
    val sku: String,
    val brand: String,
    val type: String,
    val colorArgb: Int,
    val extruderTempMin: Int,
    val extruderTempMax: Int,
    val hotbedTempMin: Int,
    val hotbedTempMax: Int,
    val diameterMm: Float,
    val lengthM: Int,
    val weightG: Int,
)

object AceTagCodec {
    const val FIRST_PAGE = 4
    const val LAST_PAGE = 36
    const val TOTAL_BYTES = (LAST_PAGE - FIRST_PAGE + 4) * 4

    private val HEADER = byteArrayOf(0x7B, 0x00, 0x65, 0x00)
    private const val HEADER_PAGE = 4
    private const val SKU_START_PAGE = 5
    private const val BRAND_START_PAGE = 10
    private const val TYPE_START_PAGE = 15
    private const val COLOR_PAGE = 20
    private const val EXT_TEMP_PAGE = 24
    private const val BED_TEMP_PAGE = 29
    private const val SIZE_PAGE = 30
    private const val WEIGHT_PAGE = 31

    // Referenz-Apps beschreiben pro Textfeld nur 4 Pages (16 Byte), die jeweils
    // fünfte reservierte Page (9/14/19) bleibt als Lücke unberührt.
    private const val TEXT_FIELD_BYTES = 16

    /** Baut die zu schreibenden Pages für [data] auf (Page-Nummer -> genau 4 Byte). */
    fun encode(data: AceTagData): Map<Int, ByteArray> {
        val pages = LinkedHashMap<Int, ByteArray>()
        pages[HEADER_PAGE] = HEADER
        writeAsciiField(pages, SKU_START_PAGE, data.sku)
        writeAsciiField(pages, BRAND_START_PAGE, data.brand)
        writeAsciiField(pages, TYPE_START_PAGE, data.type)
        pages[COLOR_PAGE] = encodeColor(data.colorArgb)
        pages[EXT_TEMP_PAGE] = le16(data.extruderTempMin) + le16(data.extruderTempMax)
        pages[BED_TEMP_PAGE] = le16(data.hotbedTempMin) + le16(data.hotbedTempMax)
        pages[SIZE_PAGE] = le16((data.diameterMm * 100f).toInt()) + le16(data.lengthM)
        pages[WEIGHT_PAGE] = le16(data.weightG) + byteArrayOf(0, 0)
        return pages
    }

    /** `raw` = 144 Byte ab Page 4 (siehe [AceTagIo.readAllPages]). */
    fun decode(raw: ByteArray): AceTagData {
        val sku = asciiTrim(fieldBytes(raw, SKU_START_PAGE))
        val brand = asciiTrim(fieldBytes(raw, BRAND_START_PAGE))
        val type = asciiTrim(fieldBytes(raw, TYPE_START_PAGE))
        val extPage = pageBytes(raw, EXT_TEMP_PAGE)
        val bedPage = pageBytes(raw, BED_TEMP_PAGE)
        val sizePage = pageBytes(raw, SIZE_PAGE)
        val weightPage = pageBytes(raw, WEIGHT_PAGE)
        return AceTagData(
            sku = sku,
            brand = brand,
            type = type,
            colorArgb = decodeColor(pageBytes(raw, COLOR_PAGE)),
            extruderTempMin = leToInt(extPage, 0),
            extruderTempMax = leToInt(extPage, 2),
            hotbedTempMin = leToInt(bedPage, 0),
            hotbedTempMax = leToInt(bedPage, 2),
            diameterMm = leToInt(sizePage, 0) / 100f,
            lengthM = leToInt(sizePage, 2),
            weightG = leToInt(weightPage, 0),
        )
    }

    /** true, wenn Page 4 Byte 0 nicht unserem Header entspricht -> Tag ist leer/unbeschrieben. */
    fun isEmptyTag(raw: ByteArray): Boolean = raw.isEmpty() || raw[0] == 0.toByte()

    /** Vergleicht nach dem Schreiben Byte-für-Byte die tatsächlich geschriebenen Pages (Lesevergleich, PLAN.md Schritt 4). */
    fun verify(raw: ByteArray, expectedPages: Map<Int, ByteArray>): Boolean =
        expectedPages.all { (page, expected) -> pageBytes(raw, page).contentEquals(expected) }

    private fun pageBytes(raw: ByteArray, page: Int): ByteArray {
        val offset = (page - HEADER_PAGE) * 4
        return raw.copyOfRange(offset, offset + 4)
    }

    private fun fieldBytes(raw: ByteArray, startPage: Int): ByteArray {
        val offset = (startPage - HEADER_PAGE) * 4
        return raw.copyOfRange(offset, offset + TEXT_FIELD_BYTES)
    }

    private fun writeAsciiField(pages: MutableMap<Int, ByteArray>, startPage: Int, text: String) {
        val bytes = ByteArray(TEXT_FIELD_BYTES)
        val src = text.toByteArray(Charsets.US_ASCII)
        System.arraycopy(src, 0, bytes, 0, minOf(src.size, TEXT_FIELD_BYTES))
        for (i in 0 until TEXT_FIELD_BYTES / 4) {
            pages[startPage + i] = bytes.copyOfRange(i * 4, i * 4 + 4)
        }
    }

    private fun asciiTrim(bytes: ByteArray): String {
        val end = bytes.indexOfFirst { it == 0.toByte() }.let { if (it == -1) bytes.size else it }
        return String(bytes, 0, end, Charsets.US_ASCII)
    }

    /**
     * Alpha immer 0xFF (deckend) + BGR, byteweise gespiegelt aus RGB (PLAN.md "Page-Layout").
     * Firmware-Eigenheit: reines Schwarz (000000) wird beim Lesen als "keine Farbe" interpretiert,
     * Referenz-Apps weichen deshalb beim Schreiben auf (1,1,1) aus.
     */
    private fun encodeColor(argb: Int): ByteArray {
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        val isBlack = r == 0 && g == 0 && b == 0
        val rr = if (isBlack) 1 else r
        val gg = if (isBlack) 1 else g
        val bb = if (isBlack) 1 else b
        return byteArrayOf(0xFF.toByte(), bb.toByte(), gg.toByte(), rr.toByte())
    }

    private fun decodeColor(page: ByteArray): Int {
        var b = page[1].toInt() and 0xFF
        var g = page[2].toInt() and 0xFF
        var r = page[3].toInt() and 0xFF
        if (r == 1 && g == 1 && b == 1) {
            r = 0; g = 0; b = 0
        }
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun le16(value: Int): ByteArray {
        val v = value.coerceIn(0, 0xFFFF)
        return byteArrayOf((v and 0xFF).toByte(), ((v shr 8) and 0xFF).toByte())
    }

    private fun leToInt(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xFF) or ((bytes[offset + 1].toInt() and 0xFF) shl 8)
}
