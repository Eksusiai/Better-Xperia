package com.sonymobile.calendar.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioAttributes;
import android.provider.Settings;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.utils.TaskConfig;

/* JADX INFO: loaded from: classes2.dex */
public class NotificationUtils extends ContextWrapper {
    public static final int CHANNEL_HIGH = 1;
    public static final String NOTIFICATION_NOTICEABLE_CHANNEL_ID = "notification_importance_high_calendar";
    public static final String NOTIFICATION_TASKS_CHANNEL_ID = "notification_tasks";
    private static NotificationChannel notifyChannelhigh;
    private NotificationManager mManager;

    public NotificationUtils(Context context) {
        super(context);
        createChannels();
    }

    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_NOTICEABLE_CHANNEL_ID, getString(R.string.notification_calendar_channel_name), 4);
        notifyChannelhigh = notificationChannel;
        notificationChannel.enableLights(true);
        notifyChannelhigh.enableVibration(false);
        notifyChannelhigh.setLightColor(-1);
        notifyChannelhigh.setLockscreenVisibility(1);
        getManager().createNotificationChannel(notifyChannelhigh);
    }

    private NotificationManager getManager() {
        if (this.mManager == null) {
            this.mManager = (NotificationManager) getSystemService("notification");
        }
        return this.mManager;
    }

    public static NotificationChannel getNotifyChannel(int i) {
        if (i == 1) {
            return notifyChannelhigh;
        }
        return null;
    }

    public void createTaskChannels(Context context) {
        NotificationChannel notificationChannel = new NotificationChannel("notification_tasks", context.getString(R.string.notification_tasks_channel_name), 4);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(-1);
        TaskConfig.getTranslatedReminderSound(context);
        notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder().setUsage(5).setContentType(2).build());
        notificationChannel.enableVibration(false);
        getManager().createNotificationChannel(notificationChannel);
    }

    public static Intent getHighNotificationSettingsIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        intent.putExtra("android.provider.extra.CHANNEL_ID", NOTIFICATION_NOTICEABLE_CHANNEL_ID);
        return intent;
    }

    public static String getChannelSummary(Context context, NotificationChannel notificationChannel, boolean z) {
        boolean zShouldShowLights;
        boolean zShouldVibrate;
        int importance = notificationChannel.getImportance();
        boolean z2 = true;
        boolean z3 = false;
        if (importance == 0) {
            zShouldVibrate = false;
            z2 = false;
            zShouldShowLights = false;
        } else if (importance <= 2) {
            zShouldVibrate = false;
            zShouldShowLights = false;
        } else {
            boolean z4 = notificationChannel.getSound() != null;
            zShouldShowLights = notificationChannel.shouldShowLights();
            zShouldVibrate = z ? notificationChannel.shouldVibrate() : false;
            z3 = z4;
        }
        return generateNotificationSummaryForAttributes(context, z2, z3, zShouldShowLights, zShouldVibrate);
    }

    public static String generateNotificationSummaryForAttributes(Context context, boolean z, boolean z2, boolean z3, boolean z4) {
        if (z) {
            StringBuilder sb = new StringBuilder(context.getString(R.string.notification_statusbar_summary));
            if (z2) {
                sb.append(context.getString(R.string.notification_sound_summary));
            }
            if (z3) {
                sb.append(context.getString(R.string.notification_light_summary));
            }
            if (z4) {
                sb.append(context.getString(R.string.notification_vibration_summary));
            }
            return sb.toString();
        }
        return context.getString(R.string.notification_none_summary);
    }
}
