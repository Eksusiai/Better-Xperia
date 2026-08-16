package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ContactsAsyncHelper extends Handler {
    private static final boolean DBG = false;
    private static final int DEFAULT_TOKEN = -1;
    private static final int EVENT_LOAD_IMAGE = 1;
    private static final String LOG_TAG = "ContactsAsyncHelper";
    private static volatile ContactsAsyncHelper sInstance;
    private Handler mThreadHandler;

    private static final class WorkerArgs {
        public Context context;
        public int defaultResource;
        public Object result;
        public Uri uri;
        public ImageView view;

        private WorkerArgs() {
        }
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            InputStream inputStreamOpenContactPhotoInputStream;
            WorkerArgs workerArgs = (WorkerArgs) message.obj;
            if (message.arg1 == 1) {
                try {
                    inputStreamOpenContactPhotoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(workerArgs.context.getContentResolver(), workerArgs.uri);
                } catch (Exception e) {
                    Log.e(ContactsAsyncHelper.LOG_TAG, "Error opening photo input stream", e);
                    inputStreamOpenContactPhotoInputStream = null;
                }
                if (inputStreamOpenContactPhotoInputStream != null) {
                    workerArgs.result = Drawable.createFromStream(inputStreamOpenContactPhotoInputStream, workerArgs.uri.toString());
                } else {
                    workerArgs.result = null;
                }
            }
            Message messageObtainMessage = ContactsAsyncHelper.this.obtainMessage(message.what);
            messageObtainMessage.arg1 = message.arg1;
            messageObtainMessage.obj = message.obj;
            messageObtainMessage.sendToTarget();
        }
    }

    private ContactsAsyncHelper() {
        HandlerThread handlerThread = new HandlerThread("ContactsAsyncWorker");
        handlerThread.start();
        this.mThreadHandler = new WorkerHandler(handlerThread.getLooper());
    }

    public static ContactsAsyncHelper getInstance() {
        if (sInstance == null) {
            synchronized (ContactsAsyncHelper.class) {
                if (sInstance == null) {
                    sInstance = new ContactsAsyncHelper();
                }
            }
        }
        return sInstance;
    }

    public final void updateImageViewWithContactPhotoAsync(Context context, ImageView imageView, Uri uri, int i) {
        if (uri == null) {
            imageView.setVisibility(0);
            imageView.setImageResource(i);
            return;
        }
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.context = context;
        workerArgs.view = imageView;
        workerArgs.uri = uri;
        workerArgs.defaultResource = i;
        Message messageObtainMessage = this.mThreadHandler.obtainMessage(-1);
        messageObtainMessage.arg1 = 1;
        messageObtainMessage.obj = workerArgs;
        if (i != -1) {
            imageView.setVisibility(0);
            imageView.setImageResource(i);
        } else {
            imageView.setVisibility(4);
        }
        this.mThreadHandler.sendMessage(messageObtainMessage);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        WorkerArgs workerArgs = (WorkerArgs) message.obj;
        if (message.arg1 != 1) {
            return;
        }
        if (workerArgs.result != null) {
            workerArgs.view.setVisibility(0);
            workerArgs.view.setImageDrawable((Drawable) workerArgs.result);
        } else if (workerArgs.defaultResource != -1) {
            workerArgs.view.setVisibility(0);
            workerArgs.view.setImageResource(workerArgs.defaultResource);
        }
    }
}
