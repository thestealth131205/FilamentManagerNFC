package de.letheapp.filamentmanager.data.catalog

/**
 * Offizielle Anycubic-SKUs für Material-Typ + Farbe (vom Nutzer als Preisliste
 * geliefert, Stand 2026-11-16, de.anycubic.com). Wird von [SkuGenerator] zuerst
 * geprüft: Passt Typ + Farbe eines Eintrags (Hersteller-Feld egal) zu einer
 * hier gelisteten Anycubic-Kombination, wird deren echte SKU verwendet statt
 * einer generierten – entscheidend ist nicht, wer die Spule tatsächlich
 * hergestellt hat, sondern nur, wie sie sich druckt und welche Farbe sie hat
 * (siehe Nutzeranfrage vom 2026-11-16).
 *
 * Die Zuordnung ist bewusst tolerant: Hersteller benennen Farben und Typ-
 * Varianten oft leicht anders (z.B. "Weiß" vs. "White", "PLA" vs. "PLA Basic",
 * "Strukturgrau" vs. "Grau") – [normalizeType] und [colorTokens] fassen solche
 * Schreibweisen zusammen, statt nur exakte Strings zu vergleichen.
 */
data class AnycubicSkuEntry(val type: String, val colorDe: String, val sku: String)

object AnycubicSkuCatalog {
    val entries: List<AnycubicSkuEntry> = listOf(
        // PLA Basic
        AnycubicSkuEntry("PLA Basic", "Weiß", "AHPLBW-105"),
        AnycubicSkuEntry("PLA Basic", "Schwarz", "AHPLBK-105"),
        AnycubicSkuEntry("PLA Basic", "Strukturgrau", "AHPLGY-105"),
        AnycubicSkuEntry("PLA Basic", "Klar", "AHPLCL-105"),
        AnycubicSkuEntry("PLA Basic", "Rot", "AHPLRR-105"),
        AnycubicSkuEntry("PLA Basic", "Struktursilber", "AHPLSL-105"),
        AnycubicSkuEntry("PLA Basic", "Grün", "AHPLCG-105"),
        AnycubicSkuEntry("PLA Basic", "Lila", "AHPLPO-105"),
        AnycubicSkuEntry("PLA Basic", "Beige", "AHPLLB-105"),
        AnycubicSkuEntry("PLA Basic", "Bronze", "AHPLBZ-105"),
        AnycubicSkuEntry("PLA Basic", "Magenta", "AHPLMG-105"),
        AnycubicSkuEntry("PLA Basic", "Gelb", "AHPLVY-105"),
        AnycubicSkuEntry("PLA Basic", "Rosa", "AHPLSP-105"),
        AnycubicSkuEntry("PLA Basic", "Orange", "AHPLVO-105"),
        AnycubicSkuEntry("PLA Basic", "Braun", "AHPLBR-105"),
        AnycubicSkuEntry("PLA Basic", "Cyan", "AHPLCY-105"),
        AnycubicSkuEntry("PLA Basic", "Blau (Marineblau)", "AHPLDB-105"),
        AnycubicSkuEntry("PLA Basic", "Grüner Blitz", "AHPLGF-105"),
        AnycubicSkuEntry("PLA Basic", "Dunkelbraun", "AHPLKB-105"),
        AnycubicSkuEntry("PLA Basic", "Grau", "AHPLGE-106"),
        // PLA High Speed
        AnycubicSkuEntry("PLA High Speed", "Perlschwarz", "AHHSBK-102"),
        AnycubicSkuEntry("PLA High Speed", "Strukturgrau", "AHHSGY-102"),
        AnycubicSkuEntry("PLA High Speed", "Strahlendes Weiß", "AHHSBW-102"),
        AnycubicSkuEntry("PLA High Speed", "Leuchtendes Rot", "AHHSBR-102"),
        AnycubicSkuEntry("PLA High Speed", "Strahlendes Blau", "AHHSDB-102"),
        AnycubicSkuEntry("PLA High Speed", "Klassisches Grün", "AHHSCG-102"),
        AnycubicSkuEntry("PLA High Speed", "Lebendiges Orange", "AHHSVO-102"),
        AnycubicSkuEntry("PLA High Speed", "Erdbeerrosa", "AHHSSP-102"),
        AnycubicSkuEntry("PLA High Speed", "Leuchtendes Gelb", "AHHSVY-102"),
        AnycubicSkuEntry("PLA High Speed", "Prachtlila", "AHHSPO-102"),
        // PLA+
        AnycubicSkuEntry("PLA+", "Strukturgrau", "AHPLPGY-102"),
        AnycubicSkuEntry("PLA+", "Schwarz", "AHPLPBK-102"),
        AnycubicSkuEntry("PLA+", "Weiß", "AHPLPBW-102"),
        AnycubicSkuEntry("PLA+", "Blau", "AHPLPDB-102"),
        AnycubicSkuEntry("PLA+", "Grüner Blitz", "AHPLPGF-102"),
        AnycubicSkuEntry("PLA+", "Orange", "AHPLPVO-102"),
        AnycubicSkuEntry("PLA+", "Lila", "AHPLPPO-103"),
        AnycubicSkuEntry("PLA+", "Leuchtendes Rot", "AHPLPBR-106"),
        AnycubicSkuEntry("PLA+", "Silber", "AHPLPSL-102"),
        AnycubicSkuEntry("PLA+", "Gelb", "AHPLPVY-102"),
        AnycubicSkuEntry("PLA+", "Hellblau", "AHPLPLB-106"),
        AnycubicSkuEntry("PLA+", "Pfirsichrosa", "AHPLP16-107"),
        AnycubicSkuEntry("PLA+", "Interstellar Violett", "AHPLP17-107"),
        AnycubicSkuEntry("PLA+", "Tropisches Türkis", "AHPLP18-107"),
        AnycubicSkuEntry("PLA+", "Frühlingsblatt", "AHPLP19-107"),
        AnycubicSkuEntry("PLA+", "Grün", "AHPLPCG-107"),
        AnycubicSkuEntry("PLA+", "Grau", "AHPLPGE-107"),
        AnycubicSkuEntry("PLA+", "Rot", "AHPLPRR-107"),
        AnycubicSkuEntry("PLA+", "Rosa", "AHPLPSP-107"),
        AnycubicSkuEntry("PLA+", "Braun", "AHPLPBR-107"),
        AnycubicSkuEntry("PLA+", "Beige", "AHPLPLB-108"),
        // PLA Basic Refill
        AnycubicSkuEntry("PLA Basic Refill", "Schwarz", "AHPLBK-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Strukturgrau", "AHPLGY-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Weiß", "AHPLBW-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Rot", "AHPLRR-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Rosa", "AHPLSP-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Orange", "AHPLVO-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Gelb", "AHPLVY-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Grün", "AHPLCG-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Blau", "AHPLDB-A106"),
        AnycubicSkuEntry("PLA Basic Refill", "Lila", "AHPLPO-A106"),
        // PETG
        AnycubicSkuEntry("PETG", "Schwarz", "AHPEBK-101"),
        AnycubicSkuEntry("PETG", "Weiß", "AHPEBW-101"),
        AnycubicSkuEntry("PETG", "Strukturgrau", "AHPEGY-101"),
        AnycubicSkuEntry("PETG", "Orange", "AHPEVO-101"),
        AnycubicSkuEntry("PETG", "Lila", "AHPEPO-101"),
        AnycubicSkuEntry("PETG", "Blau", "AHPEDB-101"),
        AnycubicSkuEntry("PETG", "Klar", "AHPECL-101"),
        AnycubicSkuEntry("PETG", "Grün", "AHPECG-101"),
        AnycubicSkuEntry("PETG", "Gelb", "AHPEVY-101"),
        AnycubicSkuEntry("PETG", "See-Blau", "AHPEAB-101"),
        AnycubicSkuEntry("PETG", "Braun", "AHPEBR-101"),
        AnycubicSkuEntry("PETG", "Creme", "AHPECR-101"),
        AnycubicSkuEntry("PETG", "Dunkelgrau", "AHPEDG-101"),
        AnycubicSkuEntry("PETG", "Waldgrün", "AHPEFG-101"),
        AnycubicSkuEntry("PETG", "Grau", "AHPEGE-101"),
        AnycubicSkuEntry("PETG", "Beige", "AHPELB-101"),
        AnycubicSkuEntry("PETG", "Limettengrün", "AHPELG-101"),
        AnycubicSkuEntry("PETG", "Erdnussbraun", "AHPEPB-101"),
        AnycubicSkuEntry("PETG", "Struktursilber", "AHPESL-101"),
        AnycubicSkuEntry("PETG", "Rosa", "AHPESP-101"),
        AnycubicSkuEntry("PETG", "Rot", "AHPERR-101"),
        // PLA Spezial
        AnycubicSkuEntry("PLA Spezial", "Tropisches Türkis", "HPL18-105"),
        AnycubicSkuEntry("PLA Spezial", "Frühlingsblatt", "HPL19-105"),
        AnycubicSkuEntry("PLA Spezial", "Pfirsichrosa", "HPL16-105"),
        AnycubicSkuEntry("PLA Spezial", "Interstellar Violett", "HPL17-105"),
        // ASA
        AnycubicSkuEntry("ASA", "Weiß", "HASWH-101"),
        AnycubicSkuEntry("ASA", "Schwarz", "HASBK-101"),
        AnycubicSkuEntry("ASA", "Grau", "HASGY-101"),
        AnycubicSkuEntry("ASA", "Rot", "HASRE-101"),
        AnycubicSkuEntry("ASA", "Grün", "HASGR-102"),
        AnycubicSkuEntry("ASA", "Blau", "HASBL-102"),
        // PLA Matte
        AnycubicSkuEntry("PLA Matte", "Schwarz", "HYGBK-101"),
        AnycubicSkuEntry("PLA Matte", "Weiß", "HYGWT-101"),
        AnycubicSkuEntry("PLA Matte", "Grau", "HYGGY-102"),
        AnycubicSkuEntry("PLA Matte", "Rot", "HYGRE-102"),
        AnycubicSkuEntry("PLA Matte", "Sakura-Rosa", "HYGSP-102"),
        AnycubicSkuEntry("PLA Matte", "Rot-Blau", "HYGRB-102"),
        AnycubicSkuEntry("PLA Matte", "Lila", "HYGPU-102"),
        AnycubicSkuEntry("PLA Matte", "Orange", "HYGOR-102"),
        AnycubicSkuEntry("PLA Matte", "Eisblau", "HYGIB-102"),
        AnycubicSkuEntry("PLA Matte", "Grün", "HYGGR-102"),
        AnycubicSkuEntry("PLA Matte", "Grün-Braun", "HYGGB-102"),
        AnycubicSkuEntry("PLA Matte", "Blau", "HYGBL-102"),
        AnycubicSkuEntry("PLA Matte", "Gelb", "HYGYL-101"),
        // PETG Translucent
        AnycubicSkuEntry("PETG Translucent", "Klar", "AHPECL-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Blau", "AHPETB-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Grau", "AHPETG-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Rosa", "AHPETK-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Olivgrün", "AHPETL-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Orange", "AHPETO-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Lila", "AHPETP-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Grün", "AHPETR-101"),
        AnycubicSkuEntry("PETG Translucent", "Transparentes Braun", "AHPETW-101"),
        // PLA Silk
        AnycubicSkuEntry("PLA Silk", "Regenbogen", "AHSCRB1-102"),
        AnycubicSkuEntry("PLA Silk", "Silber", "AHSCSL-102"),
        AnycubicSkuEntry("PLA Silk", "Gold", "AHSCGD-102"),
        AnycubicSkuEntry("PLA Silk", "Weiß", "AHSCWH-102"),
        AnycubicSkuEntry("PLA Silk", "Metallblau", "HSCML-101"),
        AnycubicSkuEntry("PLA Silk", "Grau", "HSCGY-101"),
        AnycubicSkuEntry("PLA Silk", "Glänzendes Gold", "AHSCSG-102"),
        AnycubicSkuEntry("PLA Silk", "Weihnachtsgrün", "AHSCCG-102"),
        AnycubicSkuEntry("PLA Silk", "Weihnachtsrot", "AHSCCR-102"),
        AnycubicSkuEntry("PLA Silk", "Kupfer", "AHSCCO-102"),
        AnycubicSkuEntry("PLA Silk", "Grün", "AHSCGR-102"),
        AnycubicSkuEntry("PLA Silk", "Rosa", "AHSCPK-102"),
        AnycubicSkuEntry("PLA Silk", "Lila", "AHSCPU-102"),
        AnycubicSkuEntry("PLA Silk", "Blau", "AHSCBL-102"),
        // TPU Filament
        AnycubicSkuEntry("TPU Filament", "Schwarz", "HTPBK-101"),
        AnycubicSkuEntry("TPU Filament", "Milchweiß", "HTPMW-101"),
        AnycubicSkuEntry("TPU Filament", "Grau", "HTPGY-101"),
        AnycubicSkuEntry("TPU Filament", "Klar Blau", "HTPCB-101"),
        AnycubicSkuEntry("TPU Filament", "Klar Grün", "HTPCG-101"),
        AnycubicSkuEntry("TPU Filament", "Klar", "HTPCL-101"),
        AnycubicSkuEntry("TPU Filament", "Klar Orange", "HTPCO-101"),
        AnycubicSkuEntry("TPU Filament", "Klar Lila", "HTPCP-101"),
        AnycubicSkuEntry("TPU Filament", "Klar Rot", "HTPCR-101"),
        // PLA+ Refill
        AnycubicSkuEntry("PLA+ Refill", "Schwarz", "AHPLPBK-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Weiß", "AHPLPBW-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Strukturgrau", "AHPLPGY-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Rot", "AHPLPRR-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Blau", "AHPLPDB-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Gelb", "AHPLPVY-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Grün", "AHPLPCG-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Orange", "AHPLPVO-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Lila", "AHPLPPO-A108"),
        AnycubicSkuEntry("PLA+ Refill", "Rosa", "AHPLPSP-A108"),
        // PLA Silk Dual/Tri-Color
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Schwarz Gold", "AHSC07-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Schwarz Blau", "AHSC08-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Schwarz Grün", "AHSC09-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Schwarz Lila", "AHSC10-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Rosa Gold", "AHSC11-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Rot Gold", "AHSC12-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Rot Blau", "AHSC13-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Blau Grün Lila", "AHSC14-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Rot Gelb Grün", "AHSC15-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Blau Grün", "AHSC16-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Schwarz Rot", "AHSC17-102"),
        AnycubicSkuEntry("PLA Silk Dual/Tri-Color", "Rot Blau Gelb", "AHSC18-102"),
        // ABS Filament
        AnycubicSkuEntry("ABS Filament", "Schwarz", "HABBK-102"),
        AnycubicSkuEntry("ABS Filament", "Weiß", "HABWH-102"),
        AnycubicSkuEntry("ABS Filament", "Grau", "HABGY-102"),
        // PLA Galaxy
        AnycubicSkuEntry("PLA Galaxy", "Galaxy Braun", "AHXKGB-101"),
        AnycubicSkuEntry("PLA Galaxy", "Galaxy Grün", "AHXKGG-101"),
        AnycubicSkuEntry("PLA Galaxy", "Galaxy Blau", "AHXKGL-101"),
        AnycubicSkuEntry("PLA Galaxy", "Galaxy Lila", "AHXKGP-101"),
        // PLA Marble
        AnycubicSkuEntry("PLA Marble", "Marmorweiß", "AHLSMW-101"),
        AnycubicSkuEntry("PLA Marble", "Ziegelrot", "AHLSBR-101"),
        // PLA Metal
        AnycubicSkuEntry("PLA Metal", "Metall Schwarz", "HJSMB-101"),
        AnycubicSkuEntry("PLA Metal", "Metall Champagner", "HJSMC-101"),
        AnycubicSkuEntry("PLA Metal", "Metall Blau", "HJSML-101"),
        AnycubicSkuEntry("PLA Metal", "Metall Kupfer", "HJSMO-101"),
        // PLA Glow
        AnycubicSkuEntry("PLA Glow", "Nachleuchtendes Blau", "HFGBL-101"),
        AnycubicSkuEntry("PLA Glow", "Nachleuchtendes Grün", "HFGGR-101"),
        AnycubicSkuEntry("PLA Glow", "Nachleuchtendes Gelb", "HFGYE-103"),
        AnycubicSkuEntry("PLA Glow", "Blau", "HFGBE-103"),
        AnycubicSkuEntry("PLA Glow", "Grün", "HFGGN-103"),
    )

