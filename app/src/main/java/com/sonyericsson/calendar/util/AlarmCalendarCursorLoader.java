package com.sonyericsson.calendar.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes.dex */
public class AlarmCalendarCursorLoader extends CursorLoader {
    private static final String[] ALARM_PROJECTION = {"begin", "startDay", LunarContract.Instances.START_MINUTE, "description"};
    private static final String[] COLOR_PROJECTION = {LunarContract.CalendarColumns.CALENDAR_COLOR, "account_type"};
    private static final int INDEX_DAY = 1;
    private static final int INDEX_DESCRIPTION = 3;
    private static final int INDEX_MINUTE = 2;
    private static final int INDEX_TIME = 0;
    public static final int IS_ALARM_FLAG = -1;
    private static final int MAXIMUM_DAY_SPAN = 5000;
    private static final String TAG = "AlarmCalendarCursorLdr";
    private static final String URI_STRING = "content://com.sonyericsson.alarm/whenbyday";
    private Uri mAlarmUri;
    private int mDefaultCalendarColor;
    private int mEndDate;
    private boolean mIsWidget;
    private int mStartDate;

    public AlarmCalendarCursorLoader(Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2, boolean z) {
        super(context, uri, strArr, str, strArr2, str2);
        this.mStartDate = Integer.parseInt(uri.getPathSegments().get(2));
        int i = Integer.parseInt(uri.getPathSegments().get(3));
        this.mEndDate = i;
        int i2 = this.mStartDate;
        int i3 = i - i2;
        if (i3 > MAXIMUM_DAY_SPAN) {
            int i4 = (i3 - MAXIMUM_DAY_SPAN) / 2;
            this.mStartDate = i2 + i4;
            this.mEndDate = i - i4;
        }
        this.mAlarmUri = buildAlarmUri();
        this.mIsWidget = z;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.loader.content.CursorLoader, androidx.loader.content.AsyncTaskLoader
    public Cursor loadInBackground() {
        Cursor cursorLoadInBackground = super.loadInBackground();
        try {
            if (ContextCompat.checkSelfPermission(getContext(), "android.permission.READ_CALENDAR") == 0) {
                setDefaultCalendarColor(getContext().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, COLOR_PROJECTION, null, null, null));
            } else {
                this.mDefaultCalendarColor = UiUtils.getAccentColor(getContext());
            }
        } catch (Exception unused) {
            Log.e(TAG, "Unable to access " + CalendarContract.Calendars.CONTENT_URI);
        }
        Cursor cursorQuery = getContext().getContentResolver().query(this.mAlarmUri, ALARM_PROJECTION, null, null, null);
        try {
            MatrixCursor matrixCursor = new MatrixCursor(getProjection());
            if (this.mIsWidget) {
                if (cursorQuery != null) {
                    while (cursorQuery.moveToNext()) {
                        matrixCursor.addRow(buildWidgetEvent(cursorQuery));
                    }
                }
            } else if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    matrixCursor.addRow(buildCalendarEvent(cursorQuery));
                }
            }
            MergeCursor mergeCursor = new MergeCursor(new Cursor[]{cursorLoadInBackground, matrixCursor});
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return mergeCursor;
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

    private Object[] buildCalendarEvent(Cursor cursor) {
        return new Object[]{0, cursor.getString(3), "", 0, -1, Integer.valueOf(this.mDefaultCalendarColor), 0, "", cursor.getString(0), cursor.getString(0), 0, cursor.getString(1), cursor.getString(1), 0, 0, 0, 0, "", "", ""};
    }

    private Object[] buildWidgetEvent(Cursor cursor) {
        return new Object[]{0, cursor.getString(0), cursor.getString(0), cursor.getString(3), "", 0, cursor.getString(1), cursor.getString(1), 0, Integer.valueOf(this.mDefaultCalendarColor), cursor.getString(2), cursor.getString(2), 0, "", 0, -1};
    }

    private Uri buildAlarmUri() {
        Uri.Builder builderBuildUpon = Uri.parse(URI_STRING).buildUpon();
        ContentUris.appendId(builderBuildUpon, this.mStartDate);
        ContentUris.appendId(builderBuildUpon, this.mEndDate);
        return builderBuildUpon.build();
    }

    private void setDefaultCalendarColor(Cursor cursor) {
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(LunarContract.CalendarColumns.CALENDAR_COLOR);
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("account_type");
        while (cursor.moveToNext()) {
            if (cursor.getString(columnIndexOrThrow2).equalsIgnoreCase("LOCAL")) {
                this.mDefaultCalendarColor = cursor.getInt(columnIndexOrThrow);
                break;
            }
        }
        Utils.closeCursor(cursor);
    }
}
