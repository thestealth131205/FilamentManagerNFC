# Filament Manager NFC

Android-App (Kotlin/Compose) zur Verwaltung von 3D-Druck-Filamentspulen für den
**Anycubic ACE 2 Pro** (bzw. kompatible NFC-Tags auf MIFARE-Ultralight-C-Basis,
NTAG213/215/216).

Die App führt eine lokale, exportierbare Filament-Datenbank (Hersteller, Typ,
Farbe, Temperaturen, Slicer-Einstellungen, Preis, Notizen), liest Etiketten per
Kamera ein (Barcode/OCR) und liest/schreibt den Eintrag auf einen NFC-Tag im
ACE-2-Pro-kompatiblen Format.

Grundlage für das NFC-Tag-Format ist das von Dritten ([DnG-Crafts/ACE-RFID](https://github.com/DnG-Crafts/ACE-RFID))
offen reverse-engineerte Byte-Layout eines Zubehörteils (Filament-Etiketten) –
siehe `PLAN.md` im übergeordneten Projektordner für die vollständige Dokumentation.

## Status

**Phase 1 — Projektgerüst.** Datenbank (Room), Spulenliste/-editor mit
lernenden Hersteller/Typ/Farb-Dropdowns sind umgesetzt. NFC-Lesen/Schreiben und
Kamera-Scan (Barcode/OCR) sind als Platzhalter-Screens angelegt und folgen in
den nächsten Schritten.

## Build

Build läuft **ausschließlich über GitHub Actions** (`.github/workflows/build-release.yml`),
analog zu [PixelDisplay](https://github.com/thestealth131205/PixelDisplay) —
auf ARM64-Linux gibt es kein funktionierendes `aapt2`. Trigger: Tag-Push `v*`
oder `workflow_dispatch`. Signierte Release-APKs brauchen die Repo-Secrets
`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`; ohne sie
wird unsigniert gebaut (kein Release-Upload).

## Lizenz

Siehe Hinweis in `PLAN.md` des übergeordneten Projekts: eigene, interoperable
App gegen ein dokumentiertes Byte-Format, keine Übernahme fremder App-Logik
oder Assets.
