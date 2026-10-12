package com.vedicinsights.agent.data.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;
import com.vedicinsights.agent.data.local.dao.VedicEventDao;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;

@Database(entities = {VedicEventEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE vedic_events ADD COLUMN matchMode TEXT DEFAULT 'ALL'");
            database.execSQL("ALTER TABLE vedic_events ADD COLUMN frequencyDays INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE vedic_events ADD COLUMN createdAtEpochMillis INTEGER NOT NULL DEFAULT 0");
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE vedic_events ADD COLUMN targetAdhikaMasa INTEGER");
        }
    };

    private static volatile AppDatabase INSTANCE;
    public abstract VedicEventDao vedicEventDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "vedic_agent_db"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}