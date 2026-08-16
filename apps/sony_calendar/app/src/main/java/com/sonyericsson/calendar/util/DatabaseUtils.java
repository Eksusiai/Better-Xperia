package com.sonyericsson.calendar.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes.dex */
public class DatabaseUtils {
    public static final String ACCOUNT_NAME = "SYNCML-ORPHANED";
    public static final String ACCOUNT_TYPE = "LOCAL";
    private static final String CALENDARS_WHERE = "calendar_access_level>=500 AND sync_events=1 AND account_type=?";
    private static final String CALENDARS_WHERE_LUNAR = "account_type=?";
    private static final String[] CALENDAR_PROJECTION = {"_id", LunarContract.CalendarColumns.CALENDAR_COLOR, "calendar_displayName", LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, LunarContract.CalendarColumns.CALENDAR_TIME_ZONE};
    private static final String[] CALENDARS_WHERE_ARGS_LOCAL = {"LOCAL"};

    public static void createCalendarInDatabaseIfNeeded(Context context) {
        createCalendarInDatabaseIfNeeded(context, null);
    }

    public static void createCalendarInDatabaseIfNeeded(Context context, IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        new QueryDatabaseTask(iAsyncServiceResultHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    public static final Cursor doCalendarsQuery(ContentResolver contentResolver, String[] strArr, String str, String[] strArr2, String str2) {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        if (str2 == null) {
            str2 = "calendar_displayName";
        }
        return contentResolver.query(uri, strArr, str, strArr2, str2);
    }

    public static final Cursor doLunarCalendarsQuery(ContentResolver contentResolver, String[] strArr, String str, String[] strArr2, String str2) {
        Uri uri = LunarContract.Calendars.CONTENT_URI;
        if (str2 == null) {
            str2 = "calendar_displayName";
        }
        return contentResolver.query(uri, strArr, str, strArr2, str2);
    }

    public static Uri asSyncAdapter(Uri uri, String str, String str2) {
        return uri.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", str).appendQueryParameter("account_type", str2).build();
    }

    private static class QueryDatabaseTask extends AsyncTask<Context, Void, Void> {
        private IAsyncServiceResultHandler resultHandler;

        public QueryDatabaseTask(IAsyncServiceResultHandler iAsyncServiceResultHandler) {
            this.resultHandler = iAsyncServiceResultHandler;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Context... contextArr) {
            Context context = contextArr[0];
            String string = context.getString(R.string.calendar_strings_default_calendar_txt);
            int color = ContextCompat.getColor(context, R.color.default_calendar_color);
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursorDoCalendarsQuery = null;
            Cursor cursorDoLunarCalendarsQuery = null;
            try {
                if (LunarAvailabilityManager.isLunarAvailable(context)) {
                    cursorDoCalendarsQuery = DatabaseUtils.doCalendarsQuery(contentResolver, DatabaseUtils.CALENDAR_PROJECTION, DatabaseUtils.CALENDARS_WHERE_LUNAR, DatabaseUtils.CALENDARS_WHERE_ARGS_LOCAL, null);
                    cursorDoLunarCalendarsQuery = DatabaseUtils.doLunarCalendarsQuery(contentResolver, DatabaseUtils.CALENDAR_PROJECTION, DatabaseUtils.CALENDARS_WHERE_LUNAR, DatabaseUtils.CALENDARS_WHERE_ARGS_LOCAL, null);
                } else {
                    cursorDoCalendarsQuery = DatabaseUtils.doCalendarsQuery(contentResolver, DatabaseUtils.CALENDAR_PROJECTION, DatabaseUtils.CALENDARS_WHERE, DatabaseUtils.CALENDARS_WHERE_ARGS_LOCAL, null);
                }
                if (cursorDoCalendarsQuery != null && cursorDoCalendarsQuery.getCount() < 1) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", string);
                    contentValues.put("calendar_displayName", string);
                    contentValues.put(LunarContract.CalendarColumns.CALENDAR_COLOR, Integer.valueOf(color));
                    contentValues.put("account_name", DatabaseUtils.ACCOUNT_NAME);
                    contentValues.put("ownerAccount", " ");
                    contentValues.put("account_type", "LOCAL");
                    contentValues.put(LunarContract.CalendarColumns.SYNC_EVENTS, "1");
                    contentValues.put(LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, (Integer) 5000);
                    contentResolver.insert(DatabaseUtils.asSyncAdapter(CalendarContract.Calendars.CONTENT_URI, DatabaseUtils.ACCOUNT_NAME, "LOCAL"), contentValues);
                }
                if (cursorDoLunarCalendarsQuery != null && cursorDoLunarCalendarsQuery.getCount() < 1) {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("name", string);
                    contentValues2.put("calendar_displayName", string);
                    contentValues2.put(LunarContract.CalendarColumns.CALENDAR_COLOR, Integer.valueOf(color));
                    contentValues2.put("account_name", DatabaseUtils.ACCOUNT_NAME);
                    contentValues2.put("ownerAccount", " ");
                    contentValues2.put("account_type", "LOCAL");
                    contentValues2.put(LunarContract.CalendarColumns.SYNC_EVENTS, "1");
                    contentValues2.put(LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, (Integer) 5000);
                    contentResolver.insert(DatabaseUtils.asSyncAdapter(LunarContract.Calendars.CONTENT_URI, DatabaseUtils.ACCOUNT_NAME, "LOCAL"), contentValues2);
                }
                return null;
            } finally {
                Utils.closeCursor(cursorDoCalendarsQuery);
                Utils.closeCursor(cursorDoLunarCalendarsQuery);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r3) {
            super.onPostExecute(r3);
            IAsyncServiceResultHandler iAsyncServiceResultHandler = this.resultHandler;
            if (iAsyncServiceResultHandler != null) {
                iAsyncServiceResultHandler.onResult(r3, 0);
            }
        }
    }
}
