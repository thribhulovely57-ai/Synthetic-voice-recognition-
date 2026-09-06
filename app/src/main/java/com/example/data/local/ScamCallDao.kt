package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScamCallDao {
    @Query("SELECT * FROM scam_calls ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScamCallEntity>>

    @Query("SELECT * FROM scam_calls WHERE id = :id")
    suspend fun getById(id: Long): ScamCallEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: ScamCallEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls: List<ScamCallEntity>)

    @Update
    suspend fun updateCall(call: ScamCallEntity)

    @Delete
    suspend fun deleteCall(call: ScamCallEntity)

    @Query("DELETE FROM scam_calls WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scam_calls")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM scam_calls")
    fun getCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM scam_calls WHERE riskScore >= 35")
    fun getSuspiciousCount(): Flow<Int>
}
