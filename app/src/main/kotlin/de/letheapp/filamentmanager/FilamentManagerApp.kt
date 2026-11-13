package de.letheapp.filamentmanager

import android.app.Application
import de.letheapp.filamentmanager.data.db.AppDatabase

class FilamentManagerApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}
