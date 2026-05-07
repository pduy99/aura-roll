package com.helios.auraroll.home.impl.di

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.helios.auraroll.common.EntryProviderInstaller
import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.domain.usecase.ObserveFilteredPhotosUseCase
import com.helios.auraroll.domain.usecase.ObservePhotoCountUseCase
import com.helios.auraroll.home.api.navigation.Home
import com.helios.auraroll.home.impl.ui.HomeRoute
import com.helios.auraroll.home.impl.ui.HomeViewModel
import com.helios.auraroll.quotes.HueQuoteRepository
import com.helios.auraroll.quotes.SelectHueQuoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller(): EntryProviderInstaller =
        {
            entry<Home>(content = {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeRoute(viewModel = viewModel)
            })
        }
}

/**
 * Bridges the KMP-pure use cases under `:shared` into Hilt's graph.
 * Use cases themselves stay free of `@Inject` so they remain Kotlin-multiplatform safe.
 */
@Module
@InstallIn(SingletonComponent::class)
object HomeUseCaseModule {

    @Provides
    @Singleton
    fun provideObserveFilteredPhotosUseCase(
        repository: IndexedPhotoRepository
    ): ObserveFilteredPhotosUseCase = ObserveFilteredPhotosUseCase(repository)

    @Provides
    @Singleton
    fun provideObservePhotoCountUseCase(
        repository: IndexedPhotoRepository
    ): ObservePhotoCountUseCase = ObservePhotoCountUseCase(repository)

    @Provides
    @Singleton
    fun provideSelectHueQuoteUseCase(
        repository: HueQuoteRepository
    ): SelectHueQuoteUseCase = SelectHueQuoteUseCase(repository)
}
