package de.letheapp.filamentmanager.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ManufacturerDao {
    @Query("SELECT * FROM manufacturers ORDER BY name ASC")
    fun observeAllAlpha(): Flow<List<ManufacturerEntity>>

    @Query("SELECT * FROM manufacturers ORDER BY usageCount DESC, lastUsedAtEpochMs DESC LIMIT 3")
    fun observeTop3(): Flow<List<ManufacturerEntity>>

    @Query("SELECT * FROM manufacturers WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): ManufacturerEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ManufacturerEntity): Long

    @Query("UPDATE manufacturers SET usageCount = usageCount + 1, lastUsedAtEpochMs = :now WHERE id = :id")
    suspend fun markUsed(id: Long, now: Long = System.currentTimeMillis())
}

@Dao
interface FilamentTypeDao {
    @Query("SELECT * FROM filament_types ORDER BY name ASC")
    fun observeAllAlpha(): Flow<List<FilamentTypeEntity>>

    @Query("SELECT * FROM filament_types ORDER BY usageCount DESC, lastUsedAtEpochMs DESC LIMIT 3")
    fun observeTop3(): Flow<List<FilamentTypeEntity>>

    @Query("SELECT * FROM filament_types WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): FilamentTypeEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FilamentTypeEntity): Long

    @Query("UPDATE filament_types SET usageCount = usageCount + 1, lastUsedAtEpochMs = :now WHERE id = :id")
    suspend fun markUsed(id: Long, now: Long = System.currentTimeMillis())
}

@Dao
interface FilamentColorDao {
    @Query("SELECT * FROM filament_colors ORDER BY name ASC")
    fun observeAllAlpha(): Flow<List<FilamentColorEntity>>

    @Query("SELECT * FROM filament_colors ORDER BY usageCount DESC, lastUsedAtEpochMs DESC LIMIT 3")
    fun observeTop3(): Flow<List<FilamentColorEntity>>

    @Query("SELECT * FROM filament_colors WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): FilamentColorEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FilamentColorEntity): Long

    @Query("UPDATE filament_colors SET usageCount = usageCount + 1, lastUsedAtEpochMs = :now WHERE id = :id")
    suspend fun markUsed(id: Long, now: Long = System.currentTimeMillis())
}
