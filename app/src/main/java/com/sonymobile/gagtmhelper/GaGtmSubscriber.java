package com.sonymobile.gagtmhelper;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class GaGtmSubscriber extends ContentObserver {
    private static final String LOG_TAG = "GaGtmHelper";
    private final Context mContext;
    private boolean mSubscribing;
    private static final String SOMC_GA_ENABLED_SETTING = "somc.google_analytics_enabled";
    private static final Uri GA_URI = Settings.System.getUriFor(SOMC_GA_ENABLED_SETTING);

    public GaGtmSubscriber(Context context) throws IllegalArgumentException {
        super(null);
        this.mSubscribing = false;
        if (context == null) {
            throw new IllegalArgumentException("context is not allowed to be null");
        }
        this.mContext = context;
    }

    public GaGtmSubscriber(Context context, Handler handler) throws IllegalArgumentException {
        super(handler);
        this.mSubscribing = false;
        if (context == null) {
            throw new IllegalArgumentException("context is not allowed to be null");
        }
        this.mContext = context;
    }

    public void subscribeGaSettingChanges() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "subscribeGaSettingChanges");
        }
        GaGtmSystemSetting.readAndSetSomcGa(this.mContext);
        if (this.mSubscribing) {
            return;
        }
        this.mContext.getContentResolver().registerContentObserver(GA_URI, false, this);
        this.mSubscribing = true;
    }

    public void unsubscribeGaSettingChanges() {
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "unsubscribeGaSettingChanges");
        }
        if (this.mSubscribing) {
            this.mContext.getContentResolver().unregisterContentObserver(this);
            this.mSubscribing = false;
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        super.onChange(z);
        if (GaGtmLog.isEnabled()) {
            Log.d(LOG_TAG, "onChange");
        }
        GaGtmSystemSetting.readAndSetSomcGa(this.mContext);
    }
}
