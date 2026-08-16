package com.sonymobile.calendar.provider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/* JADX INFO: loaded from: classes2.dex */
public class DatabaseFactory {
    private static DatabaseHelper sDatabaseHelper;

    private DatabaseFactory() {
    }

    private static void initializeDatabaseHelper(Context context) {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context);
        }
    }

    public static synchronized SQLiteDatabase getWritableDatabase(Context context) {
        if (sDatabaseHelper == null) {
            initializeDatabaseHelper(context);
        }
        return sDatabaseHelper.getWritableDatabase();
    }

    public static synchronized SQLiteDatabase getReadableDatabase(Context context) {
        if (sDatabaseHelper == null) {
            initializeDatabaseHelper(context);
        }
        return sDatabaseHelper.getReadableDatabase();
    }
}
