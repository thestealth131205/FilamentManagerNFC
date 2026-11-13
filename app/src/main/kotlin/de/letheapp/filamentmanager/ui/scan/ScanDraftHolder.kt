package de.letheapp.filamentmanager.ui.scan

import de.letheapp.filamentmanager.ui.editor.FilamentFormState

/**
 * Übergibt den per Kamera-OCR vorausgefüllten Formular-Entwurf an den Editor-Screen
 * (ScanScreen -> AppNavigation -> Editor-Route für eine neue Spule). Einfacher
 * In-Memory-Zwischenspeicher statt Nav-Arguments, da FilamentFormState zu viele
 * Felder hat, um es sinnvoll über die Navigation zu serialisieren.
 */
object ScanDraftHolder {
    private var pending: FilamentFormState? = null

    fun set(form: FilamentFormState) {
        pending = form
    }

    /** Liefert den Entwurf einmalig und löscht ihn danach, damit er nicht bei einer späteren "Neue Spule" wieder auftaucht. */
    fun consume(): FilamentFormState? {
        val value = pending
        pending = null
        return value
    }
}
