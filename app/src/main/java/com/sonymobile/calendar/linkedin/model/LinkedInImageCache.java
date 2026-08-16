package com.sonymobile.calendar.linkedin.model;

import android.util.LruCache;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInImageCache {
    private static final int CACHE_SIZE = 10485760;
    private static LinkedInImageCache instance;
    private LruCache<String, LinkedInContact> cachedLinkedInContactLruCache = new LruCache<>(CACHE_SIZE);

    public static synchronized void killCache() {
        instance = null;
    }

    public static synchronized LinkedInImageCache getInstance() {
        if (instance == null) {
            instance = new LinkedInImageCache();
        }
        return instance;
    }

    private LinkedInImageCache() {
    }

    public LinkedInContact getContactWithEmail(String str) {
        return this.cachedLinkedInContactLruCache.get(str);
    }

    public void putContactInCache(LinkedInContact linkedInContact, String str) {
        this.cachedLinkedInContactLruCache.put(str, linkedInContact);
    }
}
