package com.sonymobile.calendar.provider.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.sonymobile.calendar.provider.observer.synchronizer.ContentSynchronizer;
import com.sonymobile.calendar.provider.observer.synchronizer.Synchronizer;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
class EventsContentObserver extends ContentObserver {
    private final Callbacks mCallbacks;
    private final Synchronizer mSynchronizer;
    private final WeakReference<Context> mWeakContext;

    interface Callbacks {
        void onFinish();
    }

    EventsContentObserver(Context context, Looper looper, Callbacks callbacks) {
        super(new Handler(looper));
        this.mSynchronizer = new ContentSynchronizer();
        this.mWeakContext = new WeakReference<>(context);
        this.mCallbacks = callbacks;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        onChange(uri);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        onChange((Uri) null);
    }

    private void onChange(Uri uri) {
        Context context = this.mWeakContext.get();
        if (context != null) {
            this.mSynchronizer.sync(context, uri);
        } else {
            this.mCallbacks.onFinish();
        }
    }
}
