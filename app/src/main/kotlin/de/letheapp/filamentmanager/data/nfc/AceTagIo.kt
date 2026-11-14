package de.letheapp.filamentmanager.data.nfc

import android.nfc.tech.NfcA

/**
 * Rohe NFC-A-Commands gegen MIFARE-Ultralight-/NTAG-kompatible Tags
 * (PLAN.md "Tag-Hardware & Format"): `READ (0x30)` liefert 16 Byte (4 Pages),
 * `WRITE (0xA2)` schreibt eine Page (4 Byte). Kein `MifareUltralight`-Tech-Objekt
 * nötig, reines `NfcA.transceive()` wie in beiden Referenzimplementierungen.
 */
object AceTagIo {
    private const val CMD_READ: Byte = 0x30
    private const val CMD_WRITE: Byte = 0xA2.toByte()

    /** Liest Page 3 (Capability Container), 4 Byte – siehe [AceTagCodec.ccMismatchWarning]. */
    fun readCapabilityContainer(nfcA: NfcA): ByteArray {
        val response = nfcA.transceive(byteArrayOf(CMD_READ, 3))
        return response.copyOfRange(0, minOf(4, response.size))
    }

    /** Liest Pages [AceTagCodec.FIRST_PAGE]..[AceTagCodec.LAST_PAGE] (144 Byte User-Bereich). */
    fun readAllPages(nfcA: NfcA): ByteArray {
        val result = ByteArray(AceTagCodec.TOTAL_BYTES)
        var offset = 0
        var page = AceTagCodec.FIRST_PAGE
        while (page <= AceTagCodec.LAST_PAGE) {
            val response = nfcA.transceive(byteArrayOf(CMD_READ, page.toByte()))
            val length = minOf(response.size, AceTagCodec.TOTAL_BYTES - offset)
            System.arraycopy(response, 0, result, offset, length)
            offset += length
            page += 4
        }
        return result
    }

    /** Schreibt genau eine Page (4 Byte Nutzdaten). */
    fun writePage(nfcA: NfcA, page: Int, data: ByteArray) {
        require(data.size == 4) { "Page-Daten müssen genau 4 Byte sein (Page $page: ${data.size})." }
        val cmd = ByteArray(6)
        cmd[0] = CMD_WRITE
        cmd[1] = page.toByte()
        System.arraycopy(data, 0, cmd, 2, 4)
        nfcA.transceive(cmd)
    }

    fun writePages(nfcA: NfcA, pages: Map<Int, ByteArray>) {
        pages.forEach { (page, data) -> writePage(nfcA, page, data) }
    }
}
