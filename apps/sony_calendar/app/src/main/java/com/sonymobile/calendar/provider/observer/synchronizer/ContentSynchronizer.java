package com.sonymobile.calendar.provider.observer.synchronizer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import com.sonymobile.calendar.provider.SomcCalendarContract;

/* JADX INFO: loaded from: classes2.dex */
public class ContentSynchronizer extends AbstractContentSynchronizer {
    private static final int SQLITE_MAX_VARIABLE_NUMBER = 998;
    private static final String TAG = "ContentSynchronizer";

    @Override // com.sonymobile.calendar.provider.observer.synchronizer.AbstractContentSynchronizer, com.sonymobile.calendar.provider.observer.synchronizer.Synchronizer
    public /* bridge */ /* synthetic */ void sync(Context context, Uri uri) {
        super.sync(context, uri);
    }

    @Override // com.sonymobile.calendar.provider.observer.synchronizer.AbstractContentSynchronizer
    protected void syncEvents(Context context, Uri uri) {
        Uri uri2 = CalendarContract.Events.CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        if (!isEventsSyncAllowed(context)) {
            Log.w(TAG, "Security warning: cannot sync events without android.permission.READ_CALENDAR");
            return;
        }
        Cursor cursorQuery = contentResolver.query(uri2, null, null, null, null);
        if (cursorQuery == null) {
            if (cursorQuery != null) {
                cursorQuery.close();
                return;
            }
            return;
        }
        try {
            int count = cursorQuery.getCount();
            int count2 = SQLITE_MAX_VARIABLE_NUMBER;
            if (count <= SQLITE_MAX_VARIABLE_NUMBER) {
                count2 = cursorQuery.getCount();
            }
            String[] strArr = new String[count2];
            StringBuilder sbAppend = new StringBuilder().append("event_id").append(" NOT IN (");
            int i = 0;
            while (i < count2 && cursorQuery.moveToNext()) {
                int i2 = i + 1;
                strArr[i] = String.valueOf(cursorQuery.getLong(cursorQuery.getColumnIndexOrThrow("_id")));
                sbAppend.append("?");
                if (i2 < count2) {
                    sbAppend.append(", ");
                }
                i = i2;
            }
            sbAppend.append(");");
            contentResolver.delete(SomcCalendarContract.EventComments.CONTENT_URI, sbAppend.toString(), strArr);
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                try {
                    cursorQuery.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private boolean isEventsSyncAllowed(Context context) {
        return Build.VERSION.SDK_INT < 23 || context.checkSelfPermission("android.permission.READ_CALENDAR") == 0;
    }
}
