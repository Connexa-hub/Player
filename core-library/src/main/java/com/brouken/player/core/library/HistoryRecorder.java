package com.brouken.player.core.library;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Single entry point {@code PlayerActivity} (plain Java) needs to persist playback progress,
 * without that call site having to know anything about Room, threading, or merge semantics.
 * Every method here is fire-and-forget and safe to call from the main thread — the actual DB
 * work always happens on a dedicated single-thread executor.
 */
public final class HistoryRecorder {

    private static volatile HistoryRecorder instance;

    private final PlayerLibraryDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private HistoryRecorder(Context context) {
        this.database = PlayerLibraryDatabase.getInstance(context);
    }

    public static HistoryRecorder getInstance(Context context) {
        if (instance == null) {
            synchronized (HistoryRecorder.class) {
                if (instance == null) {
                    instance = new HistoryRecorder(context);
                }
            }
        }
        return instance;
    }

    /**
     * Records (or updates) a playback session. Safe to call often — e.g. once when playback
     * starts and once when it's released — since it's a full upsert, not an incremental delta.
     *
     * @param uri         media URI, used as the row's primary key
     * @param title       display title, or {@code null} to leave an existing title unchanged
     * @param durationMs  known duration in ms, or 0 if unknown at call time
     * @param positionMs  last playback position in ms
     * @param isNewSession pass {@code true} exactly once per distinct playback start (e.g. in
     *                     {@code initializePlayer()}) so {@code playCount} increments once per
     *                     watch, not once per position update
     */
    public void recordAsync(@NonNull String uri, @Nullable String title, long durationMs,
                             long positionMs, boolean isNewSession) {
        executor.execute(() -> {
            HistoryDao dao = database.historyDao();
            HistoryEntry existing = dao.findByUri(uri);
            long now = System.currentTimeMillis();

            if (existing == null) {
                HistoryEntry entry = new HistoryEntry(
                        uri,
                        title,
                        durationMs,
                        positionMs,
                        now,
                        1,
                        false);
                dao.upsert(entry);
            } else {
                existing.title = title != null ? title : existing.title;
                existing.durationMs = durationMs > 0 ? durationMs : existing.durationMs;
                existing.lastPositionMs = positionMs;
                existing.lastPlayedAtEpochMs = now;
                if (isNewSession) {
                    existing.playCount += 1;
                }
                dao.update(existing);
            }
        });
    }

    public void setFavoriteAsync(@NonNull String uri, boolean isFavorite) {
        executor.execute(() -> {
            HistoryDao dao = database.historyDao();
            HistoryEntry existing = dao.findByUri(uri);
            if (existing != null) {
                existing.isFavorite = isFavorite;
                dao.update(existing);
            }
        });
    }

    public void deleteAsync(@NonNull HistoryEntry entry) {
        executor.execute(() -> database.historyDao().delete(entry));
    }
}
