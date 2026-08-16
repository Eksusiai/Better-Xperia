package com.sonymobile.generativeartwork.layers;

import com.sonymobile.generativeartwork.GenerativeArtWork;
import com.sonymobile.generativeartwork.gl.Gradient;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes2.dex */
public final class BackgroundLayer implements GenerativeLayer, ColorChangeListener {
    private static final String TAG = "com.sonymobile.generativeartwork.layers.BackgroundLayer";
    private boolean isLayerInitialized = false;
    private float[][][] mColorPalette = (float[][][]) Array.newInstance((Class<?>) float.class, 0, 3, 4);
    private float[] mLayerSize = {0.0f, 0.0f};
    private Gradient mGradient = new Gradient();

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public boolean isOpenGLLayer() {
        return true;
    }

    BackgroundLayer() {
    }

    synchronized void initLayer(GenerativeArtWork generativeArtWork) {
        if (!this.isLayerInitialized) {
            this.mGradient.init(generativeArtWork);
        }
        this.isLayerInitialized = true;
    }

    synchronized void releaseLayer() {
        if (this.isLayerInitialized) {
            this.mGradient.release();
        }
        this.isLayerInitialized = false;
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public void draw(Object obj) {
        this.mGradient.draw(obj);
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public void setSize(int i, int i2) {
        float[] fArr = this.mLayerSize;
        fArr[0] = i;
        fArr[1] = i2;
    }

    void setGradientRanges(float[] fArr) {
        this.mGradient.setGradientRanges(fArr);
    }

    void setGradientAngle(float f) {
        this.mGradient.setGradientAngle(f);
    }

    void setColor(float[][] fArr) {
        this.mGradient.setGradientColor(fArr);
    }

    @Override // com.sonymobile.generativeartwork.layers.ColorChangeListener
    public void setColorPalette(float[][][] fArr) {
        float[][][] fArr2 = (float[][][]) Array.newInstance((Class<?>) float.class, fArr.length, 3, 4);
        this.mColorPalette = fArr2;
        System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        setColorPaletteId(0);
    }

    @Override // com.sonymobile.generativeartwork.layers.ColorChangeListener
    public void setColorPaletteId(int i) {
        float[][][] fArr = this.mColorPalette;
        if (i < fArr.length) {
            setColor(fArr[i]);
        }
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public LayerType getType() {
        return LayerType.BACKGROUND;
    }
}
