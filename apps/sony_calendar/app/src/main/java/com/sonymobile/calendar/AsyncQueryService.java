package com.sonymobile.calendar;

import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class AsyncQueryService extends Handler {
    private static final String TAG = "AsyncQuery";
    static final boolean localLOGV = false;
    private static AtomicInteger mUniqueToken = new AtomicInteger(0);
    private Context mContext;
    private Handler mHandler = this;

    protected void onBatchComplete(int i, Object obj, ContentProviderResult[] contentProviderResultArr) {
    }

    protected void onDeleteComplete(int i, Object obj, int i2) {
    }

    protected void onInsertComplete(int i, Object obj, Uri uri) {
    }

    protected void onQueryComplete(int i, Object obj, Cursor cursor) {
    }

    protected void onUpdateComplete(int i, Object obj, int i2) {
    }

    public static class Operation {
        static final int EVENT_ARG_BATCH = 5;
        static final int EVENT_ARG_DELETE = 4;
        static final int EVENT_ARG_INSERT = 2;
        static final int EVENT_ARG_QUERY = 1;
        static final int EVENT_ARG_UPDATE = 3;
        public int op;
        public long scheduledExecutionTime;
        public int token;

        protected static char opToChar(int i) {
            if (i == 1) {
                return 'Q';
            }
            if (i == 2) {
                return 'I';
            }
            if (i == 3) {
                return 'U';
            }
            if (i != 4) {
                return i != 5 ? '?' : 'B';
            }
            return 'D';
        }

        public String toString() {
            return "Operation [op=" + this.op + ", token=" + this.token + ", scheduledExecutionTime=" + this.scheduledExecutionTime + "]";
        }
    }

    public AsyncQueryService(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public final int getNextToken() {
        return mUniqueToken.getAndIncrement();
    }

    public void startQuery(int i, Object obj, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        AsyncQueryServiceHelper.OperationInfo operationInfo = new AsyncQueryServiceHelper.OperationInfo();
        operationInfo.op = 1;
        operationInfo.resolver = this.mContext.getContentResolver();
        operationInfo.handler = this.mHandler;
        operationInfo.token = i;
        operationInfo.cookie = obj;
        operationInfo.uri = uri;
        operationInfo.projection = strArr;
        operationInfo.selection = str;
        operationInfo.selectionArgs = strArr2;
        operationInfo.orderBy = str2;
        AsyncQueryServiceHelper.queueOperation(this.mContext, operationInfo);
    }

    public void startUpdate(int i, Object obj, Uri uri, ContentValues contentValues, String str, String[] strArr, long j) {
        AsyncQueryServiceHelper.OperationInfo operationInfo = new AsyncQueryServiceHelper.OperationInfo();
        operationInfo.op = 3;
        operationInfo.resolver = this.mContext.getContentResolver();
        operationInfo.handler = this.mHandler;
        operationInfo.token = i;
        operationInfo.cookie = obj;
        operationInfo.uri = uri;
        operationInfo.values = contentValues;
        operationInfo.selection = str;
        operationInfo.selectionArgs = strArr;
        operationInfo.delayMillis = j;
        AsyncQueryServiceHelper.queueOperation(this.mContext, operationInfo);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        AsyncQueryServiceHelper.OperationInfo operationInfo = (AsyncQueryServiceHelper.OperationInfo) message.obj;
        int i = message.what;
        int i2 = message.arg1;
        if (i2 == 1) {
            onQueryComplete(i, operationInfo.cookie, (Cursor) operationInfo.result);
            return;
        }
        if (i2 == 2) {
            onInsertComplete(i, operationInfo.cookie, (Uri) operationInfo.result);
            return;
        }
        if (i2 == 3) {
            onUpdateComplete(i, operationInfo.cookie, ((Integer) operationInfo.result).intValue());
        } else if (i2 == 4) {
            onDeleteComplete(i, operationInfo.cookie, ((Integer) operationInfo.result).intValue());
        } else {
            if (i2 != 5) {
                return;
            }
            onBatchComplete(i, operationInfo.cookie, (ContentProviderResult[]) operationInfo.result);
        }
    }

    protected void setTestHandler(Handler handler) {
        this.mHandler = handler;
    }
}
