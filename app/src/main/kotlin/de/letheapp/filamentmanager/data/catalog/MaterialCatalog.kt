package de.letheapp.filamentmanager.data.catalog

/**
 * Markenunabhängige Referenzdatenbank gängiger 3D-Druck-Materialien
 * (FDM/FFF, SLA/DLP/MSLA, SLS/MJF), vom Nutzer als Material-Übersicht
 * vorgegeben (JSON-Quelle vom 2026-11-14). Anders als [BrandCatalog]
 * (Temperaturen an eine konkrete Marke gebunden) liefert dies Default-
 * Düsen-/Betttemperaturen und Eigenschaften je Material-*Typ*, egal von
 * welchem Hersteller – Fallback für Etikett-Scan ([LabelOcrParser]) und
 * Editor-Autofill (FilamentEditorViewModel), wenn kein Markeneintrag passt
 * (z.B. unbekannte Marke oder Hochleistungspolymere wie PEEK/PEI, die im
 * Markenkatalog nicht hinterlegt sind).
 */
data class MaterialSpec(
    val kuerzel: String,
    val name: String,
    val category: String,
    val process: String = "FDM",
    val nozzleMin: Int? = null,
    val nozzleMax: Int? = null,
    val bedMin: Int? = null,
    val bedMax: Int? = null,
    val enclosureRecommended: Boolean = false,
    val properties: List<String> = emptyList(),
    val applications: List<String> = emptyList(),
)

