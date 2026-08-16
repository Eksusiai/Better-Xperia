package com.android.ex.chips;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.collection.LruCache;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class DefaultPhotoManager implements PhotoManager {
    private static final int BUFFER_SIZE = 16384;
    private static final boolean DEBUG = false;
    private static final String TAG = "DefaultPhotoManager";
    private final ContentResolver mContentResolver;
    private final LruCache<Uri, byte[]> mPhotoCacheMap = new LruCache<>(20);

    private static class PhotoQuery {
        public static final int PHOTO = 0;
        public static final String[] PROJECTION = {"data15"};

        private PhotoQuery() {
        }
    }

    public DefaultPhotoManager(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    @Override // com.android.ex.chips.PhotoManager
    public void populatePhotoBytesAsync(RecipientEntry recipientEntry, PhotoManager.PhotoManagerCallback photoManagerCallback) {
        Uri photoThumbnailUri = recipientEntry.getPhotoThumbnailUri();
        if (photoThumbnailUri == null) {
            if (photoManagerCallback != null) {
                photoManagerCallback.onPhotoBytesAsyncLoadFailed();
                return;
            }
            return;
        }
        byte[] bArr = this.mPhotoCacheMap.get(photoThumbnailUri);
        if (bArr != null) {
            recipientEntry.setPhotoBytes(bArr);
            if (photoManagerCallback != null) {
                photoManagerCallback.onPhotoBytesPopulated();
                return;
            }
            return;
        }
        fetchPhotoAsync(recipientEntry, photoThumbnailUri, photoManagerCallback);
    }

    private void fetchPhotoAsync(final RecipientEntry recipientEntry, final Uri uri, final PhotoManager.PhotoManagerCallback photoManagerCallback) {
        new AsyncTask<Void, Void, byte[]>() { // from class: com.android.ex.chips.DefaultPhotoManager.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                Cursor cursorQuery = DefaultPhotoManager.this.mContentResolver.query(uri, PhotoQuery.PROJECTION, null, null, null);
                if (cursorQuery == null) {
                    try {
                        InputStream inputStreamOpenInputStream = DefaultPhotoManager.this.mContentResolver.openInputStream(uri);
                        if (inputStreamOpenInputStream == null) {
                            return null;
                        }
                        byte[] bArr = new byte[16384];
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        while (true) {
                            try {
                                int i = inputStreamOpenInputStream.read(bArr);
                                if (i != -1) {
                                    byteArrayOutputStream.write(bArr, 0, i);
                                } else {
                                    inputStreamOpenInputStream.close();
                                    return byteArrayOutputStream.toByteArray();
                                }
                            } catch (Throwable th) {
                                inputStreamOpenInputStream.close();
                                throw th;
                            }
                        }
                    } catch (IOException unused) {
                        return null;
                    }
                } else {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            return cursorQuery.getBlob(0);
                        }
                        return null;
                    } finally {
                        cursorQuery.close();
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                recipientEntry.setPhotoBytes(bArr);
                if (bArr != null) {
                    DefaultPhotoManager.this.mPhotoCacheMap.put(uri, bArr);
                    PhotoManager.PhotoManagerCallback photoManagerCallback2 = photoManagerCallback;
                    if (photoManagerCallback2 != null) {
                        photoManagerCallback2.onPhotoBytesAsynchronouslyPopulated();
                        return;
                    }
                    return;
                }
                PhotoManager.PhotoManagerCallback photoManagerCallback3 = photoManagerCallback;
                if (photoManagerCallback3 != null) {
                    photoManagerCallback3.onPhotoBytesAsyncLoadFailed();
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Void[0]);
    }
}
