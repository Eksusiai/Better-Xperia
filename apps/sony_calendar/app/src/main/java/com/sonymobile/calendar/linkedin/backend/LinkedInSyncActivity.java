package com.sonymobile.calendar.linkedin.backend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.GaUtils;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.linkedin.LinkedInUtils;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInSyncActivity extends Activity {
    public static final String LINKEDIN_SYNC_RESULT_ACTION = "linkedinResultAction";
    public static final String LINKEDIN_SYNC_SUCCESS_PARAMETER = "parameterSuccess";
    public static final String LINKEDIN_SYNC_TOKEN_PARAMETER = "parameterToken";
    private AuthListener authListener = new AuthListener() { // from class: com.sonymobile.calendar.linkedin.backend.LinkedInSyncActivity.1
        @Override // com.linkedin.platform.listeners.AuthListener
        public void onAuthSuccess() {
            LinkedInSyncActivity linkedInSyncActivity = LinkedInSyncActivity.this;
            linkedInSyncActivity.setResultAndFinish(true, LISessionManager.getInstance(linkedInSyncActivity.getApplicationContext()).getSession().getAccessToken().toString());
        }

        @Override // com.linkedin.platform.listeners.AuthListener
        public void onAuthError(LIAuthError lIAuthError) {
            LinkedInSyncActivity.this.setResultAndFinish(false, null);
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Utils.checkPackageAvailable(this, "com.linkedin.android")) {
            GaUtils.sendEvent(GaUtils.LINKEDIN_ACTIVATION_CATEGORY, GaUtils.LINKEDIN_APP_STORE_LAUNCHED);
            LinkedInUtils.goToAppStore(this);
            setResultCanceled();
        } else {
            LISessionManager.getInstance(getApplicationContext()).init(this, makeScope(), this.authListener, false);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, i, i2, intent);
    }

    private Scope makeScope() {
        return Scope.build(Scope.R_BASICPROFILE, new Scope.LIPermission("w_messages", "Send messages to LinkedIn contact"), Scope.R_EMAILADDRESS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setResultAndFinish(boolean z, String str) {
        Intent intent = new Intent(LINKEDIN_SYNC_RESULT_ACTION);
        intent.putExtra(LINKEDIN_SYNC_SUCCESS_PARAMETER, z);
        if (str != null) {
            intent.putExtra(LINKEDIN_SYNC_TOKEN_PARAMETER, str);
        }
        setResult(-1, intent);
        finish();
    }

    private void setResultCanceled() {
        setResult(0, new Intent(LINKEDIN_SYNC_RESULT_ACTION));
        finish();
    }
}
