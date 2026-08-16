package com.linkedin.platform;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.Scopes;
import com.linkedin.platform.errors.LIAppErrorCode;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.internals.AppStore;
import com.linkedin.platform.internals.LIAppVersion;
import com.linkedin.platform.listeners.DeepLinkListener;

/* JADX INFO: loaded from: classes.dex */
public class DeepLinkHelper {
    private static final String CURRENTLY_LOGGED_IN_MEMBER = "you";
    private static final String DEEPLINK_ERROR_CODE_EXTRA_NAME = "com.linkedin.thirdparty.deeplink.EXTRA_ERROR_CODE";
    private static final String DEEPLINK_ERROR_MESSAGE_EXTRA_NAME = "com.linkedin.thirdparty.deeplink.EXTRA_ERROR_MESSAGE";
    public static final int LI_SDK_CROSSLINK_REQUEST_CODE = 30769;
    private static final String TAG = "com.linkedin.platform.DeepLinkHelper";
    private static DeepLinkHelper deepLinkHelper;
    private DeepLinkListener deepLinkListener;

    public static DeepLinkHelper getInstance() {
        if (deepLinkHelper == null) {
            deepLinkHelper = new DeepLinkHelper();
        }
        return deepLinkHelper;
    }

    public void openCurrentProfile(Activity activity, DeepLinkListener deepLinkListener) {
        openOtherProfile(activity, CURRENTLY_LOGGED_IN_MEMBER, deepLinkListener);
    }

    public void openOtherProfile(Activity activity, String str, DeepLinkListener deepLinkListener) {
        this.deepLinkListener = deepLinkListener;
        LISession session = LISessionManager.getInstance(activity.getApplicationContext()).getSession();
        if (!session.isValid()) {
            deepLinkListener.onDeepLinkError(new LIDeepLinkError(LIAppErrorCode.NOT_AUTHENTICATED, "there is no access token"));
            return;
        }
        try {
            if (!LIAppVersion.isLIAppCurrent(activity)) {
                AppStore.goAppStore(activity, true);
            } else {
                deepLinkToProfile(activity, str, session.getAccessToken());
            }
        } catch (ActivityNotFoundException unused) {
            deepLinkListener.onDeepLinkError(new LIDeepLinkError(LIAppErrorCode.LINKEDIN_APP_NOT_FOUND, "LinkedIn app needs to be either installed or` updated"));
            this.deepLinkListener = null;
        }
    }

    private void deepLinkToProfile(Activity activity, String str, AccessToken accessToken) {
        Intent intent = new Intent("android.intent.action.VIEW");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("linkedin");
        if (CURRENTLY_LOGGED_IN_MEMBER.equals(str)) {
            builder.authority(CURRENTLY_LOGGED_IN_MEMBER);
        } else {
            builder.authority(Scopes.PROFILE).appendPath(str);
        }
        builder.appendQueryParameter("accessToken", accessToken.getValue());
        builder.appendQueryParameter("src", "sdk");
        builder.appendQueryParameter("referrer", "sony_experia");
        intent.setData(builder.build());
        Log.i("Url: ", builder.build().toString());
        activity.startActivityForResult(intent, LI_SDK_CROSSLINK_REQUEST_CODE);
    }

    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        DeepLinkListener deepLinkListener;
        if (i != 30769 || (deepLinkListener = this.deepLinkListener) == null) {
            return;
        }
        if (i2 == -1) {
            deepLinkListener.onDeepLinkSuccess();
            return;
        }
        if (i2 == 0) {
            if (intent == null || intent.getExtras() == null) {
                this.deepLinkListener.onDeepLinkError(new LIDeepLinkError(LIAppErrorCode.USER_CANCELLED, ""));
            } else {
                this.deepLinkListener.onDeepLinkError(new LIDeepLinkError(intent.getExtras().getString(DEEPLINK_ERROR_CODE_EXTRA_NAME), intent.getExtras().getString(DEEPLINK_ERROR_MESSAGE_EXTRA_NAME)));
            }
        }
    }
}
