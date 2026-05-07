package com.helios.auraroll.onboarding.impl.di

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.helios.auraroll.common.EntryProviderInstaller
import com.helios.auraroll.common.Navigator
import com.helios.auraroll.home.api.navigation.Home
import com.helios.auraroll.onboarding.api.navigation.Onboarding
import com.helios.auraroll.onboarding.impl.ui.OnboardingRoute
import com.helios.auraroll.onboarding.impl.ui.OnboardingViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object OnboardingModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller(navigator: Navigator): EntryProviderInstaller =
        {
            entry<Onboarding>(content = {
                val viewModel = hiltViewModel<OnboardingViewModel>()
                val permissionRevoked = it.permissionRevoked
                OnboardingRoute(
                    viewModel = viewModel,
                    permissionRevoked = permissionRevoked,
                    navigateToHome = { navigator.popAndGoTo(Home) }
                )
            })
        }
}
