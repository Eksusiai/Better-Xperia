package com.sonymobile.calendar.tasks.settings;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.utils.ReminderUtil;
import com.sonymobile.calendar.tasks.utils.TaskConfig;

/* JADX INFO: loaded from: classes2.dex */
public class ReminderRingtonePreference extends RingtonePreference {
    private static final long DOUBLE_CLICK_TRESHOLD = 500;
    private static final String MEDIA_INTERNAL = "media/internal";
    private boolean mIsExternalStorage;
    private long mLastClick;

    public ReminderRingtonePreference(Context context) {
        super(context);
        this.mIsExternalStorage = false;
    }

    public ReminderRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsExternalStorage = false;
    }

    public ReminderRingtonePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsExternalStorage = false;
    }

    @Override // android.preference.RingtonePreference
    protected void onPrepareRingtonePickerIntent(Intent intent) {
        intent.addCategory("android.intent.category.DEFAULT");
        String translatedReminderSound = TaskConfig.getTranslatedReminderSound(getContext());
        if (ReminderUtil.soundFileAccessible(getContext(), translatedReminderSound)) {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", Uri.parse(translatedReminderSound));
        } else if (translatedReminderSound != null) {
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(getContext(), 2));
        } else {
            intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
        }
        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        intent.putExtra("android.intent.extra.ringtone.INCLUDE_DRM", true);
        intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
        String string = getContext().getResources().getString(R.string.task_settings_pref_reminder_select_ringtone_title_txt);
        if (string != null) {
            intent.putExtra("android.intent.extra.ringtone.TITLE", string);
        }
    }

    @Override // android.preference.RingtonePreference, android.preference.Preference
    protected void onClick() {
        if (System.currentTimeMillis() - this.mLastClick < DOUBLE_CLICK_TRESHOLD) {
            return;
        }
        this.mLastClick = System.currentTimeMillis();
        super.onClick();
    }

    @Override // android.preference.RingtonePreference, android.preference.PreferenceManager.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            if (uri == null) {
                uri = Uri.parse("");
            }
            this.mIsExternalStorage = !uri.toString().contains(MEDIA_INTERNAL);
        }
        return super.onActivityResult(i, i2, intent);
    }

    public boolean isExternalStorage() {
        return this.mIsExternalStorage;
    }
}
