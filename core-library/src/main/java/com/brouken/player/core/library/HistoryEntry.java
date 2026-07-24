package com.brouken.player.core.library;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * One row per distinct media URI ever played. Backs History / Continue Watching / Favorites —
 * the same table serves all three, filtered/sorted differently by the DAO queries below, rather
 * than three separate tables that would need to stay in sync with each other.
 */
@Entity(tableName = "history_entry")
public class HistoryEntry {

    /** The media URI (as a string) is the natural key — the same file is the same history row. */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uri")
    public String uri;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "duration_ms")
    public long durationMs;

    @ColumnInfo(name = "last_position_ms")
    public long lastPositionMs;

    @ColumnInfo(name = "last_played_at_epoch_ms")
    public long lastPlayedAtEpochMs;

    @ColumnInfo(name = "play_count")
    public int playCount;

    @ColumnInfo(name = "is_favorite")
    public boolean isFavorite;

    public HistoryEntry(@NonNull String uri, String title, long durationMs, long lastPositionMs,
                         long lastPlayedAtEpochMs, int playCount, boolean isFavorite) {
        this.uri = uri;
        this.title = title;
        this.durationMs = durationMs;
        this.lastPositionMs = lastPositionMs;
        this.lastPlayedAtEpochMs = lastPlayedAtEpochMs;
        this.playCount = playCount;
        this.isFavorite = isFavorite;
    }
}
