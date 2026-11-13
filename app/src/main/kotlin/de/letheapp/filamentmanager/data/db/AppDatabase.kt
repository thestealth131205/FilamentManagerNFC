package de.letheapp.filamentmanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FilamentEntity::class,
        NfcWriteLogEntity::class,
        ManufacturerEntity::class,
        FilamentTypeEntity::class,
        FilamentColorEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filamentDao(): FilamentDao
    abstract fun manufacturerDao(): ManufacturerDao
    abstract fun filamentTypeDao(): FilamentTypeDao
    abstract fun filamentColorDao(): FilamentColorDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "filament_manager.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed-Daten (PLAN.md: "damit die Liste nicht leer startet")
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = instance ?: return@launch
                            SeedData.manufacturers.forEach {
                                database.manufacturerDao().insert(ManufacturerEntity(name = it))
                            }
                            SeedData.types.forEach {
                                database.filamentTypeDao().insert(FilamentTypeEntity(name = it))
                            }
                        }
                    }
                })
                .build()
    }
}
