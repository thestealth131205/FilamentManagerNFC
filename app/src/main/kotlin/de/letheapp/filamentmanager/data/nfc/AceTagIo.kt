package de.letheapp.filamentmanager.data.nfc

import android.nfc.tech.NfcA
import java.io.IOException

/**
 * NTAG-Typ, relevant für die Lage der Konfigurations-/Passwort-Pages
 * (PLAN.md "Passwortschutz") und den für NDEF-URLs nutzbaren Platz: die drei
 * unterstützten Chips haben unterschiedlich viel User-Memory und verschieben die
 * Config-Pages entsprechend nach hinten.
 *
 * [ccSize] = Byte 2 des werkseitigen Capability Containers (Page 3), User-Memory / 8
 * (NXP-Datenblatt NTAG213/215/216, Kap. 8.5): 0x12=144 B, 0x3E=496 B, 0x6D=872 B.
 */
enum class NtagType(val cfgStartPage: Int, val ccSize: Int) {
    NTAG213(41, 0x12),
    NTAG215(131, 0x3E),
    NTAG216(227, 0x6D);

    /** User-Memory ab Page 4 in Byte – Obergrenze für NDEF-Daten. */
    val userBytes: Int get() = ccSize * 8

    /** Werkseitiger Capability Container dieses Typs (NDEF-formatiert, lesen+schreiben frei). */
    val capabilityContainer: ByteArray get() = byteArrayOf(0xE1.toByte(), 0x10, ccSize.toByte(), 0x00)

    companion object {
        fun fromCapabilityContainer(cc: ByteArray?): NtagType? {
            if (cc == null || cc.size < 4 || cc[0] != 0xE1.toByte()) return null
            val size = cc[2].toInt() and 0xFF
            return entries.firstOrNull { it.ccSize == size }
        }
    }
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

    /** 4-Bit-ACK des Tags; manche Android-Stacks liefern einen NAK als 1-Byte-Antwort statt einer Exception. */
    private const val ACK: Int = 0x0A

    /** Liest Page 3 (Capability Container), 4 Byte – siehe [AceTagCodec.ccMismatchWarning]. */
    fun readCapabilityContainer(nfcA: NfcA): ByteArray {
        val response = nfcA.transceive(byteArrayOf(CMD_READ, 3))
        return response.copyOfRange(0, minOf(4, response.size))
    }

    /** Liest genau eine Page (4 Byte) – für Read-Modify-Write auf den Config-Pages. */
    fun readPage(nfcA: NfcA, page: Int): ByteArray {
        val response = nfcA.transceive(byteArrayOf(CMD_READ, page.toByte()))
        if (response.size < 4) throw IOException("READ Page $page abgelehnt (NAK)")
        return response.copyOfRange(0, minOf(4, response.size))
    }

    /**
     * `GET_VERSION` (ISO/IEC 14443 Teil A, NTAG-Standardbefehl): Byte 6 der 8-Byte-Antwort
     * trägt die Speichergröße (0x0F=NTAG213/144B, 0x11=NTAG215/504B, 0x13=NTAG216/888B)
     * und bestimmt damit die Lage der Config-/Passwort-Pages (PLAN.md "Passwortschutz").
     * Liefert der Tag keine eindeutige Antwort (z.B. Klon-Chips), entscheidet der bereits
     * gelesene Capability Container [cc]. Beides unbekannt -> null: Config-Pages werden dann
     * nicht blind beschrieben, sonst landet ein Passwort-Write mitten im User-Memory.
     */
    fun detectNtagType(nfcA: NfcA, cc: ByteArray?): NtagType? {
        val version = runCatching { nfcA.transceive(byteArrayOf(CMD_GET_VERSION)) }
            // Chips ohne GET_VERSION (z.B. Ultralight C) gehen nach dem NAK in IDLE und
            // antworten erst nach erneutem Aktivieren wieder – Verbindung neu aufbauen.
            // close() setzt den Timeout auf den Default zurück, daher übernehmen.
            .onFailure {
                val timeout = nfcA.timeout
                runCatching { nfcA.close(); nfcA.connect(); nfcA.timeout = timeout }
            }
            .getOrNull()
        val storageCode = if (version != null && version.size > 6) version[6].toInt() and 0xFF else 0
        return when (storageCode) {
            0x0F -> NtagType.NTAG213
            0x11 -> NtagType.NTAG215
            0x13 -> NtagType.NTAG216
            else -> NtagType.fromCapabilityContainer(cc)
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
        // Erfolg = 2 Byte PACK; ein als 1-Byte-Antwort durchgereichter NAK zählt als Fehlschlag.
        val response = runCatching { nfcA.transceive(byteArrayOf(CMD_PWD_AUTH) + password) }.getOrNull()
        return response != null && response.size >= 2
    }

    /** Liest Pages [AceTagCodec.FIRST_PAGE]..[AceTagCodec.LAST_PAGE] (144 Byte User-Bereich). */
    fun readAllPages(nfcA: NfcA): ByteArray = readBytes(nfcA, AceTagCodec.FIRST_PAGE, AceTagCodec.TOTAL_BYTES)

    /** Liest [byteCount] Byte ab [firstPage] (in 16-Byte-READ-Blöcken), z.B. für den Lesevergleich längerer NDEF-URLs. */
    fun readBytes(nfcA: NfcA, firstPage: Int, byteCount: Int): ByteArray {
        val result = ByteArray(byteCount)
        var offset = 0
        var page = firstPage
        while (offset < byteCount) {
            val response = nfcA.transceive(byteArrayOf(CMD_READ, page.toByte()))
            if (response.size < 4) throw IOException("READ Page $page abgelehnt (NAK)")
            val length = minOf(response.size, byteCount - offset)
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
        val response = nfcA.transceive(cmd)
        if (response.size == 1 && (response[0].toInt() and 0x0F) != ACK) {
            throw IOException("WRITE Page $page abgelehnt (NAK)")
        }
    }

    fun writePages(nfcA: NfcA, pages: Map<Int, ByteArray>) {
        pages.forEach { (page, data) -> writePage(nfcA, page, data) }
    }
}
