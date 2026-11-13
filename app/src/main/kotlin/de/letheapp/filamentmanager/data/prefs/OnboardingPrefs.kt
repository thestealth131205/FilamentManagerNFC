package de.letheapp.filamentmanager.data.prefs

import android.content.Context

/**
 * Merkt sich, ob die Berechtigungs-Erklärung beim App-Start (Kamera/NFC) bereits
 * gezeigt wurde, damit sie nur einmalig erscheint.
 */
object OnboardingPrefs {
    private const val PREFS_NAME = "onboarding_prefs"
    private const val KEY_PERMISSIONS_EXPLAINED = "permissions_explained"

    fun hasSeenPermissionOnboarding(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PERMISSIONS_EXPLAINED, false)

    fun setSeenPermissionOnboarding(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PERMISSIONS_EXPLAINED, true)
            .apply()
    }
}
