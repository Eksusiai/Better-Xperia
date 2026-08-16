package com.sonymobile.generativeartwork.layers;

/* JADX INFO: loaded from: classes2.dex */
public interface GenerativeLayer {

    public interface FBResultAccess {
        int getFBResult();
    }

    void draw(Object obj);

    LayerType getType();

    boolean isOpenGLLayer();

    void setSize(int i, int i2);
}
