package com.helios.auraroll.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IndexedPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: IndexedPhoto)

    @Query("SELECT COUNT(*) FROM indexed_photos")
    fun countAll(): Flow<Int>

    @Query("SELECT id FROM indexed_photos")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT DISTINCT dominantColorArgb FROM indexed_photos WHERE isMonochrome = 0 ORDER BY id DESC LIMIT 5")
    fun sampleColors(): Flow<List<Long>>
}