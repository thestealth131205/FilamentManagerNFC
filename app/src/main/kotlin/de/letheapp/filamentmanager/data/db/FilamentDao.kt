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

    // Viele Spulen haben keine SKU (nur Hersteller/Typ/Farbe sind Pflicht) – ein frisch
    // beschriebener Tag lässt sich dann beim Lesen nicht per SKU zuordnen. Der zuletzt für
    // diese Tag-UID geloggte Schreibvorgang ist der verlässlichere Schlüssel.
    @Query(
        "SELECT filamentId FROM nfc_write_log WHERE tagUid = :tagUid " +
            "ORDER BY writtenAtEpochMs DESC LIMIT 1",
    )
    suspend fun findFilamentIdByTagUid(tagUid: String): Long?

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
