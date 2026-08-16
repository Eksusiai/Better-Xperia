package com.sonyericsson.calendar.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.sonymobile.calendar.AsyncQueryService;
import com.sonymobile.calendar.Utils;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public abstract class AsyncServiceBase {
    private static Queue<OnReadyHandler> handlers = new LinkedList();
    private static boolean isDataBeingLoaded = false;
    private AsyncQueryService asyncQueryService;

    protected interface OnReadyHandler {
        void onReady(boolean z);
    }

    protected abstract boolean handleResultData(Cursor cursor);

    protected boolean isDataLoaded() {
        return !isDataBeingLoaded;
    }

    protected void performAsyncQuery(Context context, OnReadyHandler onReadyHandler, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (this.asyncQueryService == null) {
            initAsyncQueryService(context);
        }
        if (onReadyHandler != null) {
            handlers.add(onReadyHandler);
        }
        if (isDataBeingLoaded) {
            return;
        }
        isDataBeingLoaded = true;
        this.asyncQueryService.startQuery(this.asyncQueryService.getNextToken(), null, uri, strArr, str, strArr2, str2);
    }

    protected void performAsyncUpdate(Context context, OnReadyHandler onReadyHandler, Uri uri, ContentValues contentValues, String str, String[] strArr, long j) {
        if (this.asyncQueryService == null) {
            initAsyncQueryService(context);
        }
        if (onReadyHandler != null) {
            handlers.add(onReadyHandler);
        }
        this.asyncQueryService.startUpdate(this.asyncQueryService.getNextToken(), null, uri, contentValues, str, strArr, j);
    }

    private void initAsyncQueryService(Context context) {
        this.asyncQueryService = new AsyncHandlerService(context);
    }

    private class AsyncHandlerService extends AsyncQueryService {
        AsyncHandlerService(Context context) {
            super(context);
        }

        @Override // com.sonymobile.calendar.AsyncQueryService
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            if (cursor == null) {
                return;
            }
            cursor.moveToPosition(-1);
            boolean zHandleResultData = AsyncServiceBase.this.handleResultData(cursor);
            while (!AsyncServiceBase.handlers.isEmpty()) {
                ((OnReadyHandler) AsyncServiceBase.handlers.remove()).onReady(zHandleResultData);
            }
            boolean unused = AsyncServiceBase.isDataBeingLoaded = false;
            Utils.closeCursor(cursor);
        }
    }
}
