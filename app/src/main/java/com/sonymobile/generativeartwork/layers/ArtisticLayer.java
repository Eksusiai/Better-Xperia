package com.sonymobile.generativeartwork.layers;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.Matrix;
import com.sonymobile.generativeartwork.GenerativeArtWork;
import com.sonymobile.generativeartwork.gl.Letter;
import com.sonymobile.generativeartwork.gl.LetterStock;
import com.sonymobile.generativeartwork.helper.OutputSymbols;
import com.sonymobile.generativeartwork.language.LanguageData;
import com.sonymobile.generativeartwork.language.LanguageRules;
import com.sonymobile.generativeartwork.render.FontRenderer;
import com.sonymobile.generativeartwork.settings.LayerSettings;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public final class ArtisticLayer implements GenerativeLayer, ColorChangeListener {
    private static float DEFAULT_SIZE = 1080.0f;
    private static float SCALE_LETTERS = 0.9f;
    private static final String TAG = "com.sonymobile.generativeartwork.layers.ArtisticLayer";
    private final ArrayList<ColorChangeListener> mColorChangeListeners;
    private final FontRenderer mFontRenderer;
    private float[] mLayerSize;
    private final Letter mLetter;
    private final LetterStock mLetterStock;
    private float mLettersOffset;
    private final float[] mMainShapeTransform;
    private boolean mNeedFontRendering;
    private GenerativeArtWork mOwner = null;
    private boolean isLayerInitialized = false;
    private float mAngle = 0.0f;
    private float mLetter1XOffset = 0.0f;
    private float mLetter1YOffset = 0.0f;
    private float mLettersScale = 1.0f;
    private boolean mIsTransformRecalcNeeded = false;
    private float[][][] mColorPalette = (float[][][]) Array.newInstance((Class<?>) float.class, 0, 2, 4);

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public boolean isOpenGLLayer() {
        return true;
    }

    ArtisticLayer() {
        float f = DEFAULT_SIZE;
        this.mLayerSize = new float[]{f, f};
        this.mMainShapeTransform = new float[16];
        this.mColorChangeListeners = new ArrayList<>();
        this.mFontRenderer = new FontRenderer();
        this.mLetter = new Letter();
        this.mLetterStock = new LetterStock();
        this.mLettersOffset = 0.0f;
        this.mNeedFontRendering = true;
    }

    synchronized void initLayer(GenerativeArtWork generativeArtWork) {
        if (!this.isLayerInitialized) {
            LetterStock letterStock = this.mLetterStock;
            Context context = generativeArtWork.getContext();
            float[] fArr = this.mLayerSize;
            letterStock.init(context, (int) fArr[0], (int) fArr[1]);
            this.mLetter.init(generativeArtWork, this.mLetterStock);
            this.mOwner = generativeArtWork;
        }
        this.isLayerInitialized = true;
    }

    synchronized void releaseLayer() {
        if (this.isLayerInitialized) {
            this.mLetter.release();
            this.mLetterStock.release();
        }
        this.isLayerInitialized = false;
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public void draw(Object obj) {
        if (this.mNeedFontRendering) {
            this.mLetter.setTextureBitmap(this.mFontRenderer.overlapOnBitmap(this.mLettersOffset));
        }
        this.mLetter.draw(obj);
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public void setSize(int i, int i2) {
        FontRenderer fontRenderer = this.mFontRenderer;
        float f = i;
        float f2 = SCALE_LETTERS;
        float f3 = i2;
        fontRenderer.setCanvasSize((int) (f * f2), (int) (f2 * f3));
        float[] fArr = this.mLayerSize;
        fArr[0] = f;
        fArr[1] = f3;
        this.mLetter.resetTexture();
    }

    public int getBaseColor(OutputSymbols outputSymbols) {
        return getBaseColor(outputSymbols.Symbols[0], outputSymbols.Symbols[1]);
    }

    public int getBaseColor(char c, char c2) {
        int colorPaletteId = LanguageRules.getColorPaletteId(new LanguageData(c, c2), this.mColorPalette.length);
        LayerSettings settings = this.mOwner.getSettings();
        if (settings != null) {
            if (settings.containsKey(LayerSettings.Item.PaletteColors)) {
                int[] iArr = (int[]) settings.get(LayerSettings.Item.PaletteColors);
                if (colorPaletteId < iArr.length) {
                    return iArr[colorPaletteId];
                }
                throw new IllegalStateException("The generated palette Id is less than number of colors.");
            }
            throw new IllegalStateException("Settings don't contain the Palette.");
        }
        throw new IllegalStateException("There is no settings in the library.");
    }

    public void setLetters(OutputSymbols outputSymbols) {
        setLetters(outputSymbols.Symbols[0], outputSymbols.Symbols[1]);
    }

    public void setLetters(char c, char c2) {
        LanguageData languageData = new LanguageData(c, c2);
        if (languageData.isLanguageAllowed && languageData.numSkippedLetters < 2) {
            if (this.mIsTransformRecalcNeeded) {
                recalcArtTransformation();
                this.mIsTransformRecalcNeeded = false;
            }
            this.mFontRenderer.prepareShapes(languageData.mCharacter[0], languageData.mCharacter[1]);
            RectF boundsLetter = this.mFontRenderer.getBoundsLetter(0);
            this.mLettersOffset = (boundsLetter.right - boundsLetter.left) / 2.0f;
            if (languageData.numSkippedLetters > 0) {
                this.mLetter.setTextureStockImageIds(LanguageRules.getFirstStockImageId(languageData), -1);
                this.mLetter.setLetterType(Letter.Type.FONT_AND_STOCK_IMAGES);
            } else {
                this.mLetter.setLetterType(Letter.Type.FONT_IMAGE);
            }
            this.mNeedFontRendering = true;
        } else {
            this.mLetter.setTextureStockImageIds(LanguageRules.getFirstStockImageId(languageData), LanguageRules.getSecondStockImageId(languageData));
            this.mLetter.setLetterType(Letter.Type.STOCK_IMAGES);
            Matrix.setIdentityM(this.mMainShapeTransform, 0);
            this.mIsTransformRecalcNeeded = true;
            this.mNeedFontRendering = false;
        }
        this.mLetter.setMainShapeTransform(this.mMainShapeTransform);
        int colorPaletteId = LanguageRules.getColorPaletteId(languageData, this.mColorPalette.length);
        setColorPaletteId(colorPaletteId);
        Iterator<ColorChangeListener> it = this.mColorChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().setColorPaletteId(colorPaletteId);
        }
    }

    public void setArtTrasnformation(float f, float f2, float f3, float f4) {
        this.mAngle = f;
        this.mLetter1XOffset = f2;
        this.mLetter1YOffset = f3;
        this.mLettersScale = f4;
        recalcArtTransformation();
    }

    private void recalcArtTransformation() {
        Matrix.setIdentityM(this.mMainShapeTransform, 0);
        Matrix.scaleM(this.mMainShapeTransform, 0, 0.6666667f, 1.0f, 1.0f);
        Matrix.rotateM(this.mMainShapeTransform, 0, this.mAngle, 0.0f, 0.0f, 1.0f);
        Matrix.translateM(this.mMainShapeTransform, 0, 0.0f, -((float) (1.0d - Math.cos(Math.toRadians(this.mAngle)))), 0.0f);
        float[] fArr = this.mMainShapeTransform;
        float f = this.mLetter1XOffset;
        float f2 = this.mLettersScale;
        Matrix.translateM(fArr, 0, -(f / f2), -(this.mLetter1YOffset / f2), 0.0f);
        Matrix.translateM(this.mMainShapeTransform, 0, 0.0f, 1.0f - this.mLettersScale, 0.0f);
        float[] fArr2 = this.mMainShapeTransform;
        float f3 = this.mLettersScale;
        Matrix.scaleM(fArr2, 0, f3, f3, f3);
    }

    public void registerColorChangeListener(ColorChangeListener colorChangeListener) {
        this.mColorChangeListeners.add(colorChangeListener);
    }

    public void unregisterColorChangeListener(ColorChangeListener colorChangeListener) {
        this.mColorChangeListeners.remove(colorChangeListener);
    }

    @Override // com.sonymobile.generativeartwork.layers.ColorChangeListener
    public void setColorPalette(float[][][] fArr) {
        float[][][] fArr2 = (float[][][]) Array.newInstance((Class<?>) float.class, fArr.length, 2, 4);
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

    public void setColor(float[][] fArr) {
        this.mLetter.setLetterColor(fArr);
    }

    @Override // com.sonymobile.generativeartwork.layers.GenerativeLayer
    public LayerType getType() {
        return LayerType.ARTISTIC;
    }
}
