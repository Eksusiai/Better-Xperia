package com.sonymobile.generativeartwork.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import androidx.work.Data;
import com.sonymobile.generativeartwork.layers.GenerativeLayer;
import com.sonymobile.generativeartwork.utils.Utils;
import java.lang.reflect.Array;
import java.nio.FloatBuffer;

/* JADX INFO: loaded from: classes2.dex */
public class Letter {
    private static final int COORDS_PER_UV = 2;
    private static final int COORDS_PER_VERTEX = 2;
    private static final String FRAGMENT_SHADER = "#version 100\nprecision highp float;\nuniform sampler2D uFillTexture;\nuniform sampler2D uBackgroundTexture;\nuniform sampler2D uStockImgTexture;\nuniform vec4 uFirstClr;\nuniform vec4 uSecondClr;\nuniform float uTextureType;\nvarying vec2 vOutUVs;\nvarying vec2 vOutUVsMainShape;\nvarying vec2 vOutUVsStockShape;\n\nvoid main() {\n    const vec4 AllOnes = vec4(1.0);\n    const vec4 AllZeros = vec4(0.0);\n    vec4 bclClr = texture2D(uBackgroundTexture, vOutUVs);\n    vec4 mainClr = texture2D(uFillTexture, vOutUVsMainShape);\n    float isFirstChrPresent = 0.0;\n    float isSecondChrPresent = 0.0;\n    if (uTextureType > 0.25) { \n         isSecondChrPresent = texture2D(uStockImgTexture, vOutUVsStockShape).a; \n         if (uTextureType > 0.75) { \n             isFirstChrPresent = mainClr.a; \n         } else { isFirstChrPresent = mainClr.g; } \n  } else { \n        isFirstChrPresent = sign(mainClr.g); \n        isSecondChrPresent = sign(mainClr.b); \n  } \n  vec4 resClr = (isFirstChrPresent * uFirstClr + isSecondChrPresent * uSecondClr);\n  resClr.rgb /= max(isFirstChrPresent + isSecondChrPresent, 1.);\n  resClr.a = min(resClr.a, 1.);\n  gl_FragColor = vec4(mix(bclClr, resClr, resClr.a).rgb, 1.);\n}\n";
    private static final int NUM_ATTRIB_BUFFERS = 2;
    private static final int NUM_UVS;
    private static final int NUM_VERTICES;
    static final String TAG = "com.sonymobile.generativeartwork.gl.Letter";
    private static final float TEXTURE_TYPE_FONT_IMAGE = 0.0f;
    private static final float TEXTURE_TYPE_FONT_STOCK_IMAGES = 0.5f;
    private static final float TEXTURE_TYPE_STOCK_IMAGES = 1.0f;
    private static final String VERTEX_SHADER = "#version 100\n#define SQRT_2 1.414213562\nattribute vec2 aCoords;\nattribute vec2 aUVs;\nuniform mat4 uGlobalTransform;\nuniform mat4 uMainShapeTransform;\nvarying vec2 vOutUVs;\nvarying vec2 vOutUVsMainShape;\nvarying vec2 vOutUVsStockShape;\nvoid main() {\n    vOutUVs = aUVs;\n    vec4 tUVs = vec4(aUVs.xy, 0.0, 1.0);\n    vec2 mainUVs = (uMainShapeTransform * tUVs).xy;\n    vOutUVsMainShape.x = mainUVs.x;\n    vOutUVsMainShape.y = 1.0 - mainUVs.y;\n    vOutUVsStockShape = vec2(aUVs.x, 1.0 - aUVs.y);    gl_Position = uGlobalTransform * vec4(aCoords, 0.0, 1.0);\n}\n";
    private static final float[] mBackgroundCoords;
    private static final float[] mBackgroundUVCoords;
    private static FloatBuffer mUVsBuffer;
    private static FloatBuffer mVertexBuffer;
    GenerativeLayer.FBResultAccess mFBAcess;
    private LetterStock mStockImages;
    private int[] mBuffers = new int[2];
    private int mVertexShader = -1;
    private int mFragmentShader = -1;
    private int mProgram = -1;
    private int mLettersTextureID = -1;
    private int mFirstStockImageId = -1;
    private int mSecondStockImageId = -1;
    private boolean mIsTextureAllocated = false;
    private boolean mPerInstanceGLObjGenerated = false;
    private Bitmap mLettersBitmap = null;
    private Type mType = Type.UNDEFINED;
    private final float[] mMainShapeTransform = new float[16];
    private final float[][] mMainColor = (float[][]) Array.newInstance((Class<?>) float.class, 2, 4);
    private ShaderSettings mShdParams = new ShaderSettings(null);

