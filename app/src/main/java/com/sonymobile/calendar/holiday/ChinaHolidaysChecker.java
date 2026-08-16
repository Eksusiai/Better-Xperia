package com.sonymobile.calendar.holiday;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class ChinaHolidaysChecker {
    private static final String ACTION_VIEW_UPDATECENTER = "com.sonyericsson.updatecenter.action.VIEW";
    public static final String APPLICATION_UPDATE_DIR = "applicationUpdate";
    public static final String QUERY_PACKAGE_NAME = "com.sonymobile.chinaholidays";
    public static final String UPDATE_DIR = "updates";
    private AlertDialog mAlertDialog = null;
    public static final String UPDATE_CENTER_AUTHORITY = "com.sonyericsson.updatecenter.provider";
    public static final Uri BASE_UPDATE_CENTER_URI = new Uri.Builder().scheme("content").authority(UPDATE_CENTER_AUTHORITY).build();
    public static final String QUERY_LABEL_PACKAGE_IN_UPDATE_CENTER = "package";
    public static final String[] UPDATE_ELEMENT_PROJECTION = {"_id", "name", "version", QUERY_LABEL_PACKAGE_IN_UPDATE_CENTER};
    private static ChinaHolidaysChecker sChinaHolidaysChecker = null;

    private ChinaHolidaysChecker() {
    }

    public static ChinaHolidaysChecker getInstance() {
        if (sChinaHolidaysChecker == null) {
            sChinaHolidaysChecker = new ChinaHolidaysChecker();
        }
        return sChinaHolidaysChecker;
    }

    public void check(Context context) {
        keepHolidayPluginFresh(context);
        keepLocalHolidayFresh(context);
    }

    private void keepHolidayPluginFresh(Context context) {
        if (ChinaHolidaysUtils.hasRemoteHolidayPlugin(context)) {
            String remoteHolidayPluginVersion = ChinaHolidaysUtils.getRemoteHolidayPluginVersion(context);
            if (ChinaHolidaysUtils.hasLocalHolidayPlugin(context)) {
                if (ChinaHolidaysUtils.getLocalHolidayPluginVersion(context).equals(remoteHolidayPluginVersion)) {
                    return;
                }
                remindChinaHolidays(context);
                return;
            }
            remindChinaHolidays(context);
        }
    }

    private void keepLocalHolidayFresh(Context context) {
        if (ChinaHolidaysUtils.hasLocalHolidayPlugin(context)) {
            String localHolidayPluginVersion = ChinaHolidaysUtils.getLocalHolidayPluginVersion(context);
            String holidayLocalVersion = ChinaHolidaysUtils.getHolidayLocalVersion(context);
            if (!TextUtils.isEmpty(holidayLocalVersion)) {
                if (holidayLocalVersion.equals(localHolidayPluginVersion)) {
                    return;
                }
                ChinaHolidaysUtils.takeChinaHolidayJson(context);
                return;
            }
            ChinaHolidaysUtils.takeChinaHolidayJson(context);
        }
    }

    public void stop() {
        sChinaHolidaysChecker = null;
    }

    private void remindChinaHolidays(final Context context) {
        if (context.getSharedPreferences("chinaholidays_reminder", 0).getBoolean("reminder_again", true)) {
            Cursor cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(BASE_UPDATE_CENTER_URI, APPLICATION_UPDATE_DIR), UPDATE_ELEMENT_PROJECTION, "package='com.sonymobile.chinaholidays'", null, null);
            if (cursorQuery != null) {
                if (cursorQuery.moveToFirst()) {
                    do {
                        final Uri uriWithAppendedId = ContentUris.withAppendedId(Uri.withAppendedPath(BASE_UPDATE_CENTER_URI, UPDATE_DIR), cursorQuery.getInt(0));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                        builder.setView(R.layout.chinaholidays_reminder_dialog).setNegativeButton(R.string.chinaholidays_cancel_btn, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.chinaholidays_ok_btn, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.holiday.ChinaHolidaysChecker.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ChinaHolidaysChecker.ACTION_VIEW_UPDATECENTER);
                                intent.setData(uriWithAppendedId);
                                context.startActivity(intent);
                            }
                        });
                        AlertDialog alertDialog = this.mAlertDialog;
                        if (alertDialog != null) {
                            if (alertDialog.isShowing()) {
                                this.mAlertDialog.dismiss();
                            }
                            this.mAlertDialog = null;
                        }
                        AlertDialog alertDialogCreate = builder.create();
                        this.mAlertDialog = alertDialogCreate;
                        alertDialogCreate.show();
                        ((CheckBox) this.mAlertDialog.findViewById(R.id.chinaholiday_check_reminder)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.holiday.ChinaHolidaysChecker.2
                            @Override // android.widget.CompoundButton.OnCheckedChangeListener
                            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                context.getSharedPreferences("chinaholidays_reminder", 0).edit().putBoolean("reminder_again", !z).commit();
                            }
                        });
                    } while (cursorQuery.moveToNext());
                }
                cursorQuery.close();
            }
        }
    }
}