    /** Deutsche/englische Farbwörter -> kanonisches Token, über das abgeglichen wird. */
    private val colorWordTokens: Map<String, String> = mapOf(
        "weiss" to "WHITE", "white" to "WHITE",
        "schwarz" to "BLACK", "black" to "BLACK",
        "grau" to "GREY", "grey" to "GREY", "gray" to "GREY", "charcoal" to "GREY", "anthracite" to "GREY",
        "silber" to "SILVER", "silver" to "SILVER",
        "rot" to "RED", "red" to "RED", "scarlet" to "RED", "crimson" to "RED",
        "blau" to "BLUE", "blue" to "BLUE",
        "marineblau" to "NAVY", "navy" to "NAVY",
        "grun" to "GREEN", "green" to "GREEN",
        "olivgrun" to "OLIVE", "olive" to "OLIVE",
        "gelb" to "YELLOW", "yellow" to "YELLOW",
        "orange" to "ORANGE",
        "lila" to "PURPLE", "violett" to "PURPLE", "violet" to "PURPLE", "purple" to "PURPLE", "lilac" to "PURPLE",
        "rosa" to "PINK", "pink" to "PINK", "rose" to "PINK",
        "braun" to "BROWN", "brown" to "BROWN",
        "beige" to "BEIGE", "tan" to "BEIGE", "creme" to "BEIGE", "cream" to "BEIGE",
        "klar" to "CLEAR", "clear" to "CLEAR", "transparent" to "CLEAR",
        "transparentes" to "CLEAR", "transparente" to "CLEAR", "transparenter" to "CLEAR",
        "cyan" to "CYAN", "turkis" to "CYAN", "turquoise" to "CYAN", "teal" to "CYAN",
        "gold" to "GOLD", "champagner" to "GOLD", "champagne" to "GOLD",
        "bronze" to "BRONZE",
        "kupfer" to "COPPER", "copper" to "COPPER",
        "magenta" to "MAGENTA", "fuchsia" to "MAGENTA",
        "regenbogen" to "RAINBOW", "rainbow" to "RAINBOW",
        "lime" to "LIME",
    )

