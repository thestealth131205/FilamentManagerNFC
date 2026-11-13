package de.letheapp.filamentmanager.data.catalog

/**
 * Eingebaute Referenzdaten für Hersteller/Material/Farbe (vom Nutzer vorgegeben),
 * inkl. Hex-Farbwert und Düsen-/Betttemperatur-Bereich. Dient im Editor
 * (FilamentEditorViewModel) als Autofill-Quelle für Farbwahl und Temperaturen –
 * ersetzt NICHT die lernenden Stammdaten (ManufacturerEntity/FilamentTypeEntity/
 * FilamentColorEntity in StammdatenEntities.kt), die weiterhin für freie,
 * selbst angelegte Einträge zuständig bleiben.
 */
data class CatalogColor(val name: String, val hex: String)

data class CatalogMaterial(
    val type: String,
    val nozzleMin: Int,
    val nozzleMax: Int,
    val bedMin: Int,
    val bedMax: Int,
    val colors: List<CatalogColor>,
)

data class CatalogBrand(val id: String, val name: String, val materials: List<CatalogMaterial>)

object BrandCatalog {
    val brands: List<CatalogBrand> = listOf(
        CatalogBrand(
            id = "anycubic",
            name = "Anycubic",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 220, bedMin = 50, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Black", "#1B1B1B"),
                        CatalogColor("White", "#FFFFFF"),
                        CatalogColor("Grey", "#808080"),
                        CatalogColor("Red", "#D32F2F"),
                        CatalogColor("Blue", "#1976D2"),
                    ),
                ),
                CatalogMaterial(
                    type = "PETG", nozzleMin = 230, nozzleMax = 250, bedMin = 70, bedMax = 85,
                    colors = listOf(
                        CatalogColor("Black", "#1B1B1B"),
                        CatalogColor("White", "#FFFFFF"),
                        CatalogColor("Clear", "#EAEAEA"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "bambu_lab",
            name = "Bambu Lab",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 230, bedMin = 35, bedMax = 45,
                    colors = listOf(
                        CatalogColor("Bambu Green", "#00AE42"),
                        CatalogColor("Jade White", "#FFFFFF"),
                        CatalogColor("Matte Charcoal", "#2B2B2B"),
                        CatalogColor("Mandarin Orange", "#FF6A13"),
                        CatalogColor("Cobalt Blue", "#0056B8"),
                    ),
                ),
                CatalogMaterial(
                    type = "PETG", nozzleMin = 240, nozzleMax = 270, bedMin = 65, bedMax = 75,
                    colors = listOf(
                        CatalogColor("Black", "#1A1A1A"),
                        CatalogColor("White", "#F8F8F8"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "esun",
            name = "eSUN",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA+", nozzleMin = 205, nozzleMax = 225, bedMin = 60, bedMax = 65,
                    colors = listOf(
                        CatalogColor("Cold White", "#F5F7FA"),
                        CatalogColor("Black", "#181818"),
                        CatalogColor("Fire Engine Red", "#CE2029"),
                        CatalogColor("Peak Green", "#2E8B57"),
                        CatalogColor("Silver", "#A9ACB6"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "sunlu",
            name = "SUNLU",
            // PLA aus der allgemeinen Herstellerliste, PETG/ABS/ASA/TPU aus der
            // separat vom Nutzer nachgelieferten, ausführlicheren SUNLU-Tabelle.
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 200, nozzleMax = 230, bedMin = 50, bedMax = 65,
                    colors = listOf(
                        CatalogColor("Black", "#1C1C1C"),
                        CatalogColor("White", "#FFFFFF"),
                        CatalogColor("Grey", "#7D7D7D"),
                        CatalogColor("Silk Gold", "#D4AF37"),
                    ),
                ),
                CatalogMaterial(
                    type = "PETG", nozzleMin = 220, nozzleMax = 250, bedMin = 70, bedMax = 85,
                    colors = listOf(
                        CatalogColor("Black", "#1A1A1A"),
                        CatalogColor("White", "#FFFFFF"),
                        CatalogColor("Grey", "#7A7A7A"),
                        CatalogColor("Clear / Transparent", "#E8ECEF"),
                        CatalogColor("Red", "#C62828"),
                        CatalogColor("Blue", "#1565C0"),
                        CatalogColor("Green", "#2E7D32"),
                        CatalogColor("Yellow", "#F9A825"),
                        CatalogColor("Orange", "#EF6C00"),
                    ),
                ),
                CatalogMaterial(
                    type = "ABS", nozzleMin = 240, nozzleMax = 270, bedMin = 90, bedMax = 110,
                    colors = listOf(
                        CatalogColor("Black", "#191919"),
                        CatalogColor("White", "#F9F9F9"),
                        CatalogColor("Grey", "#707070"),
                        CatalogColor("Red", "#B71C1C"),
                        CatalogColor("Blue", "#0D47A1"),
                    ),
                ),
                CatalogMaterial(
                    type = "ASA", nozzleMin = 240, nozzleMax = 270, bedMin = 90, bedMax = 110,
                    colors = listOf(
                        CatalogColor("Black", "#1C1C1C"),
                        CatalogColor("White", "#FAFAFA"),
                        CatalogColor("Grey", "#787878"),
                    ),
                ),
                CatalogMaterial(
                    // Herstellerbezeichnung "TPU 95A" – Typ bewusst auf "TPU" gemappt,
                    // damit es zur bestehenden Typ-Stammdatenliste passt.
                    type = "TPU", nozzleMin = 205, nozzleMax = 230, bedMin = 40, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Black", "#1C1C1C"),
                        CatalogColor("White", "#F7F7F7"),
                        CatalogColor("Red", "#D32F2F"),
                        CatalogColor("Blue", "#1976D2"),
                        CatalogColor("Transparent", "#ECEFF1"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "polymaker",
            name = "Polymaker",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 230, bedMin = 25, bedMax = 60,
                    colors = listOf(
                        CatalogColor("PolyTerra Charcoal Black", "#262626"),
                        CatalogColor("PolyTerra Cotton White", "#F2F0EB"),
                        CatalogColor("PolyTerra Army Green", "#4B5320"),
                        CatalogColor("PolyLite True Blue", "#0047AB"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "prusament",
            name = "Prusament",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 205, nozzleMax = 225, bedMin = 40, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Prusa Orange", "#FA6831"),
                        CatalogColor("Galaxy Black", "#212124"),
                        CatalogColor("Jet Black", "#141414"),
                        CatalogColor("Azure Blue", "#007FFF"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "elegoo",
            name = "ELEGOO",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 220, bedMin = 50, bedMax = 65,
                    colors = listOf(
                        CatalogColor("Black", "#1A1A1A"),
                        CatalogColor("White", "#FFFFFF"),
                        CatalogColor("Space Grey", "#5C6066"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "creality",
            name = "Creality",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 230, bedMin = 45, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Hyper Black", "#1B1B1B"),
                        CatalogColor("Hyper White", "#FAFAFA"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "overture",
            name = "OVERTURE",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 220, bedMin = 50, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Matte Black", "#222222"),
                        CatalogColor("Matte White", "#F4F4F4"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "jayo",
            name = "JAYO",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA+", nozzleMin = 205, nozzleMax = 225, bedMin = 50, bedMax = 65,
                    colors = listOf(
                        CatalogColor("Black", "#1C1C1C"),
                        CatalogColor("White", "#FFFFFF"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "eryone",
            name = "ERYONE",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 190, nozzleMax = 220, bedMin = 50, bedMax = 65,
                    colors = listOf(
                        CatalogColor("Burnt Titanium", "#3B4252"),
                        CatalogColor("Silk Copper", "#B87333"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "extrudr",
            name = "Extrudr",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 200, nozzleMax = 230, bedMin = 50, bedMax = 60,
                    colors = listOf(
                        CatalogColor("PLA NX2 Black", "#1D1D1F"),
                        CatalogColor("PLA NX2 White", "#F7F7F7"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "das_filament",
            name = "DAS FILAMENT",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 200, nozzleMax = 220, bedMin = 50, bedMax = 60,
                    colors = listOf(
                        CatalogColor("Tiefschwarz", "#141414"),
                        CatalogColor("Tonerweiß", "#F5F5F5"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "3djake",
            name = "3DJake",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 195, nozzleMax = 215, bedMin = 40, bedMax = 60,
                    colors = listOf(
                        CatalogColor("ecoPLA Black", "#1E1E1E"),
                        CatalogColor("ecoPLA White", "#FFFFFF"),
                    ),
                ),
            ),
        ),
        CatalogBrand(
            id = "formfutura",
            name = "FormFutura",
            materials = listOf(
                CatalogMaterial(
                    type = "PLA", nozzleMin = 195, nozzleMax = 220, bedMin = 50, bedMax = 60,
                    colors = listOf(
                        CatalogColor("EasyFil Black", "#191919"),
                        CatalogColor("EasyFil White", "#FCFCFC"),
                    ),
                ),
            ),
        ),
    )

    fun findBrand(name: String): CatalogBrand? =
        brands.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun findMaterial(brandName: String, type: String): CatalogMaterial? =
        findBrand(brandName)?.materials?.firstOrNull { it.type.equals(type, ignoreCase = true) }

    fun hexFor(brandName: String, type: String, colorName: String): String? =
        findMaterial(brandName, type)?.colors?.firstOrNull { it.name.equals(colorName, ignoreCase = true) }?.hex
}

/** "#RRGGBB" -> opakes ARGB-Int (0xFFRRGGBB). */
fun parseCatalogHex(hex: String): Int {
    val rgb = hex.removePrefix("#").toLong(16).toInt()
    return 0xFF000000.toInt() or rgb
}

/** ARGB-Int -> "#RRGGBB" (Alpha wird verworfen). */
fun toCatalogHex(argb: Int): String =
    "#" + (argb and 0x00FFFFFF).toString(16).padStart(6, '0').uppercase()
