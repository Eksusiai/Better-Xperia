package com.sonyericsson.localcalendar.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AuthenticationService extends Service {
    private static final String TAG = "AuthenticationService";
    private Authenticator mAuthenticator;

    @Override // android.app.Service
    public void onCreate() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service started.");
        }
        this.mAuthenticator = new Authenticator(this);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "SampleSyncAdapter Authentication Service stopped.");
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent " + intent);
        }
        return this.mAuthenticator.getIBinder();
    }
}
