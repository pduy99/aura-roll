package com.helios.auraroll.onboarding.impl.di

import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.data.repository.OfflineFirstIndexedPhotoRepository
import com.helios.auraroll.database.IndexedPhotoDao
import com.helios.auraroll.onboarding.DefaultIndexingTipRepository
import com.helios.auraroll.onboarding.IndexingTipRepository
import com.helios.auraroll.onboarding.impl.data.repository.IndexingWorkRepository
import com.helios.auraroll.onboarding.impl.data.repository.WorkManagerIndexingRepository
import com.helios.auraroll.quotes.DefaultHueQuoteRepository
import com.helios.auraroll.quotes.HueQuoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIndexingWorkRepository(
        impl: WorkManagerIndexingRepository
    ): IndexingWorkRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProviderModule {

    @Provides
    @Singleton
    fun provideIndexedPhotoRepository(dao: IndexedPhotoDao): IndexedPhotoRepository {
        return OfflineFirstIndexedPhotoRepository(dao)
    }

    @Provides
    @Singleton
    fun provideHueQuoteRepository(): HueQuoteRepository = DefaultHueQuoteRepository()

    @Provides
    @Singleton
    fun provideIndexingTipRepository(): IndexingTipRepository = DefaultIndexingTipRepository()
}