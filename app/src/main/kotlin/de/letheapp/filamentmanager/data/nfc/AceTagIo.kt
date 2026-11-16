package de.letheapp.filamentmanager.data.nfc

import android.nfc.tech.NfcA

/**
 * NTAG-Typ, nur relevant für die Lage der Konfigurations-/Passwort-Pages
 * (PLAN.md "Passwortschutz"): die drei unterstützten Chips haben unterschiedlich
 * viel User-Memory und verschieben die Config-Pages entsprechend nach hinten.
 */
enum class NtagType(val cfgStartPage: Int) {
    NTAG213(41),
    NTAG215(131),
    NTAG216(227),
}

/**
 * Rohe NFC-A-Commands gegen MIFARE-Ultralight-/NTAG-kompatible Tags
 * (PLAN.md "Tag-Hardware & Format"): `READ (0x30)` liefert 16 Byte (4 Pages),
 * `WRITE (0xA2)` schreibt eine Page (4 Byte). Kein `MifareUltralight`-Tech-Objekt
 * nötig, reines `NfcA.transceive()` wie in beiden Referenzimplementierungen.
 */
object AceTagIo {
    private const val CMD_READ: Byte = 0x30
    private const val CMD_WRITE: Byte = 0xA2.toByte()
    private const val CMD_GET_VERSION: Byte = 0x60
    private const val CMD_PWD_AUTH: Byte = 0x1B

    /** Liest Page 3 (Capability Container), 4 Byte – siehe [AceTagCodec.ccMismatchWarning]. */
    fun readCapabilityContainer(nfcA: NfcA): ByteArray {
        val response = nfcA.transceive(byteArrayOf(CMD_READ, 3))
        return response.copyOfRange(0, minOf(4, response.size))
    }

    /** Liest genau eine Page (4 Byte) – für Read-Modify-Write auf den Config-Pages. */
    fun readPage(nfcA: NfcA, page: Int): ByteArray {
        val response = nfcA.transceive(byteArrayOf(CMD_READ, page.toByte()))
        return response.copyOfRange(0, minOf(4, response.size))
    }

    /**
     * `GET_VERSION` (ISO/IEC 14443 Teil A, NTAG-Standardbefehl): Byte 6 der 8-Byte-Antwort
     * trägt die Speichergröße (0x0F=NTAG213/144B, 0x11=NTAG215/504B, 0x13=NTAG216/888B)
     * und bestimmt damit die Lage der Config-/Passwort-Pages (PLAN.md "Passwortschutz").
     * Unbekannter Wert -> NTAG215 als Fallback (laut PLAN.md der meistgetestete Typ).
     */
    fun detectNtagType(nfcA: NfcA): NtagType {
        val version = runCatching { nfcA.transceive(byteArrayOf(CMD_GET_VERSION)) }.getOrNull()
        val storageCode = if (version != null && version.size > 6) version[6].toInt() and 0xFF else 0
        return when (storageCode) {
            0x0F -> NtagType.NTAG213
            0x13 -> NtagType.NTAG216
            else -> NtagType.NTAG215
        }
    }

    /**
     * `PWD_AUTH` (0x1B + 4 Byte Passwort): schlägt mit einer Exception fehl (NAK/Timeout),
     * wenn das Passwort nicht zum aktuell auf dem Tag hinterlegten PWD-Wert passt. Bei Erfolg
     * ist der Tag für den Rest der Feldsitzung authentifiziert – nachfolgende Writes auf
     * Pages >= AUTH0 sind dann erlaubt, unabhängig vom PROT-Bit.
     */
    fun authenticate(nfcA: NfcA, password: ByteArray): Boolean {
        require(password.size == 4) { "Passwort muss genau 4 Byte sein." }
        return runCatching { nfcA.transceive(byteArrayOf(CMD_PWD_AUTH) + password) }.isSuccess
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
