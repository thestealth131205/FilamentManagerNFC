package de.letheapp.filamentmanager.data.prefs

import android.content.Context

/** Merkt sich, ob der erweiterte "Tools"-Modus im NFC-Schreiben-Tab aktiviert ist (Zahnrad-Menü). */
object ToolModePrefs {
    private const val PREFS_NAME = "tool_mode_prefs"
    private const val KEY_TOOLS_ENABLED = "tools_enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_TOOLS_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_TOOLS_ENABLED, enabled)
            .apply()
    }
}
