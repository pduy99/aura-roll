package com.helios.auraroll.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionChecker @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun areAllPermissionsGranted(): Boolean {
        return isStoragePermissionGranted() &&
                isOverlayPermissionGranted() &&
                isNotificationPermissionGranted()
    }

    fun isStoragePermissionGranted(): Boolean {
        val hasStandardAccess = getPhotoAccessLevel(context) == PhotoAccessLevel.FULL_ACCESS

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasStandardAccess && isFolderAccessGranted()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isFolderAccessGranted(): Boolean {
        return context.contentResolver.persistedUriPermissions.isNotEmpty()
    }

    fun isOverlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        fun getPhotoAccessLevel(context: Context): PhotoAccessLevel {
            // Android 14 (API 34) and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val hasFullAccess = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED

                val hasLimitedAccess = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED

                return when {
                    hasFullAccess -> PhotoAccessLevel.FULL_ACCESS
                    hasLimitedAccess -> PhotoAccessLevel.LIMITED_ACCESS // Full is denied, but partial is granted
                    else -> PhotoAccessLevel.DENIED
                }
            }

            // Android 13 (API 33)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasAccess = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED

                return if (hasAccess) PhotoAccessLevel.FULL_ACCESS else PhotoAccessLevel.DENIED
            }

            // Android 12 and below (Legacy Storage Permission)
            val hasStoragePermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            return if (hasStoragePermission) PhotoAccessLevel.FULL_ACCESS else PhotoAccessLevel.DENIED
        }
    }
}

enum class PhotoAccessLevel {
    FULL_ACCESS,
    LIMITED_ACCESS,
    DENIED
}