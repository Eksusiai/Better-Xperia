package com.sonymobile.calendar.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import com.sonymobile.calendar.provider.database.DatabaseFactory;
import com.sonymobile.calendar.provider.observer.ObserverThread;

/* JADX INFO: loaded from: classes2.dex */
public class SomcCalendarProvider extends ContentProvider {
    private static final int EVENT_COMMENTS = 1;
    private static final int EVENT_COMMENTS_ID = 2;
    private static final UriMatcher MATCHER;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        MATCHER = uriMatcher;
        uriMatcher.addURI("com.sonymobile.calendar.provider", "event_comments", 1);
        uriMatcher.addURI("com.sonymobile.calendar.provider", SomcCalendarContract.EventComments.ID_PATH, 2);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (getContext().checkSelfPermission("android.permission.READ_CALENDAR") != 0 || getContext().checkSelfPermission("android.permission.WRITE_CALENDAR") != 0) {
            return false;
        }
        new ObserverThread(getContext(), CalendarContract.Events.CONTENT_URI).start();
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int iMatch = MATCHER.match(uri);
        if (iMatch == 1 || iMatch == 2) {
            return "vnd.android.cursor.item/event_comment";
        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase writableDatabase = DatabaseFactory.getWritableDatabase(getContext());
        writableDatabase.beginTransaction();
        try {
            int iMatch = MATCHER.match(uri);
            if (iMatch != 1 && iMatch != 2) {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            long jInsert = writableDatabase.insert("event_comments", "", contentValues);
            if (jInsert <= 0) {
                throw new SQLException("Failed to add a record into " + uri);
            }
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            Uri uriWithAppendedId = ContentUris.withAppendedId(uri, jInsert);
            notifyContentChange(uriWithAppendedId);
            return uriWithAppendedId;
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteDatabase readableDatabase = DatabaseFactory.getReadableDatabase(getContext());
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("event_comments");
        sQLiteQueryBuilder.setProjectionMap(SomcCalendarContract.EventComments.PROJECTION);
        int iMatch = MATCHER.match(uri);
        if (iMatch != 1) {
            if (iMatch == 2) {
                sQLiteQueryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
            } else {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        } else if (TextUtils.isEmpty(str2)) {
            str2 = "_ID ASC";
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase, strArr, str, strArr2, null, null, str2);
        if (cursorQuery != null) {
            cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursorQuery;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int iUpdate;
        SQLiteDatabase writableDatabase = DatabaseFactory.getWritableDatabase(getContext());
        writableDatabase.beginTransaction();
        try {
            int iMatch = MATCHER.match(uri);
            if (iMatch == 1) {
                iUpdate = writableDatabase.update("event_comments", contentValues, str, strArr);
                writableDatabase.setTransactionSuccessful();
            } else if (iMatch == 2) {
                iUpdate = writableDatabase.update("event_comments", contentValues, "_id=" + uri.getLastPathSegment(), null);
                writableDatabase.setTransactionSuccessful();
            } else {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            writableDatabase.endTransaction();
            if (iUpdate > 0) {
                notifyContentChange(uri);
            }
            return iUpdate;
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int iDelete;
        SQLiteDatabase writableDatabase = DatabaseFactory.getWritableDatabase(getContext());
        writableDatabase.beginTransaction();
        try {
            int iMatch = MATCHER.match(uri);
            if (iMatch == 1) {
                iDelete = writableDatabase.delete("event_comments", str, strArr);
                writableDatabase.setTransactionSuccessful();
            } else if (iMatch == 2) {
                iDelete = writableDatabase.delete("event_comments", "_id=" + uri.getLastPathSegment(), null);
                writableDatabase.setTransactionSuccessful();
            } else {
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
            writableDatabase.endTransaction();
            if (iDelete > 0) {
                notifyContentChange(uri);
            }
            return iDelete;
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
    }

    private void notifyContentChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, false);
    }
}
