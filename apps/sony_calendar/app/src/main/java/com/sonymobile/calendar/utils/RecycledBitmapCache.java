package com.sonymobile.calendar.utils;

import android.graphics.Bitmap;
import android.util.Pair;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.ref.SoftReference;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class RecycledBitmapCache {
    private final Multimap<Integer, Pair<Integer, SoftReferenceWithHashCode>> mReusableBitmaps = HashMultimap.create();
    public final int COLOR_F2F2F2 = -855310;

    private static int hashWidthAndHeight(int i, int i2) {
        return (i << 16) + i2;
    }

    public void recycleBitmaps(Pair<Integer, Bitmap>... pairArr) {
        for (Pair<Integer, Bitmap> pair : pairArr) {
            if (pair.first != null && pair.second != null) {
                this.mReusableBitmaps.put(Integer.valueOf(hashWidthAndHeight(((Bitmap) pair.second).getWidth(), ((Bitmap) pair.second).getHeight())), new Pair<>(Integer.valueOf(((Integer) pair.first).intValue() % 3), new SoftReferenceWithHashCode((Bitmap) pair.second)));
            }
        }
    }

    public void recycleBitmaps(Bitmap... bitmapArr) {
        for (Bitmap bitmap : bitmapArr) {
            if (bitmap != null) {
                this.mReusableBitmaps.put(Integer.valueOf(hashWidthAndHeight(bitmap.getWidth(), bitmap.getHeight())), new Pair<>(3, new SoftReferenceWithHashCode(bitmap)));
            }
        }
    }

    public Bitmap getBitmap(int i, int i2, int i3) {
        Iterator<Pair<Integer, SoftReferenceWithHashCode>> it = this.mReusableBitmaps.get(Integer.valueOf(hashWidthAndHeight(i, i2))).iterator();
        while (it.hasNext()) {
            Pair<Integer, SoftReferenceWithHashCode> next = it.next();
            Bitmap bitmap = ((SoftReferenceWithHashCode) next.second).get();
            it.remove();
            if (bitmap != null && bitmap.getHeight() == i2 && bitmap.getWidth() == i) {
                if (((Integer) next.first).intValue() == i3 % 3) {
                    bitmap.eraseColor(-855310);
                    return bitmap;
                }
                return Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            }
        }
        return Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getBitmap(int i, int i2) {
        Iterator<Pair<Integer, SoftReferenceWithHashCode>> it = this.mReusableBitmaps.get(Integer.valueOf(hashWidthAndHeight(i, i2))).iterator();
        while (it.hasNext()) {
            Bitmap bitmap = ((SoftReferenceWithHashCode) it.next().second).get();
            it.remove();
            if (bitmap != null && bitmap.getHeight() == i2 && bitmap.getWidth() == i) {
                bitmap.eraseColor(-855310);
                return bitmap;
            }
        }
        return Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
    }

    private static class SoftReferenceWithHashCode extends SoftReference<Bitmap> {
        private volatile int mHashCode;

        private SoftReferenceWithHashCode(Bitmap bitmap) {
            super(bitmap);
            this.mHashCode = 0;
        }

        public boolean equals(Object obj) {
            Bitmap bitmap;
            if (super.equals(obj)) {
                return true;
            }
            return (obj instanceof SoftReferenceWithHashCode) && (bitmap = get()) != null && bitmap == ((SoftReferenceWithHashCode) obj).get();
        }

        public int hashCode() {
            if (this.mHashCode == 0) {
                Bitmap bitmap = get();
                this.mHashCode = bitmap != null ? bitmap.hashCode() : super.hashCode();
            }
            return this.mHashCode;
        }
    }
}
