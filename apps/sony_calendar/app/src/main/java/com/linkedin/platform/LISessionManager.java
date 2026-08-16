package com.linkedin.platform;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.linkedin.platform.errors.LIAppErrorCode;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.internals.AppStore;
import com.linkedin.platform.internals.LIAppVersion;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LISessionManager {
    private static final String AUTH_STATE = "state";
    private static final String AUTH_TOKEN = "token";
    private static final String LI_APP_ACTION_AUTHORIZE_APP = "com.linkedin.android.auth.AUTHORIZE_APP";
    private static final String LI_APP_AUTH_CLASS_NAME = "com.linkedin.android.liauthlib.thirdparty.LiThirdPartyAuthorizeActivity";
    private static final String LI_APP_CATEGORY = "com.linkedin.android.auth.thirdparty.authorize";
    private static final String LI_APP_PACKAGE_NAME = "com.linkedin.android";
    private static final String LI_ERROR_DESCRIPTION = "com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_DESCRIPTION";
    private static final String LI_ERROR_INFO = "com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_INFO";
    private static final int LI_SDK_AUTH_REQUEST_CODE = 3672;
    private static final String SCOPE_DATA = "com.linkedin.thirdpartysdk.SCOPE_DATA";
    private static final String TAG = "LISessionManager";
    private static LISessionManager sessionManager;
    private AuthListener authListener;
    private Context ctx;
    private LISessionImpl session = new LISessionImpl();

    public static LISessionManager getInstance(Context context) {
        if (sessionManager == null) {
            sessionManager = new LISessionManager();
        }
        if (context != null) {
            LISessionManager lISessionManager = sessionManager;
            if (lISessionManager.ctx == null) {
                lISessionManager.ctx = context.getApplicationContext();
            }
        }
        return sessionManager;
    }

    private LISessionManager() {
    }

    public void init(AccessToken accessToken) {
        this.session.setAccessToken(accessToken);
    }

    public void init(Activity activity, Scope scope, AuthListener authListener, boolean z) {
        if (!LIAppVersion.isLIAppCurrent(this.ctx)) {
            AppStore.goAppStore(activity, z);
            return;
        }
        this.authListener = authListener;
        Intent intent = new Intent();
        intent.setClassName("com.linkedin.android", LI_APP_AUTH_CLASS_NAME);
        intent.putExtra(SCOPE_DATA, scope.createScope());
        intent.setAction(LI_APP_ACTION_AUTHORIZE_APP);
        intent.addCategory(LI_APP_CATEGORY);
        try {
            activity.startActivityForResult(intent, LI_SDK_AUTH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        Log.d(TAG, "onActivityResult called requestCode=" + i);
        AuthListener authListener = this.authListener;
        if (authListener == null || i != LI_SDK_AUTH_REQUEST_CODE) {
            return;
        }
        if (i2 == -1) {
            init(new AccessToken(intent.getStringExtra(AUTH_TOKEN), intent.getLongExtra("expiresOn", 0L)));
            this.authListener.onAuthSuccess();
        } else if (i2 == 0) {
            authListener.onAuthError(new LIAuthError(LIAppErrorCode.USER_CANCELLED, "user canceled"));
        } else {
            this.authListener.onAuthError(new LIAuthError(intent.getStringExtra(LI_ERROR_INFO), intent.getStringExtra(LI_ERROR_DESCRIPTION)));
        }
        this.authListener = null;
    }

    public LISession getSession() {
        return this.session;
    }

    public void clearSession() {
        this.session.setAccessToken(null);
    }

    private static String createScope(List<String> list) {
        return (list == null || list.isEmpty()) ? "" : TextUtils.join(" ", list);
    }

    private static class LISessionImpl implements LISession {
        private static final String LI_SDK_SHARED_PREF_STORE = "li_shared_pref_store";
        private static final String SHARED_PREFERENCES_ACCESS_TOKEN = "li_sdk_access_token";
        private AccessToken accessToken = null;

        @Override // com.linkedin.platform.LISession
        public AccessToken getAccessToken() {
            if (this.accessToken == null) {
                recover();
            }
            return this.accessToken;
        }

        void setAccessToken(AccessToken accessToken) {
            this.accessToken = accessToken;
            save();
        }

        @Override // com.linkedin.platform.LISession
        public boolean isValid() {
            AccessToken accessToken = getAccessToken();
            return (accessToken == null || accessToken.isExpired()) ? false : true;
        }

        public void clear() {
            setAccessToken(null);
        }

        private SharedPreferences getSharedPref() {
            return LISessionManager.sessionManager.ctx.getSharedPreferences(LI_SDK_SHARED_PREF_STORE, 0);
        }

        private void save() {
            SharedPreferences.Editor editorEdit = getSharedPref().edit();
            AccessToken accessToken = this.accessToken;
            editorEdit.putString(SHARED_PREFERENCES_ACCESS_TOKEN, accessToken == null ? null : accessToken.toString());
            editorEdit.commit();
        }

        private void recover() {
            String string = getSharedPref().getString(SHARED_PREFERENCES_ACCESS_TOKEN, null);
            this.accessToken = string != null ? AccessToken.buildAccessToken(string) : null;
        }
    }
}