    /** Farbwörter ab 3 Zeichen, längste zuerst – für zusammengesetzte Wörter wie "Marmorweiß" oder "Ziegelrot". */
    private val colorWordRoots: List<Pair<String, String>> = colorWordTokens.entries
        .filter { it.key.length >= 3 }
        .map { it.key to it.value }
        .sortedByDescending { it.first.length }

    private val adjectiveSuffixes = listOf("es", "er", "em", "en", "e")

    private fun wordToToken(word: String): String? {
        colorWordTokens[word]?.let { return it }
        // Deutsche Adjektivendung abschneiden ("gruner" -> "grun", "weisses" -> "weiss").
        for (suffix in adjectiveSuffixes) {
            if (word.length > suffix.length + 2 && word.endsWith(suffix)) {
                colorWordTokens[word.removeSuffix(suffix)]?.let { return it }
            }
        }
        // Zusammengesetztes Wort, Farbe steht am Ende ("marmorweiss", "prachtlila", "ziegelrot").
        for ((root, token) in colorWordRoots) {
            if (word.length > root.length && word.endsWith(root)) return token
        }
        return null
    }

    private fun germanize(text: String): String =
        text.lowercase().replace("ä", "a").replace("ö", "o").replace("ü", "u").replace("ß", "ss")

    /** Zerlegt einen Farbnamen in kanonische Farbwort-Tokens; unbekannte/schmückende Wörter (z.B. "Strahlendes") fallen weg. */
    private fun colorTokens(colorName: String): Set<String> =
        germanize(colorName).split(Regex("[^a-z0-9]+"))
            .filter { it.isNotBlank() }
            .mapNotNull(::wordToToken)
            .toSet()

