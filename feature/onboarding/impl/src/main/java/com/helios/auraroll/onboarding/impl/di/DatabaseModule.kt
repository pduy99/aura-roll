package com.helios.auraroll.onboarding.impl.di

import android.content.Context
import com.helios.auraroll.database.AuraRollDatabase
import com.helios.auraroll.database.IndexedPhotoDao
import com.helios.auraroll.database.getDatabaseBuilder
import com.helios.auraroll.indexing.AndroidPhotoIndexer
import com.helios.auraroll.indexing.PhotoIndexer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AuraRollDatabase {
        return getDatabaseBuilder(context).build()
    }

    @Provides
    fun provideIndexedPhotoDao(database: AuraRollDatabase): IndexedPhotoDao {
        return database.indexedPhotoDao()
    }

    @Provides
    @Singleton
    fun providePhotoIndexer(
        @ApplicationContext context: Context,
        dao: IndexedPhotoDao
    ): PhotoIndexer {
        return AndroidPhotoIndexer(context, dao)
    }
}