    public enum Type {
        FONT_IMAGE,
        STOCK_IMAGES,
        FONT_AND_STOCK_IMAGES,
        UNDEFINED
    }

    static {
        float[] fArr = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        mBackgroundCoords = fArr;
        float[] fArr2 = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        mBackgroundUVCoords = fArr2;
        NUM_VERTICES = fArr.length / 2;
        NUM_UVS = fArr2.length / 2;
    }

    private static class ShaderSettings {
        public int aCoords;
        public int aUVs;
        public int uBackgroundTexture;
        public int uFillTexture;
        public int uFirstClr;
        public int uGlobalTransform;
        public int uMainShapeTransform;
        public int uSecondClr;
        public int uStockImgTexture;
        public int uTextureType;

        private ShaderSettings() {
            this.aCoords = -1;
            this.aUVs = -1;
            this.uFillTexture = -1;
            this.uStockImgTexture = -1;
            this.uBackgroundTexture = -1;
            this.uMainShapeTransform = -1;
            this.uGlobalTransform = -1;
            this.uFirstClr = -1;
            this.uSecondClr = -1;
            this.uTextureType = -1;
        }

        /* synthetic */ ShaderSettings(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean checkIDs() {
            return (this.aCoords == -1 || this.aUVs == -1 || this.uFillTexture == -1 || this.uBackgroundTexture == -1 || this.uMainShapeTransform == -1 || this.uGlobalTransform == -1 || this.uFirstClr == -1 || this.uSecondClr == -1 || this.uTextureType == -1 || this.uStockImgTexture == -1) ? false : true;
        }

        public String toString() {
            return "aCoords: " + this.aCoords + " aUVs: " + this.aUVs + " uFillTexture: " + this.uFillTexture + " uBackgroundTexture: " + this.uBackgroundTexture + " uMainShapeTransform: " + this.uMainShapeTransform + " uGlobalTransform: " + this.uGlobalTransform + " uFirstClr: " + this.uFirstClr + " uSecondClr: " + this.uSecondClr + " uTextureType: " + this.uTextureType + " uStockImgTexture: " + this.uStockImgTexture;
        }
    }

