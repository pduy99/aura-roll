package com.helios.auraroll.home.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class HomeTab { GALLERY, AURA, MEMORIES, PROFILE }

@Composable
fun HomeBottomBar(
    activeTab: HomeTab = HomeTab.AURA,
    onTabSelected: (HomeTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarIcon(
            isActive = activeTab == HomeTab.GALLERY,
            onClick = { onTabSelected(HomeTab.GALLERY) }
        ) {
            Icon(
                imageVector = Icons.Outlined.GridView,
                contentDescription = "Gallery",
                modifier = Modifier.size(24.dp)
            )
        }

        BottomBarIcon(
            isActive = activeTab == HomeTab.AURA,
            onClick = { onTabSelected(HomeTab.AURA) }
        ) {
            Icon(
                imageVector = Icons.Filled.Palette,
                contentDescription = "Aura",
                modifier = Modifier.size(24.dp)
            )
        }

        BottomBarIcon(
            isActive = activeTab == HomeTab.MEMORIES,
            onClick = { onTabSelected(HomeTab.MEMORIES) }
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = "Memories",
                modifier = Modifier.size(24.dp)
            )
        }

        BottomBarIcon(
            isActive = activeTab == HomeTab.PROFILE,
            onClick = { onTabSelected(HomeTab.PROFILE) }
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BottomBarIcon(
    isActive: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val tint = if (isActive) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    IconButton(onClick = onClick) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides tint
        ) {
            icon()
        }
    }
}
