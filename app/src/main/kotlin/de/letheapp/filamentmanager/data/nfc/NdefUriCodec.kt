package de.letheapp.filamentmanager.data.nfc

/**
 * Baut ein minimales NDEF-URI-Record (Type "U") als TLV ab Page 4 auf, damit jedes
 * NFC-fähige Handy (nicht nur diese App) die URL beim Auflegen automatisch öffnet
 * ("Tools"-Modus, PLAN.md "Passwortschutz"-Abschnitt). Nutzt denselben Page-Bereich
 * wie das ACE-Datenformat ([AceTagCodec]) – beide schließen sich gegenseitig aus,
 * ein Tag trägt entweder Filament-Daten oder eine Tool-Funktion.
 */
object NdefUriCodec {
    /** Kein Prefix-Shortcut (Identifier Code 0x00) – einfacher und robuster als die URI-Abkürzungen. */
    private const val URI_IDENTIFIER_NONE: Byte = 0x00
    private const val NDEF_TLV_TYPE: Byte = 0x03
    private const val TERMINATOR_TLV: Byte = 0xFE.toByte()

    /** Verfügbarer Platz: dieselben 144 Byte (Pages 4–39), die auch für das ACE-Format als sicher garantiert gelten. */
    private const val MAX_BYTES = AceTagCodec.TOTAL_BYTES

    class UrlTooLongException(val maxUrlLength: Int) : Exception()

    /** Baut die zu schreibenden Pages auf (Page-Nummer -> genau 4 Byte), analog zu [AceTagCodec.encode]. */
    fun encodeUrlMessage(url: String): Map<Int, ByteArray> {
        val urlBytes = url.toByteArray(Charsets.UTF_8)
        // Record-Header (1) + Type-Length (1) + Payload-Length (1) + Type 'U' (1) + URI-Identifier (1)
        val recordOverhead = 5
        val recordBytes = recordOverhead + urlBytes.size
        // TLV-Header (Type+Length, 2 Byte) + Record + Terminator-TLV (1 Byte)
        val totalBytes = 2 + recordBytes + 1
        val maxUrlBytes = MAX_BYTES - 2 - recordOverhead - 1
        if (totalBytes > MAX_BYTES) throw UrlTooLongException(maxUrlBytes)

        val payloadLength = 1 + urlBytes.size
        val record = byteArrayOf(
            0xD1.toByte(), // MB|ME|SR|TNF=well-known (einzelnes, kurzes Record)
            0x01, // Type Length: 1 Byte ('U')
            payloadLength.toByte(),
            0x55, // Type: 'U' (URI Record)
            URI_IDENTIFIER_NONE,
        ) + urlBytes

        val tlv = byteArrayOf(NDEF_TLV_TYPE, record.size.toByte()) + record + byteArrayOf(TERMINATOR_TLV)
        val padded = ByteArray(((tlv.size + 3) / 4) * 4)
        System.arraycopy(tlv, 0, padded, 0, tlv.size)

        val pages = LinkedHashMap<Int, ByteArray>()
        for (i in padded.indices step 4) {
            pages[AceTagCodec.FIRST_PAGE + i / 4] = padded.copyOfRange(i, i + 4)
        }
        return pages
    }
}
