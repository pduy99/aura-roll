package com.helios.auraroll.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AuraRollDatabase> =
    Room.databaseBuilder<AuraRollDatabase>(
        context = context.applicationContext,
        name = "auraroll.db"
    )