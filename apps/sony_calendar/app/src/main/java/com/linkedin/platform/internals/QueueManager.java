package com.linkedin.platform.internals;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/* JADX INFO: loaded from: classes.dex */
public class QueueManager {
    private static final String TAG = "com.linkedin.platform.internals.QueueManager";
    private static QueueManager queueManager;
    private Context ctx;
    private RequestQueue requestQueue;

    private QueueManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.ctx = applicationContext;
        this.requestQueue = Volley.newRequestQueue(applicationContext);
    }

    public static synchronized QueueManager getInstance(Context context) {
        if (queueManager == null) {
            queueManager = new QueueManager(context);
        }
        return queueManager;
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }
}
