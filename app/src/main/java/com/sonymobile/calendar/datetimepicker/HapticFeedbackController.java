package com.sonymobile.calendar.datetimepicker;

import android.content.Context;
import android.database.ContentObserver;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;

/* JADX INFO: loaded from: classes2.dex */
public class HapticFeedbackController {
    private static final int VIBRATE_DELAY_MS = 125;
    private static final int VIBRATE_LENGTH_MS = 5;
    private final ContentObserver mContentObserver = new ContentObserver(null) { // from class: com.sonymobile.calendar.datetimepicker.HapticFeedbackController.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            HapticFeedbackController hapticFeedbackController = HapticFeedbackController.this;
            hapticFeedbackController.mIsGloballyEnabled = HapticFeedbackController.checkGlobalSetting(hapticFeedbackController.mContext);
        }
    };
    private final Context mContext;
    private boolean mIsGloballyEnabled;
    private long mLastVibrate;
    private Vibrator mVibrator;

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean checkGlobalSetting(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 0) == 1;
    }

    public HapticFeedbackController(Context context) {
        this.mContext = context;
    }

    public void start() {
        this.mVibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mIsGloballyEnabled = checkGlobalSetting(this.mContext);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("haptic_feedback_enabled"), false, this.mContentObserver);
    }

    public void stop() {
        this.mVibrator = null;
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    public void tryVibrate() {
        if (this.mVibrator == null || !this.mIsGloballyEnabled) {
            return;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis - this.mLastVibrate >= 125) {
            this.mVibrator.vibrate(5L);
            this.mLastVibrate = jUptimeMillis;
        }
    }
}
