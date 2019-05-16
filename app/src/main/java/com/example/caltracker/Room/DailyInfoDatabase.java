package com.example.caltracker.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DaliyInfo.class}, version = 3, exportSchema = false)

public abstract class DailyInfoDatabase extends RoomDatabase {
    public abstract DaliyInfoDAO InfoDao();
    private static volatile DailyInfoDatabase INSTANCE;
    static DailyInfoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DailyInfoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    DailyInfoDatabase.class, "dailyInfo_database")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

}
