package com.sonymobile.calendar.tasks.utils;

import android.content.Context;
import com.sonymobile.calendar.tasks.settings.Settings;

/* JADX INFO: loaded from: classes2.dex */
public class TaskConfig {
    private static Settings mSettings;

    public static boolean getDisplayCompletedTaskSettings(Context context) {
        Settings settings = Settings.getInstance(context);
        mSettings = settings;
        return settings.getBoolean(Settings.KEY_DISPLAY_COMPLETED_TASK, false);
    }

    public static String getTranslatedReminderSound(Context context) {
        Settings settings = Settings.getInstance(context);
        mSettings = settings;
        String string = settings.getString(Settings.KEY_REMINDER_RINGTONE, null);
        return !ReminderUtil.reminderHasBeenIndexed(string) ? ReminderUtil.convertReminderPathToInternalUri(context, string) : string;
    }

    public static boolean getReminderVibrateSettings(Context context) {
        Settings settings = Settings.getInstance(context);
        mSettings = settings;
        return settings.getBoolean(Settings.KEY_REMINDER_VIBRATE, false);
    }
}
