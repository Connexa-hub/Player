package com.brouken.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.brouken.player.core.design.PlayerTheme
import com.brouken.player.core.design.PlayerThemeMode
import com.brouken.player.core.design.PlayerTypography
import com.brouken.player.core.library.HistoryEntry
import com.brouken.player.core.library.HistoryRecorder
import com.brouken.player.core.library.PlayerLibraryDatabase
import com.brouken.player.core.library.ResumePolicy
import com.brouken.player.feature.libraryui.LibraryItemUi
import com.brouken.player.feature.libraryui.LibraryScreen
import com.brouken.player.feature.libraryui.LibraryTab

/**
 * Experimental (see `Prefs.useComposeControls`): the library/history screen. Reachable only from
 * the new Compose control surface for now — the legacy XML UI has no entry point to this, so it
 * cannot regress the default app for anyone who hasn't opted into the experimental controls.
 */
class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val historyDao = PlayerLibraryDatabase.getInstance(this).historyDao()
        val recorder = HistoryRecorder.getInstance(this)

        setContent {
            PlayerTheme(mode = PlayerThemeMode.Dark) {
                var selectedTab by remember { mutableStateOf(LibraryTab.ContinueWatching) }
                val allEntries by historyDao.observeAllByRecency().observeAsState(initial = emptyList())

                val items = remember(allEntries, selectedTab) {
                    mapEntriesForTab(allEntries, selectedTab)
                }

                Column {
                    IconButton(onClick = { finish() }, modifier = Modifier.padding(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    LibraryScreen(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        items = items,
                        onItemClick = { item -> openInPlayer(item.uri) },
                        onToggleFavorite = { item -> recorder.setFavoriteAsync(item.uri, !item.isFavorite) },
                    )
                }
            }
        }
    }

    private fun openInPlayer(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setClass(this@LibraryActivity, PlayerActivity::class.java)
            data = Uri.parse(uri)
        }
        startActivity(intent)
    }

    companion object {
        private fun mapEntriesForTab(entries: List<HistoryEntry>, tab: LibraryTab): List<LibraryItemUi> {
            val filtered = when (tab) {
                LibraryTab.ContinueWatching -> entries.filter {
                    ResumePolicy.isResumable(it.lastPositionMs, it.durationMs)
                }
                LibraryTab.History -> entries
                LibraryTab.Favorites -> entries.filter { it.isFavorite }
            }
            return filtered.map { entry ->
                val watchedFraction = if (tab == LibraryTab.Favorites) {
                    null
                } else {
                    ResumePolicy.watchedFraction(entry.lastPositionMs, entry.durationMs)
                }
                LibraryItemUi(
                    uri = entry.uri,
                    title = entry.title ?: entry.uri,
                    subtitle = subtitleFor(entry),
                    watchedFraction = watchedFraction,
                    isFavorite = entry.isFavorite,
                )
            }
        }

        private fun subtitleFor(entry: HistoryEntry): String {
            if (entry.durationMs <= 0) {
                return Utils.formatMilis(entry.lastPositionMs) + " played"
            }
            val remainingMs = (entry.durationMs - entry.lastPositionMs).coerceAtLeast(0L)
            return if (remainingMs <= ResumePolicy.END_THRESHOLD_MS) {
                "Watched"
            } else {
                Utils.formatMilis(remainingMs) + " left"
            }
        }
    }
}
