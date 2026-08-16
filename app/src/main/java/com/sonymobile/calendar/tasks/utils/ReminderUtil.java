package com.sonymobile.calendar.tasks.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public class ReminderUtil {
    private static final String SELECTION_FILTER_NOTIF = "name= ?";

    public static String convertReminderPathToInternalUri(Context context, String str) {
        long j;
        Cursor cursorQuery = null;
        if (context == null || TextUtils.isEmpty(str)) {
            return null;
        }
        Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        String[] strArr = {"_id", "_display_name"};
        String strTranslateDefaultUri = translateDefaultUri(context, str);
        if (strTranslateDefaultUri == null || !strTranslateDefaultUri.equals(str)) {
            return strTranslateDefaultUri;
        }
        int iLastIndexOf = str.lastIndexOf(47);
        String strSubstring = iLastIndexOf != -1 ? str.substring(iLastIndexOf + 1) : null;
        if (strSubstring != null) {
            try {
                cursorQuery = context.getContentResolver().query(uri, strArr, "is_notification=1", null, null);
                j = -1;
                if (cursorQuery != null) {
                    int columnIndex = cursorQuery.getColumnIndex("_display_name");
                    int columnIndex2 = cursorQuery.getColumnIndex("_id");
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        if (cursorQuery.getString(columnIndex).equalsIgnoreCase(strSubstring)) {
                            j = cursorQuery.getLong(columnIndex2);
                            break;
                        }
                        cursorQuery.moveToNext();
                    }
                }
                Utils.closeCursor(cursorQuery);
            } catch (Throwable th) {
                Utils.closeCursor(cursorQuery);
                throw th;
            }
        } else {
            j = -1;
        }
        if (j != -1) {
            str = ContentUris.withAppendedId(uri, j).toString();
        }
        return str;
    }

    private static String translateDefaultUri(Context context, String str) {
        if (str.equals(Settings.System.DEFAULT_NOTIFICATION_URI.toString())) {
            Cursor cursorQuery = null;
            try {
                cursorQuery = context.getContentResolver().query(Settings.System.CONTENT_URI, null, SELECTION_FILTER_NOTIF, new String[]{"notification_sound"}, null);
                if (cursorQuery != null && cursorQuery.moveToFirst()) {
                    str = cursorQuery.getString(cursorQuery.getColumnIndex("value"));
                }
            } finally {
                Utils.closeCursor(cursorQuery);
            }
        }
        return str;
    }

    public static boolean reminderHasBeenIndexed(String str) {
        int iLastIndexOf;
        return !TextUtils.isEmpty(str) && str.startsWith("content://media/") && (iLastIndexOf = str.lastIndexOf(47)) != -1 && str.substring(iLastIndexOf + 1).matches("[0-9]+");
    }

    public static boolean soundFileAccessible(Context context, String str) {
        if (context == null || str == null) {
            return false;
        }
        if (str.length() > 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, Uri.parse(str));
            } catch (Exception unused) {
                return false;
            } finally {
                mediaPlayer.release();
            }
        }
        return true;
    }
}
