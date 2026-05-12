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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<IndexedPhoto>)

    @Query("SELECT COUNT(*) FROM indexed_photos")
    fun countAll(): Flow<Int>

    @Query("SELECT id FROM indexed_photos")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT * FROM indexed_photos WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): IndexedPhoto?

    @Query("SELECT DISTINCT dominantColorArgb FROM indexed_photos WHERE isMonochrome = 0 ORDER BY id DESC LIMIT 5")
    fun sampleColors(): Flow<List<Long>>

    @Query("SELECT * FROM indexed_photos WHERE hue >= :minHue AND hue <= :maxHue AND isMonochrome = 0 AND colorDominance >= :minDominance ORDER BY colorDominance DESC, id DESC")
    fun observePhotosByHueRange(minHue: Float, maxHue: Float, minDominance: Float): Flow<List<IndexedPhoto>>

    @Query("SELECT COUNT(*) FROM indexed_photos WHERE hue >= :minHue AND hue <= :maxHue AND isMonochrome = 0 AND colorDominance >= :minDominance")
    fun countPhotosByHueRange(minHue: Float, maxHue: Float, minDominance: Float): Flow<Int>

    @Query("SELECT * FROM indexed_photos WHERE isMonochrome = 1 ORDER BY id DESC")
    fun observeMonochromePhotos(): Flow<List<IndexedPhoto>>

    @Query("SELECT COUNT(*) FROM indexed_photos WHERE isMonochrome = 1")
    fun countMonochromePhotos(): Flow<Int>
}
