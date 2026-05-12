package com.helios.auraroll.detail.impl.di

import android.content.Context
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.helios.auraroll.common.EntryProviderInstaller
import com.helios.auraroll.common.Navigator
import com.helios.auraroll.database.IndexedPhotoDao
import com.helios.auraroll.detail.DefaultPhotoDetailRepository
import com.helios.auraroll.detail.PhotoDetailRepository
import com.helios.auraroll.detail.api.navigation.Detail
import com.helios.auraroll.detail.data.source.AndroidPaletteExtractorSource
import com.helios.auraroll.detail.data.source.AndroidPhotoExifSource
import com.helios.auraroll.detail.data.source.AndroidPhotoImageSource
import com.helios.auraroll.detail.data.source.AndroidPhotoLocationSource
import com.helios.auraroll.detail.data.source.AndroidPhotoMediaSource
import com.helios.auraroll.detail.data.source.PaletteExtractorSource
import com.helios.auraroll.detail.data.source.PhotoExifSource
import com.helios.auraroll.detail.data.source.PhotoImageSource
import com.helios.auraroll.detail.data.source.PhotoLocationSource
import com.helios.auraroll.detail.data.source.PhotoMediaSource
import com.helios.auraroll.detail.impl.ui.DetailRoute
import com.helios.auraroll.detail.impl.ui.DetailTransitionMetadata
import com.helios.auraroll.detail.impl.ui.DetailViewModel
import com.helios.auraroll.domain.usecase.LoadPhotoDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object DetailModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller(navigator: Navigator): EntryProviderInstaller =
        {
            entry<Detail>(
                metadata = { DetailTransitionMetadata },
                content = { destination ->
                    val viewModel = hiltViewModel<DetailViewModel>()
                    DetailRoute(
                        viewModel = viewModel,
                        photoId = destination.photoId,
                        navigateBack = { navigator.goBack() }
                    )
                }
            )
        }
}

/**
 * Wires the Detail feature's data layer.
 *
 * Each `@Provides` exposes a single-responsibility data source; the repository
 * itself is the KMP-pure [DefaultPhotoDetailRepository] that simply orchestrates
 * them. Adding iOS later is a matter of providing iOS implementations of the
 * source interfaces — no change to this module.
 */
@Module
@InstallIn(SingletonComponent::class)
object DetailUseCaseModule {

    @Provides
    @Singleton
    fun providePhotoMediaSource(
        @ApplicationContext context: Context,
    ): PhotoMediaSource = AndroidPhotoMediaSource(context)

    @Provides
    @Singleton
    fun providePhotoLocationSource(
        @ApplicationContext context: Context,
    ): PhotoLocationSource = AndroidPhotoLocationSource(context)

    @Provides
    @Singleton
    fun providePhotoImageSource(
        @ApplicationContext context: Context,
    ): PhotoImageSource = AndroidPhotoImageSource(context)

    @Provides
    @Singleton
    fun providePhotoExifSource(
        @ApplicationContext context: Context,
    ): PhotoExifSource = AndroidPhotoExifSource(context)

    @Provides
    @Singleton
    fun providePaletteExtractorSource(): PaletteExtractorSource =
        AndroidPaletteExtractorSource()

    @Provides
    @Singleton
    fun providePhotoDetailRepository(
        dao: IndexedPhotoDao,
        mediaSource: PhotoMediaSource,
        locationSource: PhotoLocationSource,
        imageSource: PhotoImageSource,
        exifSource: PhotoExifSource,
        paletteExtractor: PaletteExtractorSource,
    ): PhotoDetailRepository = DefaultPhotoDetailRepository(
        indexedPhotoDao = dao,
        mediaSource = mediaSource,
        locationSource = locationSource,
        imageSource = imageSource,
        exifSource = exifSource,
        paletteExtractor = paletteExtractor,
    )

    @Provides
    @Singleton
    fun provideLoadPhotoDetailUseCase(
        repository: PhotoDetailRepository,
    ): LoadPhotoDetailUseCase = LoadPhotoDetailUseCase(repository)
}
