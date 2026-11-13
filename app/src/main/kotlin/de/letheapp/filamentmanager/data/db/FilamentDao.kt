package de.letheapp.filamentmanager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilamentDao {
    @Query("SELECT * FROM filaments ORDER BY updatedAtEpochMs DESC")
    fun observeAll(): Flow<List<FilamentEntity>>

    @Query("SELECT * FROM filaments WHERE id = :id")
    suspend fun getById(id: Long): FilamentEntity?

    @Query("SELECT * FROM filaments WHERE sku = :sku LIMIT 1")
    suspend fun findBySku(sku: String): FilamentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(filament: FilamentEntity): Long

    @Update
    suspend fun update(filament: FilamentEntity)

    @Delete
    suspend fun delete(filament: FilamentEntity)

    @Insert
    suspend fun logNfcWrite(entry: NfcWriteLogEntity)

    @Query("SELECT * FROM nfc_write_log WHERE filamentId = :filamentId ORDER BY writtenAtEpochMs DESC")
    fun observeWriteLog(filamentId: Long): Flow<List<NfcWriteLogEntity>>
}
