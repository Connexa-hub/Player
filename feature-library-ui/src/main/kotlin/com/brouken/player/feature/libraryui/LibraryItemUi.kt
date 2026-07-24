package com.brouken.player.feature.libraryui

/**
 * What a history/continue-watching/favorites row needs to render. The app module maps
 * `core-library`'s `HistoryEntry` (a Room entity) into this, so this module has no Room/Android
 * persistence dependency at all — same decoupling pattern as `feature-player-ui`'s
 * `PlayerControlsState`.
 */
data class LibraryItemUi(
    val uri: String,
    val title: String,
    /** Pre-formatted, e.g. "1:24:10 left" or "Watched" — locale formatting stays in the app module. */
    val subtitle: String,
    /** 0f..1f watched progress, or null to hide the progress bar entirely (e.g. Favorites tab). */
    val watchedFraction: Float?,
    val isFavorite: Boolean,
)

enum class LibraryTab {
    ContinueWatching,
    History,
    Favorites,
}
