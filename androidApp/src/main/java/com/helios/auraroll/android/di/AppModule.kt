package com.helios.auraroll.android.di

import com.helios.auraroll.common.Navigator
import com.helios.auraroll.common.utils.PermissionChecker
import com.helios.auraroll.onboarding.impl.domain.usecase.DetermineStartDestinationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(ActivityRetainedComponent::class)
object AppModule {

    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(
        determineStartDestinationUseCase: DetermineStartDestinationUseCase
    ): Navigator {
        val startDestination = runBlocking {
            determineStartDestinationUseCase()
        }
        
        return Navigator(startDestination = startDestination)
    }
}