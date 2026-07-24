package com.brouken.player.core.library;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HistoryEntry.class}, version = 1, exportSchema = true)
public abstract class PlayerLibraryDatabase extends RoomDatabase {

    private static volatile PlayerLibraryDatabase instance;

    public abstract HistoryDao historyDao();

    public static PlayerLibraryDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (PlayerLibraryDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    PlayerLibraryDatabase.class,
                                    "player_library.db")
                            .build();
                }
            }
        }
        return instance;
    }
}
