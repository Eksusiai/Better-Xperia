package com.sonymobile.calendar.provider.observer;

import android.content.Context;
import android.net.Uri;
import android.os.HandlerThread;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class ObserverThread extends HandlerThread implements EventsContentObserver.Callbacks {
    private static final String TAG = "ObserverThread";
    private final Uri mObservableUri;
    private final WeakReference<Context> mWeakContext;

    public ObserverThread(Context context, Uri uri) {
        super(TAG, 10);
        this.mWeakContext = new WeakReference<>(context);
        this.mObservableUri = uri;
    }

    @Override // java.lang.Thread
    public synchronized void start() {
        super.start();
        if (!registerContentObserver()) {
            getLooper().quit();
        }
    }

    @Override // com.sonymobile.calendar.provider.observer.EventsContentObserver.Callbacks
    public void onFinish() {
        getLooper().quit();
    }

    private boolean registerContentObserver() {
        Context context = this.mWeakContext.get();
        if (context == null) {
            return false;
        }
        context.getContentResolver().registerContentObserver(this.mObservableUri, false, new EventsContentObserver(context, getLooper(), this));
        return true;
    }
}