    /**
     * Ordnet einen freien Typ-Text (z.B. "PLA", "PLA Silk", "PETG Translucent") einem der
     * Anycubic-Typ-Buckets in [entries] zu, oder null, wenn der Typ gar nicht zu Anycubic
     * passt (z.B. technische Verbundwerkstoffe wie PLA-CF/PC-ABS, die Anycubic hier nicht
     * führt – sonst würde z.B. "PLA-CF" fälschlich auf "PLA Basic" fallen).
     */
    private fun normalizeType(rawType: String): String? {
        val t = rawType.uppercase().replace(Regex("[^A-Z0-9+]"), " ").replace(Regex("\\s+"), " ").trim()
        if (t.isEmpty()) return null
        val words = t.split(" ").filter { it.isNotBlank() }
        if (words.any { it in setOf("PC", "PCTG", "CF", "GF", "WOOD") }) return null

        val hasPla = words.contains("PLA") || words.contains("PLA+")
        val hasPetg = words.contains("PETG")
        val hasAbs = words.contains("ABS")
        val hasAsa = words.contains("ASA")
        val hasTpu = words.contains("TPU")

        return when {
            hasPla && t.contains("SILK") && (t.contains("DUAL") || t.contains("TRI") || t.contains("MULTI")) ->
                "PLA Silk Dual/Tri-Color"
            hasPla && t.contains("SILK") -> "PLA Silk"
            hasPla && t.contains("GALAXY") -> "PLA Galaxy"
            hasPla && t.contains("MARBLE") -> "PLA Marble"
            hasPla && t.contains("METAL") -> "PLA Metal"
            hasPla && t.contains("GLOW") -> "PLA Glow"
            hasPla && (t.contains("MATTE") || t.contains("MATT")) -> "PLA Matte"
            hasPla && (t.contains("SPEZIAL") || t.contains("SPECIAL")) -> "PLA Spezial"
            hasPla && t.contains("SPEED") -> "PLA High Speed"
            hasPla && words.contains("PLA+") && t.contains("REFILL") -> "PLA+ Refill"
            hasPla && t.contains("REFILL") -> "PLA Basic Refill"
            hasPla && words.contains("PLA+") -> "PLA+"
            hasPetg && (t.contains("TRANSLUCENT") || t.contains("TRANSPARENT")) -> "PETG Translucent"
            hasPetg -> "PETG"
            hasAbs -> "ABS Filament"
            hasAsa -> "ASA"
            hasTpu -> "TPU Filament"
            hasPla -> "PLA Basic"
            else -> null
        }
    }

