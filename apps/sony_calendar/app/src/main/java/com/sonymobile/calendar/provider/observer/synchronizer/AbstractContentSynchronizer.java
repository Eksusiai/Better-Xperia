package com.sonymobile.calendar.provider.observer.synchronizer;

import android.content.Context;
import android.net.Uri;

/* JADX INFO: loaded from: classes2.dex */
abstract class AbstractContentSynchronizer implements Synchronizer {
    protected abstract void syncEvents(Context context, Uri uri);

    AbstractContentSynchronizer() {
    }

    @Override // com.sonymobile.calendar.provider.observer.synchronizer.Synchronizer
    public void sync(Context context, Uri uri) {
        syncEvents(context, uri);
    }
}
