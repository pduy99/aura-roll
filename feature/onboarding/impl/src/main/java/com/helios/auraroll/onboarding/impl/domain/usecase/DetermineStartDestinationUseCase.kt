package com.helios.auraroll.onboarding.impl.domain.usecase

import com.helios.auraroll.common.preferences.AppPreferences
import com.helios.auraroll.common.utils.PermissionChecker
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
        // we bounce them back to Welcome so they can grant it again.
        if (onboardingCompleted && !hasPermission) {
            return Onboarding(permissionRevoked = true)
        }
        
        // If they finished onboarding and still have permission, go to Home
        if (onboardingCompleted && hasPermission) {
            // TODO: Replace with the actual Home navigation object when it's built
            return Onboarding(permissionRevoked = false)
        }
        
        // At this point, onboarding is NOT completed. 
        // We return Onboarding(false). The OnboardingViewModel's init block 
        // handles the "mid-indexing" state restore by querying WorkManager.
        return Onboarding(permissionRevoked = false)
    }
}