    /**
     * Sucht die passende Anycubic-SKU für Typ + Farbe. Erst ein exakter Namensvergleich
     * (z.B. "Grau" trifft gezielt den Eintrag "Grau", nicht "Strukturgrau"), sonst die
     * engste Übereinstimmung der Farbwort-Tokens innerhalb desselben Typ-Buckets. Liefert
     * null, wenn der Typ keinem Anycubic-Bucket zugeordnet werden kann oder keine Farbe
     * im Bucket passt – dann generiert [SkuGenerator] weiterhin automatisch eine SKU.
     */
    fun findSku(type: String, colorName: String): String? {
        if (colorName.isBlank()) return null
        val wantedType = normalizeType(type) ?: return null
        val candidates = entries.filter { it.type == wantedType }
        if (candidates.isEmpty()) return null

        val normalizedQuery = germanize(colorName).trim()
        candidates.firstOrNull { germanize(it.colorDe).trim() == normalizedQuery }?.let { return it.sku }

        val queryTokens = colorTokens(colorName)
        if (queryTokens.isEmpty()) return null
        return candidates
            .mapNotNull { entry ->
                val catalogTokens = colorTokens(entry.colorDe)
                if (catalogTokens.isEmpty()) return@mapNotNull null
                val isMatch = queryTokens.all { it in catalogTokens } || catalogTokens.all { it in queryTokens }
                if (!isMatch) return@mapNotNull null
                val distance = (catalogTokens - queryTokens).size + (queryTokens - catalogTokens).size
                entry.sku to distance
            }
            .minByOrNull { it.second }
            ?.first
    }
}