object MaterialCatalog {
    val materials: List<MaterialSpec> = listOf(
        // Standard- & Einsteiger-Filamente (FDM)
        MaterialSpec(
            "PLA", "Polylactid (Polymilchsäure)", "Standard- & Einsteiger-Filamente", "FDM",
            190, 220, 50, 60, false,
            listOf("Biobasiert", "Sehr einfach zu drucken", "Kaum Warping", "Hohe Steifigkeit", "Spröde unter Last", "Geringe Temperaturbeständigkeit (~55°C)"),
            listOf("Prototypen", "Dekoartikel", "Modelle", "Architektur"),
        ),
        MaterialSpec(
            "PLA+", "Modifiziertes Polylactid (PLA Plus / Pro / Tough)", "Standard- & Einsteiger-Filamente", "FDM",
            205, 230, 55, 65, false,
            listOf("Höhere Schlagzähigkeit als Standard-PLA", "Bessere Layerhaftung", "Weniger spröde", "Einfach zu drucken"),
            listOf("Funktionale Prototypen", "Gehäuse", "Mechanisch leicht belastete Teile"),
        ),
        MaterialSpec(
            "PETG", "Polyethylenterephthalat-Glykol", "Standard- & Einsteiger-Filamente", "FDM",
            230, 255, 70, 85, false,
            listOf("Gute chemische Beständigkeit", "Zäh und leicht flexibel", "Temperaturbeständig bis ~75°C", "Neigt zu Stringing", "Hygroskopisch"),
            listOf("Mechanische Bauteile", "Halterungen", "Outdoor-Anwendungen", "Flüssigkeitsbehälter"),
        ),
        MaterialSpec(
            "PCTG", "Polycyclohexylendimethylenterephthalat-Glykol", "Standard- & Einsteiger-Filamente", "FDM",
            250, 270, 80, 100, false,
            listOf("Deutlich höhere Schlagzähigkeit als PETG", "Sehr hohe optische Klarheit", "Geringes Warping", "Chemikalienbeständig"),
            listOf("Hochbelastbare Funktionsteile", "Transparente Abdeckungen", "Werkzeuge"),
        ),

        // Technische & Konstruktions-Filamente (FDM)
        MaterialSpec(
            "ABS", "Acrylnitril-Butadien-Styrol", "Technische & Konstruktions-Filamente", "FDM",
            240, 270, 95, 110, true,
            listOf("Hohe Schlagzähigkeit", "Temperaturbeständig bis ~95°C", "Nachbearbeitbar mit Aceton", "Starkes Warping", "Entwickelt Styrol-Dämpfe"),
            listOf("Automobilteile", "Elektronikgehäuse", "Werkzeuge", "Klemmbausteine"),
        ),
        MaterialSpec(
            "ASA", "Acrylnitril-Styrol-Acrylat", "Technische & Konstruktions-Filamente", "FDM",
            245, 275, 95, 110, true,
            listOf("Extrem UV- und witterungsbeständig", "Ähnliche mechanische Werte wie ABS", "Temperaturbeständig bis ~95°C", "Warping-anfällig"),
            listOf("Außeneinsatz", "KFZ-Exterieur", "Garten- & Drohnenteile"),
        ),
        MaterialSpec(
            "PC", "Polycarbonat", "Technische & Konstruktions-Filamente", "FDM",
            270, 310, 100, 120, true,
            listOf("Extrem schlagzäh und bruchfest", "Temperaturbeständig bis ~115°C", "Sehr starkes Warping", "Stark hygroskopisch"),
            listOf("Hochbelastete Bauteile", "Schutzabdeckungen", "Lichtleiter", "Maschinenbau"),
        ),
        MaterialSpec(
            "PC-ABS", "Polycarbonat / ABS Blend", "Technische & Konstruktions-Filamente", "FDM",
            260, 290, 100, 115, true,
            listOf("Kombiniert Zähigkeit von PC mit Verarbeitbarkeit von ABS", "Hohe Wärmeformbeständigkeit", "Gute Oberflächenqualität"),
            listOf("Industriegehäuse", "Automobil-Innenraum", "Vorrichtungsbau"),
        ),
        MaterialSpec(
            "PA6", "Polyamid 6 (Nylon 6)", "Technische & Konstruktions-Filamente", "FDM",
            250, 285, 80, 100, true,
            listOf("Sehr hohe Zugfestigkeit und Abriebfestigkeit", "Gute Chemikalienresistenz", "Extrem hygroskopisch (muss getrocknet werden)"),
            listOf("Zahnräder", "Gleitlager", "Scharniere", "Mechanische Belastungsteile"),
        ),
        MaterialSpec(
            "PA12", "Polyamid 12 (Nylon 12)", "Technische & Konstruktions-Filamente", "FDM",
            250, 280, 80, 100, true,
            listOf("Geringere Wasseraufnahme als PA6", "Hohe Maßhaltigkeit", "Schlagzäh auch bei niedrigen Temperaturen", "Beständig gegen Öle und Fette"),
            listOf("Funktionsprototypen", "Schnappverbindungen", "Kraftstoffleitungen", "Drohnenrahmen"),
        ),
        MaterialSpec(
            "PP", "Polypropylen", "Technische & Konstruktions-Filamente", "FDM",
            220, 250, 85, 105, true,
            listOf("Extrem geringes Gewicht (Dichte < 1 g/cm³)", "Sehr hohe Chemikalienbeständigkeit", "Ideal für Filmscharniere", "Schwierige Druckbetthaftung (PP-Tape nötig)"),
            listOf("Filmscharniere", "Chemikalienbehälter", "Leichtbauteile", "Medizintechnik"),
        ),
        MaterialSpec(
            "POM", "Polyoxymethylen (Acetal / Delrin)", "Technische & Konstruktions-Filamente", "FDM",
            210, 230, 100, 120, true,
            listOf("Extrem niedriger Reibungskoeffizient", "Hohe Steifigkeit und Maßhaltigkeit", "Sehr schlechte Druckbetthaftung", "Starkes Warping"),
            listOf("Präzisionszahnräder", "Lagerbuchsen", "Gleitführungen"),
        ),
        MaterialSpec(
            "PMMA", "Polymethylmethacrylat (Acrylglas)", "Technische & Konstruktions-Filamente", "FDM",
            230, 260, 90, 110, true,
            listOf("Hohe Lichtdurchlässigkeit", "Kratzfester als PC", "UV-beständig", "Spröde unter Schlagbelastung"),
            listOf("Lichtdiffusoren", "Transparente Abdeckungen", "Display-Elemente"),
        ),
        MaterialSpec(
            "HIPS", "High Impact Polystyrene", "Technische & Konstruktions-Filamente", "FDM",
            230, 250, 95, 110, true,
            listOf("Löslich in D-Limonen", "Leicht und schlagzäh", "Mattes Finish", "Gute Schleifbarkeit"),
            listOf("Stützmaterial für ABS/ASA", "Modellbau", "Verpackungsprototypen"),
        ),

        // Flexible & Elastomere Filamente (FDM)
        MaterialSpec(
            "TPU", "Thermoplastisches Polyurethan (Shore 82A - 98A)", "Flexible & Elastomere Filamente", "FDM",
            215, 245, 40, 60, false,
            listOf("Hochelastisch und gummiartig", "Extrem abriebfest und reißfest", "Beständig gegen Öle und Fette", "Erfordert langsame Druckgeschwindigkeit"),
            listOf("Dichtungen", "Dämpfer", "Schutzhüllen", "Reifen", "Kabeltüllen"),
        ),
        MaterialSpec(
            "TPE", "Thermoplastisches Elastomer (Shore 60A - 85A)", "Flexible & Elastomere Filamente", "FDM",
            210, 240, 40, 60, false,
            listOf("Weicher und flexibler als Standard-TPU", "Soft-Touch-Oberfläche", "Schwierig mit Bowden-Extrudern zu drucken (Direct Drive nötig)"),
            listOf("Griffe", "Weiche Dichtungen", "Wearables", "Vibrationsdämpfer"),
        ),
        MaterialSpec(
            "TPC", "Thermoplastisches Copolyester", "Flexible & Elastomere Filamente", "FDM",
            220, 250, 60, 80, false,
            listOf("Hohe Temperatur- und UV-Beständigkeit", "Gute chemische Resistenz", "Hohe Rückstellelastizität"),
            listOf("Automobil-Bälge", "Industriedichtungen", "Outdoor-Elastomere"),
        ),
        MaterialSpec(
            "PEBA", "Polyetherblockamid", "Flexible & Elastomere Filamente", "FDM",
            220, 250, 50, 70, false,
            listOf("Extrem hohe Energierückgabe (Federung)", "Bleibt auch bei starkem Frost flexibel", "Sehr geringes Gewicht"),
            listOf("Sportsohlen", "Funktionsfedern", "Kältetechnik-Dichtungen"),
        ),

        // Faserverstärkte & Verbund-Filamente (Composites)
        MaterialSpec(
            "PLA-CF", "Carbonfaserverstärktes PLA", "Faserverstärkte & Verbund-Filamente", "FDM",
            200, 230, 50, 60, false,
            listOf("Erhöhte Steifigkeit", "Edle matte Oberfläche (kaschiert Layerlinien)", "Abrasiv (gehärtete Düse erforderlich)"),
            listOf("Optische Funktionsteile", "Gehäuse", "Halterungen ohne hohe Hitzebelastung"),
        ),
        MaterialSpec(
            "PETG-CF", "Carbonfaserverstärktes PETG", "Faserverstärkte & Verbund-Filamente", "FDM",
            240, 270, 75, 90, false,
            listOf("Hohe Steifigkeit und Formstabilität", "Geringeres Stringing als normales PETG", "Abrasiv (gehärtete Düse erforderlich)"),
            listOf("Drohnenteile", "Vorrichtungen", "Mechanische Abdeckungen"),
        ),
        MaterialSpec(
            "PA6-CF / PA12-CF", "Carbonfaserverstärktes Polyamid", "Faserverstärkte & Verbund-Filamente", "FDM",
            260, 300, 80, 105, true,
            listOf("Extrem hohe Festigkeit und Steifigkeit", "Temperaturbeständig bis über 140°C", "Deutlich reduziertes Warping gegenüber reinem Nylon", "Stark abrasiv"),
            listOf("Metallersatzteile", "Motorsport", "Drohnenrahmen", "Maschinenbau-Vorrichtungen"),
        ),
        MaterialSpec(
            "PA-GF", "Glasfaserverstärktes Polyamid", "Faserverstärkte & Verbund-Filamente", "FDM",
            260, 295, 80, 105, true,
            listOf("Hohe Schlagzähigkeit und Steifigkeit", "Einfärbbar (im Gegensatz zu schwarzem CF)", "Elektrisch isolierend", "Stark abrasiv"),
            listOf("Elektrowerkzeuggehäuse", "Strukturbauteile", "Automobilkomponenten"),
        ),
        MaterialSpec(
            "PC-CF", "Carbonfaserverstärktes Polycarbonat", "Faserverstärkte & Verbund-Filamente", "FDM",
            280, 310, 105, 120, true,
            listOf("Maximale Steifigkeit und Hitzebeständigkeit", "Geringeres Warping als reines PC", "Stark abrasiv"),
            listOf("Hochtemperatur-Vorrichtungen", "Strukturteile im Motorraum"),
        ),
        MaterialSpec(
            "ASA-CF / ABS-GF", "Faserverstärktes ASA / ABS", "Faserverstärkte & Verbund-Filamente", "FDM",
            255, 280, 95, 110, true,
            listOf("Deutlich reduziertes Warping", "Hohe Maßhaltigkeit", "UV-beständig (ASA-CF)", "Abrasiv"),
            listOf("Großformatige Außenteile", "KFZ-Anbauteile"),
        ),
        MaterialSpec(
            "Wood-PLA", "Holzgefülltes PLA (ca. 15-30% Holzfasern)", "Faserverstärkte & Verbund-Filamente", "FDM",
            195, 220, 50, 60, false,
            listOf("Holzoptik, -haptik und -geruch", "Schleif- und beizbar", "Neigt bei feinen Düsen (<0.4mm) zum Verstopfen"),
            listOf("Deko", "Skulpturen", "Möbelgriffe", "Architekturmodelle"),
        ),
        MaterialSpec(
            "Metal-Fill", "Metallpulver-gefülltes PLA (bis zu 80% Metallanteil, Bronze/Kupfer/Stahl)", "Faserverstärkte & Verbund-Filamente", "FDM",
            200, 225, 50, 60, false,
            listOf("Hohes Eigengewicht", "Polierbar für echten Metallglanz", "Kann oxidieren/Patina ansetzen", "Extrem abrasiv"),
            listOf("Statuen", "Schmuck", "Gewichte", "Repliken"),
        ),
        MaterialSpec(
            "316L / 17-4PH", "Sinterbares Edelstahl-Filament (z.B. BASF Ultrafuse)", "Faserverstärkte & Verbund-Filamente", "FDM",
            230, 250, 90, 120, true,
            listOf("Erfordert nach dem Druck Entbindern und Sintern im Ofen", "Ergibt vollwertige, massive Metallteile (~15-20% Schrumpfung beim Sintern)"),
            listOf("Industrielle Metallprototypen", "Werkzeuge", "Kleinserien-Metallteile"),
        ),

        // Hochleistungs-Polymere (High-Temp Industrial FDM)
        MaterialSpec(
            "PEEK", "Polyetheretherketon", "Hochleistungs-Polymere", "FDM",
            380, 420, 130, 160, true,
            listOf("Dauereinsatztemperatur bis 250°C", "Extreme mechanische Festigkeit und Chemikalienresistenz", "Biokompatibel und sterilisierbar", "Erfordert Hochtemperatur-Drucker (>90°C Bauraum)"),
            listOf("Luft- und Raumfahrt", "Medizintechnik (Implantate)", "Öl- & Gasindustrie"),
        ),
        MaterialSpec(
            "PEKK", "Polyetherketonketon", "Hochleistungs-Polymere", "FDM",
            360, 400, 120, 150, true,
            listOf("Leichter zu drucken als PEEK (langsamere Kristallisation)", "Bessere Layerhaftung als PEEK", "Extreme Chemikalien- und Hitzebeständigkeit"),
            listOf("Luftfahrt", "Halbleiterfertigung", "Hochtemperatur-Dichtungen"),
        ),
        MaterialSpec(
            "PEI", "Polyetherimid (ULTEM 9085 / 1010)", "Hochleistungs-Polymere", "FDM",
            350, 390, 120, 160, true,
            listOf("Flammhemmend (UL94 V-0, FST-zertifiziert)", "Hohes Festigkeits-Gewichts-Verhältnis", "Temperaturbeständig bis ~170-210°C"),
            listOf("Flugzeuginnenausstattung", "Schienenfahrzeuge", "Autoklavierbare Werkzeuge"),
        ),
        MaterialSpec(
            "PPSU", "Polyphenylsulfon", "Hochleistungs-Polymere", "FDM",
            360, 390, 140, 160, true,
            listOf("Beständig gegen über 1000 Dampfsterilisationszyklen", "Hohe Schlagzähigkeit und Hydrolysebeständigkeit"),
            listOf("Chirurgische Instrumente", "Medizintechnik", "Heißwasser-Armaturen"),
        ),
        MaterialSpec(
            "PVDF", "Polyvinylidenfluorid", "Hochleistungs-Polymere", "FDM",
            240, 265, 90, 110, true,
            listOf("Fluorpolymer mit extremer Beständigkeit gegen Säuren, Basen und Lösungsmittel", "UV- und strahlungsbeständig", "Flammhemmend"),
            listOf("Chemische Industrie", "Pumpenbauteile", "Reinstwasser-Anlagen"),
        ),

        // Lösliche & Abreißbare Stützmaterialien (Support FDM)
        MaterialSpec(
            "PVA", "Polyvinylalkohol", "Lösliche & Abreißbare Stützmaterialien", "FDM",
            190, 215, 50, 60, false,
            listOf("Wasserlöslich", "Ideal für PLA, PETG und Nylon", "Extrem hygroskopisch (muss luftdicht gelagert werden)"),
            listOf("Komplexe Überhänge", "Innenliegende Kanäle", "Bewegliche Baugruppen in einem Druck"),
        ),
        MaterialSpec(
            "BVOH", "Butendiol-Vinylalkohol-Copolymer", "Lösliche & Abreißbare Stützmaterialien", "FDM",
            195, 220, 50, 60, false,
            listOf("Löst sich deutlich schneller in Wasser als PVA", "Bessere Haftung an PETG und PA", "Sehr feuchtigkeitsempfindlich"),
            listOf("Feine Stützstrukturen bei Multi-Material-Druckern"),
        ),
        MaterialSpec(
            "Breakaway Support", "Spezielle Trennschicht-Filamente (z.B. Bambu Support for PLA/PA)", "Lösliche & Abreißbare Stützmaterialien", "FDM",
            200, 280, 50, 100, false,
            listOf("Mechanisch leicht ablösbar ohne Rückstände", "Keine Trocknungs- oder Auflösungszeit nötig", "Nur für Trennschicht (Interface) optimiert"),
            listOf("Flache Überhänge", "Saubere Unterseiten ohne Stützspuren"),
        ),

        // Photopolymere / Kunstharze (SLA / DLP / MSLA) – kein Düsen-/Betttemperaturbereich
        MaterialSpec(
            "Standard Resin", "Standard-Photopolymerharz (405nm)", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Extrem hohe Detailgenauigkeit", "Glatte Oberflächen", "Relativ spröde"),
            applications = listOf("Miniaturen", "Figuren", "Visuelle Prototypen"),
        ),
        MaterialSpec(
            "ABS-Like Resin", "Schlagzähes Photopolymerharz (Tough Resin)", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Deutlich höhere Bruchdehnung und Schlagzähigkeit als Standard-Resin", "Bohr- und gewindeschneidbar"),
            applications = listOf("Funktionsprototypen", "Schnappverbindungen", "Gehäuse"),
        ),
        MaterialSpec(
            "Water-Washable Resin", "Wasserwaschbares Harz", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Reinigung mit Wasser statt Isopropanol (IPA)", "Geringere Geruchsentwicklung"),
            applications = listOf("Hobbybereich", "Modellbau"),
        ),
        MaterialSpec(
            "Flexible Resin", "Elastisches Photopolymerharz (Shore 50A - 80A)", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Gummiartig, komprimierbar und biegsam", "Hohe Reißfestigkeit"),
            applications = listOf("Dichtungen", "Soft-Touch-Griffe", "Medizinische Modelle"),
        ),
        MaterialSpec(
            "High-Temp Resin", "Hochtemperatur-Photopolymerharz", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Wärmeformbeständigkeit bis über 230°C (HDT)", "Sehr hohe Steifigkeit"),
            applications = listOf("Spritzgussformen (Kleinserien)", "Heißluft- und Flüssigkeitsströmungstests"),
        ),
        MaterialSpec(
            "Castable Resin", "Ausbrennbares Gießharz (Wachsanteil)", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Verbrennt rückstandsfrei ohne Asche im Brennofen", "Extrem feine Oberflächen"),
            applications = listOf("Schmuckherstellung (Feinguss)", "Zahntechnik"),
        ),
        MaterialSpec(
            "Dental / BioMed Resin", "Biokompatibles & Medizinisches Harz", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Zertifiziert für Haut- oder Schleimhautkontakt (ISO 10993)", "Autoklavierbar"),
            applications = listOf("Bohrschablonen", "Zahnschienen (Aligner)", "Hörgeräte-Otoplastiken"),
        ),
        MaterialSpec(
            "Ceramic-Filled Resin", "Keramikgefülltes Photopolymerharz", "Photopolymere / Kunstharze", "SLA/DLP/MSLA",
            properties = listOf("Sehr hohe Steifigkeit", "Kann nach dem Druck im Brennofen zu reiner Silikat- oder Aluminiumoxidkeramik gesintert werden"),
            applications = listOf("Hitzeschilde", "Elektrische Isolatoren", "Präzisionskeramik"),
        ),

        // Pulverbett-Werkstoffe (SLS / MJF) – kein Düsen-/Betttemperaturbereich
        MaterialSpec(
            "PA12 Powder", "Polyamid 12 Sinterpulver", "Pulverbett-Werkstoffe", "SLS/MJF",
            properties = listOf("Isotrope mechanische Eigenschaften", "Keine Stützstrukturen nötig", "Leicht raue, matte Oberfläche"),
            applications = listOf("Serienfertigung", "Komplexe Baugruppen", "Industrie-Endbauteile"),
        ),
        MaterialSpec(
            "PA11 Powder", "Polyamid 11 Sinterpulver (biobasiert aus Rizinusöl)", "Pulverbett-Werkstoffe", "SLS/MJF",
            properties = listOf("Höhere Bruchdehnung und Schlagzähigkeit als PA12", "Splittert nicht unter Crash-Belastung"),
            applications = listOf("Prothetik", "Sportartikel", "Automobil-Crash-Komponenten"),
        ),
        MaterialSpec(
            "TPU Powder", "Thermoplastisches Polyurethan Sinterpulver", "Pulverbett-Werkstoffe", "SLS/MJF",
            properties = listOf("Hohe Elastizität und Reißfestigkeit", "Komplexe Gitterstrukturen (Lattice) ohne Stützen möglich"),
            applications = listOf("Orthopädische Einlagen", "Dämpfungselemente", "Schutzpolster"),
        ),
        MaterialSpec(
            "Alumide / PA-GF Powder", "Aluminium- oder glasgefülltes PA12 Pulver", "Pulverbett-Werkstoffe", "SLS",
            properties = listOf("Erhöhte Steifigkeit und Wärmeleitfähigkeit", "Metallischer Schimmer (Alumide)"),
            applications = listOf("Windkanalmodelle", "Thermoform-Werkzeuge", "Motorraumteile"),
        ),
    )

    fun findByKuerzel(kuerzel: String): MaterialSpec? =
        materials.firstOrNull { spec ->
            spec.kuerzel.split("/").map { it.trim() }.any { it.equals(kuerzel, ignoreCase = true) }
        }

    /** Alle Kürzel, zusammengesetzte Einträge (z.B. "PA6-CF / PA12-CF") für Typ-Dropdown/Erkennung in Einzelteile zerlegt. */
    val allKuerzel: List<String> by lazy {
        materials.flatMap { it.kuerzel.split("/").map(String::trim) }.distinct()
    }
}
