package com.sonymobile.calendar;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.LruCache;
import com.google.android.gms.maps.model.LatLng;

/* JADX INFO: loaded from: classes2.dex */
public class LocationCoordinatesCache {
    private LruCache<String, LatLng> mLatLngCache;

    private LocationCoordinatesCache() {
        this.mLatLngCache = new LruCache<>(((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)) / 128);
    }

    private static class InstanceHolder {
        private static final LocationCoordinatesCache INSTANCE = new LocationCoordinatesCache();

        private InstanceHolder() {
        }
    }

    public static LocationCoordinatesCache getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public LatLng getCoordinates(String str) {
        LruCache<String, LatLng> lruCache = this.mLatLngCache;
        if (lruCache == null) {
            return null;
        }
        return lruCache.get(str);
    }

    public void addCoordinates(String str, LatLng latLng) {
        synchronized (this.mLatLngCache) {
            if (this.mLatLngCache.get(str) == null) {
                this.mLatLngCache.put(str, latLng);
            }
        }
    }
}
