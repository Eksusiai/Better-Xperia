package com.sonymobile.calendar.linkedin.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInImageLoader {
    private static LinkedInImageLoader mInstance;
    private static ImageLoader.ImageCache sCache;
    private static RequestQueue sRequestQueue;
    private ImageLoader mImageLoader;

    private LinkedInImageLoader(Context context) {
        this.mImageLoader = new ImageLoader(getRequestQueue(context), getCache());
    }

    public static synchronized LinkedInImageLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LinkedInImageLoader(context);
        }
        return mInstance;
    }

    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return sRequestQueue;
    }

    public static synchronized ImageLoader.ImageCache getCache() {
        if (sCache == null) {
            sCache = new ImageLoader.ImageCache() { // from class: com.sonymobile.calendar.linkedin.model.LinkedInImageLoader.1
                private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                @Override // com.android.volley.toolbox.ImageLoader.ImageCache
                public Bitmap getBitmap(String str) {
                    return this.cache.get(str);
                }

                @Override // com.android.volley.toolbox.ImageLoader.ImageCache
                public void putBitmap(String str, Bitmap bitmap) {
                    this.cache.put(str, bitmap);
                }
            };
        }
        return sCache;
    }

    public <T> void addToRequestQueue(Request<T> request, Context context) {
        getRequestQueue(context).add(request);
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    public static void makeImageDownloadRequest(Context context, final ImageView imageView, String str) {
        getInstance(context).addToRequestQueue(new ImageRequest(str, new Response.Listener<Bitmap>() { // from class: com.sonymobile.calendar.linkedin.model.LinkedInImageLoader.2
            @Override // com.android.volley.Response.Listener
            public void onResponse(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }, 0, 0, null, null), context);
    }
}
