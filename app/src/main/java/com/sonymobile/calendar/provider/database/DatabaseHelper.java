package com.sonymobile.calendar.provider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* JADX INFO: loaded from: classes2.dex */
class DatabaseHelper extends SQLiteOpenHelper {
    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    DatabaseHelper(Context context) {
        super(context, "SomcCalendarProvider.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE event_comments (_id INTEGER PRIMARY KEY, event_id INTEGER NOT NULL, event_comment TEXT, event_dirty INTEGER )");
        sQLiteDatabase.execSQL("CREATE INDEX index_event_id ON event_comments(event_id)");
    }
}
