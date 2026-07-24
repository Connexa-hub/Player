package com.brouken.player.core.library;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(HistoryEntry entry);

    @Update
    void update(HistoryEntry entry);

    @Delete
    void delete(HistoryEntry entry);

    @Query("SELECT * FROM history_entry WHERE uri = :uri LIMIT 1")
    HistoryEntry findByUri(String uri);

    /** Most-recently-played first — the "History" list. */
    @Query("SELECT * FROM history_entry ORDER BY last_played_at_epoch_ms DESC")
    LiveData<List<HistoryEntry>> observeAllByRecency();

    /**
     * "Continue Watching": played far enough in to matter, not already finished.
     * Position/duration thresholds are applied in {@link ResumePolicy}, not here — this query
     * intentionally just orders by recency and lets the caller filter, so the resume-eligibility
     * rule lives in one testable place instead of being duplicated in SQL.
     */
    @Query("SELECT * FROM history_entry WHERE last_position_ms > 0 ORDER BY last_played_at_epoch_ms DESC")
    LiveData<List<HistoryEntry>> observeResumeCandidates();

    @Query("SELECT * FROM history_entry WHERE is_favorite = 1 ORDER BY last_played_at_epoch_ms DESC")
    LiveData<List<HistoryEntry>> observeFavorites();

    @Query("DELETE FROM history_entry")
    void clearAll();
}
