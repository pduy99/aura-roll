package com.helios.auraroll.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IndexedPhoto::class], version = 3)
abstract class AuraRollDatabase : RoomDatabase() {
    abstract fun indexedPhotoDao(): IndexedPhotoDao
}
