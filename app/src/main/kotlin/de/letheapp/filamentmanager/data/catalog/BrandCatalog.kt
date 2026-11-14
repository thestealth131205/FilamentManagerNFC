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

    /**
     * Markenübergreifende, nicht herstellergebundene Farbnamen (bekannte Filament-
     * Marketingbezeichnungen wie "Ceramic" oder "Midnight Black"), die in keinem
     * konkreten [CatalogBrand] stehen, aber beim Etikett-Scan (OCR) oder bei der
     * manuellen Farbcode-Auswahl im Editor trotzdem einen passenden Hex-Wert liefern
     * sollen, statt unerkannt zu bleiben bzw. beim Speichern fälschlich auf Schwarz
     * zurückzufallen (siehe FilamentEditorViewModel.toEntity()).
     */
    val commonColors: List<CatalogColor> = listOf(
        CatalogColor("Ceramic", "#F3EFE6"),
        CatalogColor("Ceramic White", "#F3EFE6"),
        // Firmenname bewusst mit leichtem Blauanteil statt reinem Schwarz, wie
        // bei dieser Filament-Farbe üblich.
        CatalogColor("Midnight Black", "#14141E"),
        CatalogColor("Jet Black", "#141414"),
        CatalogColor("Galaxy Black", "#1E1E2A"),
        CatalogColor("Charcoal", "#2B2B2B"),
        CatalogColor("Space Grey", "#5C6066"),
        CatalogColor("Titanium", "#70726E"),
        CatalogColor("Natural", "#E8DFC8"),
        CatalogColor("Beige", "#D8C8A8"),
        CatalogColor("Ivory", "#F2EEDD"),
        CatalogColor("Cream", "#F5EBCB"),
        CatalogColor("Skin", "#E8B998"),
        CatalogColor("Marble White", "#EDEAE2"),
        CatalogColor("Marble", "#C9C3B8"),
        CatalogColor("Wood", "#A1795A"),
        CatalogColor("Copper", "#B87333"),
        CatalogColor("Bronze", "#8C7853"),
        CatalogColor("Rose Gold", "#E0BFB8"),
        CatalogColor("Champagne", "#E8D5A8"),
        CatalogColor("Navy Blue", "#1B2A4A"),
        CatalogColor("Sky Blue", "#7EC8E3"),
        CatalogColor("Ice Blue", "#C5E8F0"),
        CatalogColor("Teal", "#008080"),
        CatalogColor("Mint Green", "#A8E6C1"),
        CatalogColor("Lime Green", "#9ACD32"),
        CatalogColor("Army Green", "#4B5320"),
        CatalogColor("Olive", "#6B6B2A"),
        CatalogColor("Magenta", "#D5006D"),
        CatalogColor("Fuchsia", "#C2185B"),
        CatalogColor("Peach", "#FFCBA4"),
        CatalogColor("Coral", "#FF6F61"),
        CatalogColor("Burgundy", "#6D071A"),
        CatalogColor("Wine Red", "#722F37"),
        CatalogColor("Maroon", "#800000"),
        CatalogColor("Glow in the Dark", "#D8F0C0"),

        // Erweiterte, vom Nutzer nachgelieferte Sammlung (230 Markenfarben,
        // bereits dedupliziert gegen die obige handkuratierte Liste – bei
        // Namenskollision gewinnt der obige Eintrag). Reine Namens->Hex-
        // Zuordnung ohne Herstellerbindung, siehe JSON-Quelle vom 2026-11-14.
        // Weiß & Natur
        CatalogColor("Keramik Weiß (Ceramic White)", "#F4F5F0"),
        CatalogColor("Jade White", "#FFFFFF"),
        CatalogColor("Ivory White", "#FFFFFF"),
        CatalogColor("Bone White", "#CBC6B8"),
        CatalogColor("Pristine White", "#E6EAED"),
        CatalogColor("Signal White", "#E5E0E1"),
        CatalogColor("Natural Transparent", "#E0DDCB"),
        CatalogColor("Vanilla White", "#D9D4C4"),
        CatalogColor("Pearl White", "#D0D7D6"),
        CatalogColor("Desert Tan", "#E8DBB7"),
        CatalogColor("Noctua Beige", "#E7CEB4"),
        CatalogColor("Natural / Clear PLA", "#F2F4F3"),
        CatalogColor("Cotton White", "#FAFAFA"),
        CatalogColor("Alabaster", "#ECEBE9"),
        CatalogColor("Eco Natural", "#E2DEC9"),
        CatalogColor("Linen", "#EFECE1"),
        CatalogColor("Milk Tea", "#D8C4B6"),
        CatalogColor("Raw White", "#F0F0F0"),
        CatalogColor("Oatmeal", "#D6CEBF"),
        CatalogColor("Transparent Clear", "#E4E7E3"),
        // Schwarz & Grau
        CatalogColor("Basic Black", "#000000"),
        CatalogColor("Matte Black", "#303635"),
        CatalogColor("Prusa Galaxy Black", "#3D3E3C"),
        CatalogColor("Anthracite Grey", "#3F4647"),
        CatalogColor("Gentleman's Grey", "#354046"),
        CatalogColor("Gravity Grey", "#9FA4A7"),
        CatalogColor("Marble Grey", "#B0B4B4"),
        CatalogColor("Urban Grey", "#999595"),
        CatalogColor("Basic Silver / Gray", "#A6A9AA"),
        CatalogColor("Lava Gray (CF)", "#4D5054"),
        CatalogColor("Dark Charcoal", "#383B3E"),
        CatalogColor("Slate Grey", "#595D61"),
        CatalogColor("Cool Gray", "#919599"),
        CatalogColor("Carbon Black", "#1A1A1A"),
        CatalogColor("Iron Gray", "#4B5155"),
        CatalogColor("Concrete Grey", "#7C8184"),
        CatalogColor("Onyx Black", "#212121"),
        CatalogColor("Smoky Black Transparent", "#74716C"),
        CatalogColor("Gunmetal Grey", "#53595E"),
        CatalogColor("Space Gray", "#565A5E"),
        CatalogColor("Graphite", "#363A3E"),
        CatalogColor("Ash Gray", "#8C9296"),
        CatalogColor("Night Black", "#111111"),
        CatalogColor("Silver Fox", "#A0A5A9"),
        // Rot, Magenta & Pink
        CatalogColor("Basic Red", "#C00D1E"),
        CatalogColor("Blood Red", "#990303"),
        CatalogColor("Lipstick Red", "#D03036"),
        CatalogColor("Carmine Red Transparent", "#B5422F"),
        CatalogColor("CF Burgundy Red", "#951E23"),
        CatalogColor("Ms. Pink", "#E34A93"),
        CatalogColor("Cherry Red", "#EA4A5D"),
        CatalogColor("Neon Pink", "#FF1493"),
        CatalogColor("Ruby Red", "#D21F3C"),
        CatalogColor("Pastel Pink", "#FFB6C1"),
        CatalogColor("Flamingo Pink", "#FA8072"),
        CatalogColor("Rose Red", "#E63946"),
        CatalogColor("Crimson", "#990000"),
        CatalogColor("Hot Magenta", "#FF007F"),
        CatalogColor("Brick Red", "#CB4154"),
        CatalogColor("Raspberry", "#E30B5C"),
        CatalogColor("Blush Pink", "#FFC0CB"),
        CatalogColor("Salmon", "#FA8072"),
        CatalogColor("Scarlet Red", "#FF2400"),
        CatalogColor("Dark Red", "#8B0000"),
        // Blau & Cyan
        CatalogColor("Basic Blue", "#00358E"),
        CatalogColor("Azure Blue", "#489FDF"),
        CatalogColor("Basic Cyan", "#0086D6"),
        CatalogColor("Azure Blue (Prusa)", "#0682AC"),
        CatalogColor("Royal Blue", "#04518E"),
        CatalogColor("Chalky Blue", "#6F9AAA"),
        CatalogColor("Ocean Blue", "#1A4C54"),
        CatalogColor("Sapphire Blue", "#3B4467"),
        CatalogColor("CF Jeans Blue", "#6E88BC"),
        CatalogColor("Electric Blue", "#0066CC"),
        CatalogColor("Cobalt Blue", "#0047AB"),
        CatalogColor("Baby Blue", "#B0E0E6"),
        CatalogColor("Turquoise", "#00C5CD"),
        CatalogColor("Midnight Blue", "#191970"),
        CatalogColor("Teal Blue", "#008080"),
        CatalogColor("Denim Blue", "#1560BD"),
        CatalogColor("Pacific Blue", "#1CA9C9"),
        CatalogColor("Prussian Blue", "#003153"),
        CatalogColor("Cyan Blue", "#00B7EB"),
        CatalogColor("Dark Navy", "#000080"),
        CatalogColor("Periwinkle", "#CCCCFF"),
        CatalogColor("Aqua Blue", "#00FFFF"),
        // Grün-Töne
        CatalogColor("Basic Green", "#00AE42"),
        CatalogColor("Mistletoe Green", "#3F8E43"),
        CatalogColor("Olive (ABS)", "#789D4A"),
        CatalogColor("CF Matcha Green", "#5C9748"),
        CatalogColor("Galaxy Green", "#426639"),
        CatalogColor("Opal Green", "#075B49"),
        CatalogColor("Jungle Green", "#007248"),
        CatalogColor("Simply Green", "#70A640"),
        CatalogColor("Pistachio Green", "#93A683"),
        CatalogColor("Prusa Pro Green", "#00C389"),
        CatalogColor("Emerald Green", "#009B77"),
        CatalogColor("Forest Green", "#228B22"),
        CatalogColor("Sage Green", "#87AE73"),
        CatalogColor("Pine Green", "#01796F"),
        CatalogColor("Apple Green", "#8DB600"),
        CatalogColor("Fluorescent Green", "#00FF00"),
        CatalogColor("Hunter Green", "#355E3B"),
        CatalogColor("Bottle Green", "#006A4E"),
        CatalogColor("Neon Green Transparent", "#A6EC5E"),
        CatalogColor("Seafoam Green", "#98FF98"),
        CatalogColor("Dark Green", "#006400"),
        CatalogColor("Chartreuse", "#7FFF00"),
        // Gelb & Orange
        CatalogColor("Prusa Orange", "#FE6E31"),
        CatalogColor("Prusa Orange PETG", "#EB5403"),
        CatalogColor("Mango Yellow", "#F2B706"),
        CatalogColor("Pineapple Yellow", "#EFD006"),
        CatalogColor("Basic Yellow", "#FCE300"),
        CatalogColor("Sunny Yellow", "#FFD700"),
        CatalogColor("Pumpkin Orange", "#FF7518"),
        CatalogColor("Neon Yellow", "#CCFF00"),
        CatalogColor("Golden Yellow", "#FFCC00"),
        CatalogColor("Mustard Yellow", "#FFDB58"),
        CatalogColor("Tangerine Orange", "#FA8128"),
        CatalogColor("Lemon Yellow", "#FFF700"),
        CatalogColor("Amber Yellow", "#FFBF00"),
        CatalogColor("Safety Orange", "#FF6700"),
        CatalogColor("Apricot", "#FBCEB1"),
        CatalogColor("Cadmium Yellow", "#FFF600"),
        CatalogColor("Burnt Orange", "#CC5500"),
        CatalogColor("Cream Yellow", "#FFFDD0"),
        CatalogColor("Fluorescent Orange", "#FF5F1F"),
        CatalogColor("Corn Yellow", "#B37B46"),
        CatalogColor("Light Yellow Transparent", "#E6BE1E"),
        CatalogColor("Bronze Orange", "#CD7F32"),
        CatalogColor("Dark Yellow", "#FFC000"),
        CatalogColor("Mandarin", "#F37A48"),
        CatalogColor("Saffron", "#F4C430"),
        // Violett & Braun
        CatalogColor("Basic Purple", "#6E3FA3"),
        CatalogColor("Galaxy Purple PLA", "#453A71"),
        CatalogColor("Galaxy Purple (Standard)", "#0F0939"),
        CatalogColor("Shimmering Violet", "#1D1991"),
        CatalogColor("Noctua Brown", "#653525"),
        CatalogColor("Mystic Brown", "#482E31"),
        CatalogColor("Mystic Green (Dark/Brownish)", "#555845"),
        CatalogColor("Woodfill Linden Light", "#D2B692"),
        CatalogColor("Algae Pigment", "#674B41"),
        CatalogColor("Wine Pigment", "#5E493F"),
        CatalogColor("Latte Brown", "#D3B7A7"),
        CatalogColor("Dark Violet", "#4A0E4E"),
        CatalogColor("Lavender", "#E6E6FA"),
        CatalogColor("Chocolate Brown", "#5C4033"),
        CatalogColor("Lilac", "#C8A2C8"),
        CatalogColor("Plum", "#8E4585"),
        CatalogColor("Mahogany", "#4A2E1B"),
        CatalogColor("Amethyst Purple", "#9966CC"),
        CatalogColor("Coffee", "#6F4E37"),
        CatalogColor("Grape", "#6F2DA8"),
        CatalogColor("Chestnut", "#954535"),
        CatalogColor("Violet", "#7F00FF"),
        CatalogColor("Dark Cocoa", "#3D2314"),
        CatalogColor("Taro Purple", "#B59EAD"),
        CatalogColor("Deep Plum", "#301934"),
        // Silk & Metallic
        CatalogColor("Silk Gold", "#FFC600"),
        CatalogColor("Silk Silver", "#97999B"),
        CatalogColor("Silk Copper", "#5E4B3C"),
        CatalogColor("Silk Blue", "#147BD1"),
        CatalogColor("Basic Gold", "#E4BD64"),
        CatalogColor("Basic Bronze", "#84754E"),
        CatalogColor("Oh My Gold", "#CE9F2D"),
        CatalogColor("My Silverness", "#AEB8C0"),
        CatalogColor("Viva La Bronze", "#B38E3E"),
        CatalogColor("Silk Bronze", "#A77B43"),
        CatalogColor("Silk Emerald Green", "#2E8B57"),
        CatalogColor("Silk Purple", "#8A2BE2"),
        CatalogColor("Silk Champagne", "#F7E7CE"),
        CatalogColor("Silk Red", "#D2143A"),
        CatalogColor("Metallic Silver", "#B0C4DE"),
        CatalogColor("Metallic Anthracite", "#38454F"),
        CatalogColor("Silk Rose Gold", "#B76E79"),
        CatalogColor("Silk Light Blue", "#87CEEB"),
        CatalogColor("Brass Metallic", "#B5A642"),
        CatalogColor("Silk Magenta", "#CA1F7B"),
        CatalogColor("Titanium Metallic", "#878787"),
        CatalogColor("Silk Yellow", "#FFD700"),
        CatalogColor("Silk Copper Brown", "#B87333"),
        CatalogColor("Silk White Pearl", "#F0F8FF"),
        CatalogColor("Liquid Platinum", "#D8D8D8"),
        // Spezial (Glow / UV)
        CatalogColor("Glow in the Dark Green", "#98FB98"),
        CatalogColor("Glow in the Dark Blue", "#B4EEB4"),
        CatalogColor("Glow in the Dark Cyan", "#AFEEEE"),
        CatalogColor("Glow in the Dark Rainbow", "#E0FFFF"),
        CatalogColor("Glow Yellow / Orange", "#FFD700"),
        CatalogColor("UV Reactive Pink", "#FF69B4"),
        CatalogColor("UV Reactive Blue", "#1E90FF"),
        CatalogColor("Glow Red", "#FFA07A"),
        CatalogColor("Glow Purple", "#D8BFD8"),
        CatalogColor("Glow White-to-Green", "#F5FFFA"),
        CatalogColor("UV Reactive Orange", "#FFA500"),
        CatalogColor("Glow Neutral / Transparent", "#FAFAD2"),
        CatalogColor("Glow Multi-Color Sparkle", "#E6FAFA"),
        CatalogColor("UV Reactive Yellow", "#FFFF00"),
        CatalogColor("Glow Aqua", "#00FFFF"),
        // Gradient & Coextrusion
        CatalogColor("Rainbow Silk (Multi-Color Gradient)", "#FF4500"),
        CatalogColor("Sunset Gradient (Red-Orange-Yellow)", "#FF7F50"),
        CatalogColor("Galaxy Space Gradient (Blue-Purple-Black)", "#483D8B"),
        CatalogColor("Forest Autumn Gradient (Green-Brown-Gold)", "#8B4513"),
        CatalogColor("Coextrusion Red-Blue (Dual-Color)", "#8B008B"),
        CatalogColor("Coextrusion Gold-Green (Dual-Color)", "#556B2F"),
        CatalogColor("Tri-Color Silk (Red-Green-Blue)", "#4682B4"),
        CatalogColor("Pastel Rainbow Gradient", "#FFB6C1"),
        CatalogColor("Neon Gradient (Pink-Yellow-Green)", "#00FF7F"),
        CatalogColor("Ocean Breeze Gradient (Blue-Cyan-White)", "#00CED1"),
        CatalogColor("Coextrusion Purple-Green (Dual-Color)", "#4B0082"),
        CatalogColor("Coextrusion Bronze-Yellow (Dual-Color)", "#DAA520"),
        CatalogColor("Galaxy Nebula Gradient", "#6A5ACD"),
        CatalogColor("Spring Blossom Gradient (Pink-White-Purple)", "#D8BFD8"),
        CatalogColor("Coextrusion Black-Red (Dual-Color)", "#8B0000"),
        CatalogColor("Arctic Frost Gradient (White-Blue-Silver)", "#E0FFFF"),
        CatalogColor("Magma Flow Gradient (Red-Orange-Black)", "#FF4500"),
        CatalogColor("Quad-Color Silk (Gold-Silver-Copper-Black)", "#B8860B"),
    )

    fun findBrand(name: String): CatalogBrand? =
        brands.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun findMaterial(brandName: String, type: String): CatalogMaterial? =
        findBrand(brandName)?.materials?.firstOrNull { it.type.equals(type, ignoreCase = true) }

    fun hexFor(brandName: String, type: String, colorName: String): String? =
        findMaterial(brandName, type)?.colors?.firstOrNull { it.name.equals(colorName, ignoreCase = true) }?.hex

    /** Markenunabhängiger Fallback: sucht [colorName] in allen Katalogfarben aller Marken plus [commonColors]. */
    fun hexForAny(colorName: String): String? =
        (brands.flatMap { b -> b.materials.flatMap { it.colors } } + commonColors)
            .firstOrNull { it.name.equals(colorName, ignoreCase = true) }?.hex
}

/** "#RRGGBB" -> opakes ARGB-Int (0xFFRRGGBB). */
fun parseCatalogHex(hex: String): Int {
    val rgb = hex.removePrefix("#").toLong(16).toInt()
    return 0xFF000000.toInt() or rgb
}

/** ARGB-Int -> "#RRGGBB" (Alpha wird verworfen). */
fun toCatalogHex(argb: Int): String =
    "#" + (argb and 0x00FFFFFF).toString(16).padStart(6, '0').uppercase()
