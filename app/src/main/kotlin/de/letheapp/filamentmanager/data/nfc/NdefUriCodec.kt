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

    /** Ab dieser Länge braucht das TLV das 3-Byte-Längenformat (0xFF + 2 Byte Big Endian). */
    private const val TLV_SHORT_MAX = 0xFE

    /** Ab dieser Payload-Länge entfällt das SR-Flag, Payload-Length wird 4 Byte lang. */
    private const val SHORT_RECORD_MAX = 0xFF

    class UrlTooLongException(val maxUrlLength: Int) : Exception()

    /**
     * Baut die zu schreibenden Pages auf (Page-Nummer -> genau 4 Byte), analog zu [AceTagCodec.encode].
     * [capacityBytes] = User-Memory des erkannten Chips ([NtagType.userBytes]: 144/496/872 Byte),
     * bei unbekanntem Typ die sichere Untergrenze [AceTagCodec.TOTAL_BYTES].
     */
    fun encodeUrlMessage(url: String, capacityBytes: Int = AceTagCodec.TOTAL_BYTES): Map<Int, ByteArray> {
        val urlBytes = url.toByteArray(Charsets.UTF_8)
        val tlv = buildTlv(urlBytes)
        if (tlv.size > capacityBytes) throw UrlTooLongException(maxUrlBytes(capacityBytes))

        val padded = ByteArray(((tlv.size + 3) / 4) * 4)
        System.arraycopy(tlv, 0, padded, 0, tlv.size)

        val pages = LinkedHashMap<Int, ByteArray>()
        for (i in padded.indices step 4) {
            pages[AceTagCodec.FIRST_PAGE + i / 4] = padded.copyOfRange(i, i + 4)
        }
        return pages
    }

    private fun buildTlv(urlBytes: ByteArray): ByteArray {
        val payloadLength = 1 + urlBytes.size
        val shortRecord = payloadLength <= SHORT_RECORD_MAX
        val header = if (shortRecord) {
            byteArrayOf(
                0xD1.toByte(), // MB|ME|SR|TNF=well-known (einzelnes, kurzes Record)
                0x01, // Type Length: 1 Byte ('U')
                payloadLength.toByte(),
            )
        } else {
            byteArrayOf(
                0xC1.toByte(), // MB|ME|TNF=well-known, ohne SR -> 4-Byte-Payload-Length
                0x01,
                (payloadLength ushr 24).toByte(),
                (payloadLength ushr 16).toByte(),
                (payloadLength ushr 8).toByte(),
                payloadLength.toByte(),
            )
        }
        val record = header + byteArrayOf(
            0x55, // Type: 'U' (URI Record)
            URI_IDENTIFIER_NONE,
        ) + urlBytes

        val tlvLength = if (record.size <= TLV_SHORT_MAX) {
            byteArrayOf(record.size.toByte())
        } else {
            byteArrayOf(0xFF.toByte(), (record.size ushr 8).toByte(), record.size.toByte())
        }
        return byteArrayOf(NDEF_TLV_TYPE) + tlvLength + record + byteArrayOf(TERMINATOR_TLV)
    }

    /** Größte URL-Länge in Byte, deren TLV noch in [capacityBytes] passt (Overhead hängt von der Länge ab). */
    private fun maxUrlBytes(capacityBytes: Int): Int {
        var length = capacityBytes
        while (length > 0 && buildTlv(ByteArray(length)).size > capacityBytes) length--
        return length
    }
}
