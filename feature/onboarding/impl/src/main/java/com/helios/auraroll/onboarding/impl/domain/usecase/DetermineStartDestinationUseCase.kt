package com.helios.auraroll.onboarding.impl.domain.usecase

import com.helios.auraroll.common.preferences.AppPreferences
import com.helios.auraroll.common.utils.PermissionChecker
import com.helios.auraroll.home.api.navigation.Home
import com.helios.auraroll.onboarding.api.navigation.Onboarding
import com.helios.auraroll.onboarding.impl.data.repository.IndexingWorkRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetermineStartDestinationUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
    private val permissionChecker: PermissionChecker,
    private val indexingWorkRepository: IndexingWorkRepository
) {
    suspend operator fun invoke(): Any {
        val onboardingCompleted = appPreferences.isOnboardingCompleted()
        val hasPermission = permissionChecker.isStoragePermissionGranted()

        // If they finished onboarding, but revoked storage permissions in settings later,
        // bounce them back to Welcome so they can grant it again.
        if (onboardingCompleted && !hasPermission) {
            return Onboarding(permissionRevoked = true)
        }

        // If they finished onboarding and still have permission, go directly to Home.
        if (onboardingCompleted && hasPermission) {
            return Home
        }

        // Onboarding is NOT completed yet — start from the beginning.
        // OnboardingViewModel's init block handles mid-indexing state restore via WorkManager.
        return Onboarding(permissionRevoked = false)
    }
}
