package com.sonymobile.calendar.birthday;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.LruCache;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes2.dex */
public class ContactPhotoService {
    private static ContactPhotoService instance;
    private Queue<OnReadyHandler> handlers = new LinkedList();
    private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(getCacheSize()) { // from class: com.sonymobile.calendar.birthday.ContactPhotoService.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public int sizeOf(String str, Bitmap bitmap) {
            return bitmap.getByteCount() / 1024;
        }
    };
    private Bitmap mEmptyPhotoBitmap;

    protected interface OnReadyHandler {
        void onReady();
    }

    private ContactPhotoService() {
    }

    public static ContactPhotoService getInstance() {
        if (instance == null) {
            instance = new ContactPhotoService();
        }
        return instance;
    }

    public void requestContactPhotos(Context context, ArrayList<ContactBirthday> arrayList, final IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        loadDataAsync(arrayList, new OnReadyHandler() { // from class: com.sonymobile.calendar.birthday.ContactPhotoService.2
            @Override // com.sonymobile.calendar.birthday.ContactPhotoService.OnReadyHandler
            public void onReady() {
                iAsyncServiceResultHandler.onResult(null, 0);
            }
        }, context);
    }

    private void loadDataAsync(ArrayList<ContactBirthday> arrayList, OnReadyHandler onReadyHandler, Context context) {
        this.handlers.add(onReadyHandler);
        new LoadDataAsyncTasks(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList);
    }

    private class LoadDataAsyncTasks extends AsyncTask<ArrayList<ContactBirthday>, Void, Void> {
        private Context context;

        public LoadDataAsyncTasks(Context context) {
            this.context = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        @SafeVarargs
        public final Void doInBackground(ArrayList<ContactBirthday>... arrayListArr) {
            if (arrayListArr.length == 0) {
                return null;
            }
            ContactPhotoService.this.imageCache.evictAll();
            for (ContactBirthday contactBirthday : arrayListArr[0]) {
                Bitmap bitmapLoadPhotoForContactBirthday = ContactPhotoService.this.loadPhotoForContactBirthday(this.context, contactBirthday.contactId);
                if (contactBirthday.contactId != null && bitmapLoadPhotoForContactBirthday != null) {
                    ContactPhotoService.this.imageCache.put(contactBirthday.contactId, bitmapLoadPhotoForContactBirthday);
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            while (!ContactPhotoService.this.handlers.isEmpty()) {
                ((OnReadyHandler) ContactPhotoService.this.handlers.remove()).onReady();
            }
        }
    }

    public Bitmap loadPhotoForContactBirthday(Context context, String str) {
        Bitmap bitmapDecodeStream;
        byte[] blob;
        Bitmap bitmap = this.imageCache.get(str);
        if (bitmap != null) {
            return bitmap;
        }
        Uri uriWithAppendedId = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(str));
        Cursor cursorQuery = null;
        try {
            try {
                AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(Uri.withAppendedPath(uriWithAppendedId, "display_photo"), "r");
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    bitmapDecodeStream = BitmapFactory.decodeStream(new BufferedInputStream(assetFileDescriptorOpenAssetFileDescriptor.createInputStream()));
                    try {
                        assetFileDescriptorOpenAssetFileDescriptor.close();
                    } catch (IOException unused) {
                        cursorQuery = context.getContentResolver().query(Uri.withAppendedPath(uriWithAppendedId, "photo"), new String[]{"data15"}, null, null, null);
                        if (cursorQuery != null && cursorQuery.getCount() > 0 && cursorQuery.moveToFirst() && (blob = cursorQuery.getBlob(0)) != null) {
                            bitmapDecodeStream = BitmapFactory.decodeStream(new ByteArrayInputStream(blob));
                        }
                    }
                } else {
                    bitmapDecodeStream = null;
                }
            } catch (Throwable th) {
                Utils.closeCursor(cursorQuery);
                throw th;
            }
        } catch (IOException unused2) {
            bitmapDecodeStream = null;
        }
        Utils.closeCursor(cursorQuery);
        return bitmapDecodeStream != null ? bitmapDecodeStream : getEmptyPhotoBitmap(context);
    }

    public Bitmap getPhoto(Context context, String str) {
        Bitmap bitmap = this.imageCache.get(str);
        return bitmap != null ? bitmap : getEmptyPhotoBitmap(context);
    }

    private Bitmap getEmptyPhotoBitmap(Context context) {
        if (this.mEmptyPhotoBitmap == null && context != null) {
            this.mEmptyPhotoBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_contact_picture)).getBitmap();
        }
        return this.mEmptyPhotoBitmap;
    }

    private int getCacheSize() {
        return ((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)) / 16;
    }
}
