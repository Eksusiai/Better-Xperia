package com.sonymobile.calendar.holiday;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class ChinaHolidaysUtils {
    private static final String HOLIDAYS_ASSETS_SUBPATH = "holidays";

    /* JADX WARN: Code duplicated, block: B:50:0x00c9 A[Catch: IOException -> 0x00c5, TRY_LEAVE, TryCatch #1 {IOException -> 0x00c5, blocks: (B:46:0x00c1, B:50:0x00c9), top: B:55:0x00c1 }] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r0v5 */
    public static String getHolidayLocalVersion(Context context) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + ChinaHolidaysManager.FILE_VERSION_JSON_NAME);
        String string = "";
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    string = new JSONObject(sb.toString()).getString("version");
                    Log.d("333", "hasHolidayLocalVersion version = " + string);
                } finally {
                    bufferedReader.close();
                }
            } catch (IOException e10) {
                e10.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return string;
    }

    public static boolean hasLocalHolidayPlugin(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            packageManager.getApplicationInfo(ChinaHolidaysChecker.QUERY_PACKAGE_NAME, 128);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static String getLocalHolidayPluginVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager != null ? packageManager.getPackageInfo(ChinaHolidaysChecker.QUERY_PACKAGE_NAME, 128).versionName : "";
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    public static boolean hasRemoteHolidayPlugin(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(ChinaHolidaysChecker.BASE_UPDATE_CENTER_URI, ChinaHolidaysChecker.APPLICATION_UPDATE_DIR), ChinaHolidaysChecker.UPDATE_ELEMENT_PROJECTION, "package='com.sonymobile.chinaholidays'", null, null);
        boolean z = false;
        if (cursorQuery != null) {
            z = cursorQuery.getCount() > 0;
            cursorQuery.close();
        }
        return z;
    }

    public static String getRemoteHolidayPluginVersion(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(ChinaHolidaysChecker.BASE_UPDATE_CENTER_URI, ChinaHolidaysChecker.APPLICATION_UPDATE_DIR), ChinaHolidaysChecker.UPDATE_ELEMENT_PROJECTION, "package='com.sonymobile.chinaholidays'", null, null);
        String string = "";
        if (cursorQuery != null) {
            if (cursorQuery.moveToFirst()) {
                do {
                    string = cursorQuery.getString(2);
                } while (cursorQuery.moveToNext());
            }
            cursorQuery.close();
        }
        return string;
    }

    public static void takeChinaHolidayJson(Context context) {
        try {
            Context contextCreatePackageContext = context.createPackageContext(ChinaHolidaysChecker.QUERY_PACKAGE_NAME, 2);
            String[] list = contextCreatePackageContext.getAssets().list(HOLIDAYS_ASSETS_SUBPATH);
            int length = list.length;
            for (int i = 0; i < length; i++) {
                writeJsonToLocal(context.getFilesDir().getAbsolutePath(), list[i], contextCreatePackageContext.getAssets().open("holidays/" + list[i]));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARN: Code duplicated, block: B:34:0x0064 A[Catch: IOException -> 0x0060, TRY_LEAVE, TryCatch #2 {IOException -> 0x0060, blocks: (B:30:0x005c, B:34:0x0064), top: B:41:0x005c }] */
    private static void writeJsonToLocal(String str, String str2, InputStream inputStream) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(str + "/" + str2));
            try {
                while (true) {
                    int i = inputStream.read();
                    if (i == -1) {
                        break;
                    }
                    fileOutputStream.write(i);
                }
                fileOutputStream.flush();
            } finally {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
