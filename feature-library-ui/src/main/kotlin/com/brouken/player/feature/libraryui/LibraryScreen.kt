package com.brouken.player.feature.libraryui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.GlassSurface
import com.brouken.player.core.design.PlayerColors
import com.brouken.player.core.design.PlayerShapes
import com.brouken.player.core.design.PlayerTypography

/**
 * Library/history screen: three tabs (Continue Watching / History / Favorites) over the same
 * underlying data, each a simple list of glass cards. No navigation library dependency — tab
 * state and item lists are supplied by the caller, so this composable is fully previewable and
 * this module stays free of any nav-graph or Activity assumptions.
 */
@Composable
fun LibraryScreen(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    items: List<LibraryItemUi>,
    onItemClick: (LibraryItemUi) -> Unit,
    onToggleFavorite: (LibraryItemUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = PlayerColors.AccentViolet,
        ) {
            LibraryTab.values().forEach { tab ->
                Tab(
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(tab.displayName(), style = PlayerTypography.label) },
                )
            }
        }

        if (items.isEmpty()) {
            EmptyState(selectedTab, modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items, key = { it.uri }) { item ->
                    LibraryRow(
                        item = item,
                        onClick = { onItemClick(item) },
                        onToggleFavorite = { onToggleFavorite(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryRow(
    item: LibraryItemUi,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = PlayerShapes.medium,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    style = PlayerTypography.title,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (item.isFavorite) PlayerColors.AccentViolet else PlayerColors.Neutral80,
                    )
                }
            }
            Text(
                text = item.subtitle,
                style = PlayerTypography.body,
                color = PlayerColors.Neutral80,
            )
            item.watchedFraction?.let { fraction ->
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(4.dp),
                    color = PlayerColors.AccentViolet,
                    trackColor = PlayerColors.GlassFillDark,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(tab: LibraryTab, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = when (tab) {
                LibraryTab.ContinueWatching -> "Nothing in progress yet"
                LibraryTab.History -> "No playback history yet"
                LibraryTab.Favorites -> "No favorites yet"
            },
            style = PlayerTypography.body,
            color = PlayerColors.Neutral80,
        )
    }
}

private fun LibraryTab.displayName(): String = when (this) {
    LibraryTab.ContinueWatching -> "Continue Watching"
    LibraryTab.History -> "History"
    LibraryTab.Favorites -> "Favorites"
}
