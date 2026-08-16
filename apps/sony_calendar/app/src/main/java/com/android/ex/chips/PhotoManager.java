package com.android.ex.chips;

/* JADX INFO: loaded from: classes.dex */
public interface PhotoManager {
    public static final int PHOTO_CACHE_SIZE = 20;

    public interface PhotoManagerCallback {
        void onPhotoBytesAsyncLoadFailed();

        void onPhotoBytesAsynchronouslyPopulated();

        void onPhotoBytesPopulated();
    }

    void populatePhotoBytesAsync(RecipientEntry recipientEntry, PhotoManagerCallback photoManagerCallback);
}