    public void init(GenerativeLayer.FBResultAccess fBResultAccess, LetterStock letterStock) {
        this.mFBAcess = fBResultAccess;
        this.mStockImages = letterStock;
        if (mVertexBuffer == null) {
            FloatBuffer floatBufferWrap = FloatBuffer.wrap(mBackgroundCoords);
            mVertexBuffer = floatBufferWrap;
            floatBufferWrap.position(0);
        }
        if (mUVsBuffer == null) {
            FloatBuffer floatBufferWrap2 = FloatBuffer.wrap(mBackgroundUVCoords);
            mUVsBuffer = floatBufferWrap2;
            floatBufferWrap2.position(0);
        }
        this.mVertexShader = Utils.loadShader(35633, VERTEX_SHADER);
        int iLoadShader = Utils.loadShader(35632, FRAGMENT_SHADER);
        this.mFragmentShader = iLoadShader;
        int i = this.mVertexShader;
        if (i != 0 && iLoadShader != 0) {
            int iPrepareShaderProgram = Utils.prepareShaderProgram(i, iLoadShader);
            this.mProgram = iPrepareShaderProgram;
            if (iPrepareShaderProgram == -1) {
                throw new RuntimeException("Failed to create shader program for: " + TAG);
            }
            this.mShdParams.aCoords = GLES20.glGetAttribLocation(iPrepareShaderProgram, "aCoords");
            this.mShdParams.aUVs = GLES20.glGetAttribLocation(this.mProgram, "aUVs");
            this.mShdParams.uBackgroundTexture = GLES20.glGetUniformLocation(this.mProgram, "uBackgroundTexture");
            this.mShdParams.uFillTexture = GLES20.glGetUniformLocation(this.mProgram, "uFillTexture");
            this.mShdParams.uStockImgTexture = GLES20.glGetUniformLocation(this.mProgram, "uStockImgTexture");
            this.mShdParams.uGlobalTransform = GLES20.glGetUniformLocation(this.mProgram, "uGlobalTransform");
            this.mShdParams.uMainShapeTransform = GLES20.glGetUniformLocation(this.mProgram, "uMainShapeTransform");
            this.mShdParams.uFirstClr = GLES20.glGetUniformLocation(this.mProgram, "uFirstClr");
            this.mShdParams.uSecondClr = GLES20.glGetUniformLocation(this.mProgram, "uSecondClr");
            this.mShdParams.uTextureType = GLES20.glGetUniformLocation(this.mProgram, "uTextureType");
            if (!this.mShdParams.checkIDs()) {
                throw new RuntimeException("Couldn't found one of the Shader params (attribute,uniform) for: " + TAG + " " + this.mShdParams);
            }
            GLES20.glGenBuffers(2, this.mBuffers, 0);
            Utils.checkGLErr();
            GLES20.glBindBuffer(34962, this.mBuffers[0]);
            Utils.checkGLErr();
            GLES20.glBufferData(34962, NUM_VERTICES * 8, mVertexBuffer, 35044);
            Utils.checkGLErr();
            GLES20.glVertexAttribPointer(this.mShdParams.aCoords, 2, 5126, false, 0, 0);
            GLES20.glEnableVertexAttribArray(this.mShdParams.aCoords);
            GLES20.glBindBuffer(34962, this.mBuffers[1]);
            Utils.checkGLErr();
            GLES20.glBufferData(34962, NUM_UVS * 8, mUVsBuffer, 35044);
            Utils.checkGLErr();
            GLES20.glVertexAttribPointer(this.mShdParams.aUVs, 2, 5126, false, 0, 0);
            GLES20.glEnableVertexAttribArray(this.mShdParams.aUVs);
            GLES20.glBindBuffer(34962, 0);
            return;
        }
        throw new RuntimeException("Failed to create shader objects for: " + TAG);
    }

    public void release() {
        GLES20.glUseProgram(0);
        GLES20.glDetachShader(this.mProgram, this.mVertexShader);
        GLES20.glDetachShader(this.mProgram, this.mFragmentShader);
        GLES20.glDeleteProgram(this.mProgram);
        GLES20.glDeleteShader(this.mVertexShader);
        GLES20.glDeleteShader(this.mFragmentShader);
        GLES20.glDeleteBuffers(2, this.mBuffers, 0);
        mVertexBuffer.clear();
        mUVsBuffer.clear();
    }

    public void draw(Object obj) {
        GLES20.glUseProgram(this.mProgram);
        GLES20.glBindBuffer(34962, this.mBuffers[0]);
        GLES20.glVertexAttribPointer(this.mShdParams.aCoords, 2, 5126, false, 0, 0);
        GLES20.glEnableVertexAttribArray(this.mShdParams.aCoords);
        GLES20.glBindBuffer(34962, this.mBuffers[1]);
        GLES20.glVertexAttribPointer(this.mShdParams.aUVs, 2, 5126, false, 0, 0);
        GLES20.glEnableVertexAttribArray(this.mShdParams.aUVs);
        if (!this.mPerInstanceGLObjGenerated) {
            generatePerInstanceTexture();
        }
        GLES20.glUniformMatrix4fv(this.mShdParams.uGlobalTransform, 1, false, (float[]) obj, 0);
        GLES20.glActiveTexture(33984);
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$gl$Letter$Type[this.mType.ordinal()];
        float f = 0.0f;
        if (i != 1) {
            if (i == 2) {
                GLES20.glBindTexture(3553, this.mLettersTextureID);
                Bitmap bitmap = this.mLettersBitmap;
                if (bitmap != null) {
                    if (!this.mIsTextureAllocated) {
                        GLUtils.texImage2D(3553, 0, bitmap, 0);
                        this.mIsTextureAllocated = true;
                    } else {
                        GLUtils.texSubImage2D(3553, 0, 0, 0, bitmap);
                    }
                }
                GLES20.glUniform1i(this.mShdParams.uFillTexture, 0);
                GLES20.glActiveTexture(33986);
                GLES20.glBindTexture(3553, this.mStockImages.sStockTextures[0]);
                GLES20.glUniform1i(this.mShdParams.uStockImgTexture, 2);
            } else if (i == 3 && this.mFirstStockImageId != -1 && this.mSecondStockImageId != -1) {
                f = 1.0f;
                GLES20.glBindTexture(3553, this.mStockImages.sStockTextures[this.mFirstStockImageId]);
                GLES20.glUniform1i(this.mShdParams.uFillTexture, 0);
                GLES20.glActiveTexture(33986);
                GLES20.glBindTexture(3553, this.mStockImages.sStockTextures[this.mSecondStockImageId]);
                GLES20.glUniform1i(this.mShdParams.uStockImgTexture, 2);
            }
        } else if (this.mLettersBitmap != null && this.mFirstStockImageId != -1) {
            f = TEXTURE_TYPE_FONT_STOCK_IMAGES;
            GLES20.glBindTexture(3553, this.mLettersTextureID);
            if (!this.mIsTextureAllocated) {
                GLUtils.texImage2D(3553, 0, this.mLettersBitmap, 0);
                this.mIsTextureAllocated = true;
            } else {
                GLUtils.texSubImage2D(3553, 0, 0, 0, this.mLettersBitmap);
            }
            GLES20.glUniform1i(this.mShdParams.uFillTexture, 0);
            GLES20.glActiveTexture(33986);
            GLES20.glBindTexture(3553, this.mStockImages.sStockTextures[this.mFirstStockImageId]);
            GLES20.glUniform1i(this.mShdParams.uStockImgTexture, 2);
        }
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.mFBAcess.getFBResult());
        GLES20.glUniform1i(this.mShdParams.uBackgroundTexture, 1);
        GLES20.glUniformMatrix4fv(this.mShdParams.uMainShapeTransform, 1, false, this.mMainShapeTransform, 0);
        GLES20.glUniform4fv(this.mShdParams.uFirstClr, 1, this.mMainColor[0], 0);
        GLES20.glUniform4fv(this.mShdParams.uSecondClr, 1, this.mMainColor[1], 0);
        GLES20.glUniform1f(this.mShdParams.uTextureType, f);
        GLES20.glDrawArrays(5, 0, NUM_VERTICES);
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.gl.Letter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$gl$Letter$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$gl$Letter$Type = iArr;
            try {
                iArr[Type.FONT_AND_STOCK_IMAGES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$gl$Letter$Type[Type.FONT_IMAGE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$gl$Letter$Type[Type.STOCK_IMAGES.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private void generatePerInstanceTexture() {
        int[] iArr = {-1};
        GLES20.glGenTextures(1, iArr, 0);
        Utils.checkGLErr();
        this.mLettersTextureID = iArr[0];
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mLettersTextureID);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, Data.MAX_DATA_BYTES, 9728);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        this.mPerInstanceGLObjGenerated = true;
    }

    public void resetTexture() {
        this.mIsTextureAllocated = false;
    }

    public void setTextureBitmap(Bitmap bitmap) {
        this.mLettersBitmap = bitmap;
    }

    public void setTextureStockImageIds(int i, int i2) {
        this.mFirstStockImageId = i;
        this.mSecondStockImageId = i2;
    }

    public void setLetterType(Type type) {
        this.mType = type;
    }

    public void setLetterColor(float[][] fArr) {
        int length = fArr.length;
        float[][] fArr2 = this.mMainColor;
        if (length == fArr2.length) {
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        }
    }

    public void setMainShapeTransform(float[] fArr) {
        float[] fArr2 = this.mMainShapeTransform;
        if (fArr2.length == fArr.length) {
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        }
    }
}
