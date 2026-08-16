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
public class Letter2 {
    private static final int COORDS_PER_UV = 2;
    private static final int COORDS_PER_VERTEX = 2;
    private static final String FRAGMENT_SHADER = "#version 100\nprecision highp float;\n#define SQRT_TWO    1.414213562\nuniform sampler2D uFillTexture;\nuniform sampler2D uBackgroundTexture;\nvarying vec2 vOutUVs;\nvarying vec2 vOutUVsMainShape;\n\nfloat rand(vec2 co){ \n   return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n}\nfloat interFunc1(float t) {\n    return 3.0*t*t - 2.0 * t*t*t;\n}\nfloat interFunc2(float t) {\n    return 6.0*pow(t, 5.0) - 15.0 * pow(t, 4.0) + 10.0 * pow(t, 3.0);\n}\nfloat perlinNoiseSimple(vec2 vl, float cellSz) {\n    float minStep = 1.0 / cellSz;\n    float maxLength = minStep * SQRT_TWO;\n    vec2 grid = floor(vl * cellSz) / cellSz;\n    vec2 gridPnt1 = grid;\n    vec2 gridPnt2 = vec2(grid.x, floor((vl.y + minStep) * cellSz) / cellSz);\n    vec2 gridPnt3 = vec2(floor((vl.x + minStep) * cellSz) / cellSz, grid.y);\n    vec2 gridPnt4 = vec2(gridPnt3.x, gridPnt2.y);\n    vec2 gradient1 = normalize(vec2(rand(gridPnt1), rand(gridPnt1.yx)) - 0.5);\n    vec2 gradient2 = normalize(vec2(rand(gridPnt2), rand(gridPnt2.yx)) - 0.5);\n    vec2 gradient3 = normalize(vec2(rand(gridPnt3), rand(gridPnt3.yx)) - 0.5);\n    vec2 gradient4 = normalize(vec2(rand(gridPnt4), rand(gridPnt4.yx)) - 0.5);\n    float s = dot(gradient1, (vl - gridPnt1) / maxLength);\n    float t = dot(gradient3, (vl - gridPnt3) / maxLength);\n    float u = dot(gradient2, (vl - gridPnt2) / maxLength);\n    float v = dot(gradient4, (vl - gridPnt4) / maxLength);\n    float x1 = interFunc2((vl.x - grid.x) * cellSz);\n    float interp1 = mix(s, t, x1);\n    float interp2 = mix(u, v, x1);\n    float y =interFunc2 ((vl.y - grid.y) * cellSz);\n    float interp3 = abs(mix(interp1, interp2, y));\n    float interp4 = clamp(mix(interp1, interp2, y), -1.0, 1.0);\n    return interp4;\n}\n\nvoid main() {\n    const vec4 AllOnes = vec4(1.0);\n    const vec4 AllZeros = vec4(0.0);\n    float threshold = 1.00;\n    vec4 bclClr = texture2D(uBackgroundTexture, vOutUVs);\n    vec4 mainClr = texture2D(uFillTexture, vOutUVsMainShape);\n    float presenceFill = sign(mainClr.g);\n    threshold += presenceFill * 1.0;\n    float offsetx = threshold * perlinNoiseSimple(vOutUVs, 1.5);\n    float offsety = threshold * perlinNoiseSimple(vOutUVsMainShape, 1.5);\n    vec2 offset = vec2(offsetx, offsety);\n    offsetx = threshold * perlinNoiseSimple(vOutUVs, 1.01);\n    offsety = -threshold * perlinNoiseSimple(vOutUVsMainShape, 1.01);\n    vec2 offset1 = vec2(offsetx, offsety);\n    offsetx = -threshold * perlinNoiseSimple(vOutUVs, 1.3);\n    offsety = threshold * perlinNoiseSimple(vOutUVsMainShape, 1.3);\n    vec2 offset2 = vec2(offsetx, offsety);\n    vec4 resClr = AllZeros;\n    float width = 0.35;\n    float thinness = 0.001;\n    float dist = width - thinness;\n    for (float i = width; i > thinness; i -= 0.02) {\n        mainClr = texture2D(uFillTexture, vOutUVsMainShape + i * offset);\n        resClr.a += 0.25 * 1.5 * (width - i) * floor(mainClr.r);\n    }\n    for (float i = width; i > thinness; i -= 0.02) {\n        mainClr = texture2D(uFillTexture, vOutUVsMainShape + i * offset1);\n        resClr.a += 0.25 * 1.5 * (width - i) * floor(mainClr.r);\n    }\n    for (float i = width; i > thinness; i -= 0.02) {\n        mainClr = texture2D(uFillTexture, vOutUVsMainShape + i * offset2);\n        resClr.a += 0.25 * 1.5 * (width - i) * floor(mainClr.r);\n    }\n    resClr = clamp(resClr, 0.0, 1.0);\n    gl_FragColor = vec4(mix(bclClr, AllOnes, resClr.a).rgb, 1.0);\n}\n";
    private static final int NUM_ATTRIB_BUFFERS = 2;
    private static final int NUM_UVS;
    private static final int NUM_VERTICES;
    static final String TAG = "com.sonymobile.generativeartwork.gl.Letter2";
    private static final float TEXTURE_TYPE_FONT_IMAGE = 0.0f;
    private static final float TEXTURE_TYPE_STOCK_IMAGE = 1.0f;
    private static final String VERTEX_SHADER = "#version 100\n#define SQRT_2 1.414213562\nattribute vec2 aCoords;\nattribute vec2 aUVs;\nuniform mat4 uGlobalTransform;\nuniform mat4 uMainShapeTransform;\nvarying vec2 vOutUVs;\nvarying vec2 vOutUVsMainShape;\nvoid main() {\n    vOutUVs = aUVs;\n    vec4 tUVs = vec4(aUVs.xy, 0.0, 1.0);\n    vec2 mainUVs = (uMainShapeTransform * tUVs).xy;\n    vOutUVsMainShape.x = mainUVs.x;\n    vOutUVsMainShape.y = 1.0 - mainUVs.y;\n    gl_Position = uGlobalTransform * vec4(aCoords, 0.0, 1.0);\n}\n";
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
    private int mStockImageId = -1;
    private boolean mIsTextureAllocated = false;
    private boolean mPerInstanceGLObjGenerated = false;
    private Bitmap mLettersBitmap = null;
    private Type mType = Type.UNDEFINED;
    private final float[] mMainShapeTransform = new float[16];
    private final float[][] mMainColor = (float[][]) Array.newInstance((Class<?>) float.class, 2, 4);
    private ShaderSettings mShdParams = new ShaderSettings(null);

    public enum Type {
        FONT_IMAGE,
        STOCK_IMAGE,
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
        public int uGlobalTransform;
        public int uMainShapeTransform;

        private ShaderSettings() {
            this.aCoords = -1;
            this.aUVs = -1;
            this.uFillTexture = -1;
            this.uBackgroundTexture = -1;
            this.uMainShapeTransform = -1;
            this.uGlobalTransform = -1;
        }

        /* synthetic */ ShaderSettings(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean checkIDs() {
            return (this.aCoords == -1 || this.aUVs == -1 || this.uFillTexture == -1 || this.uBackgroundTexture == -1 || this.uMainShapeTransform == -1 || this.uGlobalTransform == -1) ? false : true;
        }

        public String toString() {
            return "aCoords: " + this.aCoords + " aUVs: " + this.aUVs + " uFillTexture: " + this.uFillTexture + " uBackgroundTexture: " + this.uBackgroundTexture + " uMainShapeTransform: " + this.uMainShapeTransform + " uGlobalTransform: " + this.uGlobalTransform;
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
            this.mShdParams.uGlobalTransform = GLES20.glGetUniformLocation(this.mProgram, "uGlobalTransform");
            this.mShdParams.uMainShapeTransform = GLES20.glGetUniformLocation(this.mProgram, "uMainShapeTransform");
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
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$generativeartwork$gl$Letter2$Type[this.mType.ordinal()];
        if (i == 1) {
            if (this.mLettersBitmap != null) {
                GLES20.glBindTexture(3553, this.mLettersTextureID);
                if (!this.mIsTextureAllocated) {
                    GLUtils.texImage2D(3553, 0, this.mLettersBitmap, 0);
                    this.mIsTextureAllocated = true;
                } else {
                    GLUtils.texSubImage2D(3553, 0, 0, 0, this.mLettersBitmap);
                }
            }
        } else if (i == 2 && this.mStockImageId != -1) {
            GLES20.glBindTexture(3553, this.mStockImages.sStockTextures[this.mStockImageId]);
        }
        GLES20.glUniform1i(this.mShdParams.uFillTexture, 0);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.mFBAcess.getFBResult());
        GLES20.glUniform1i(this.mShdParams.uBackgroundTexture, 1);
        GLES20.glUniformMatrix4fv(this.mShdParams.uMainShapeTransform, 1, false, this.mMainShapeTransform, 0);
        GLES20.glDrawArrays(5, 0, NUM_VERTICES);
    }

    /* JADX INFO: renamed from: com.sonymobile.generativeartwork.gl.Letter2$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$generativeartwork$gl$Letter2$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$com$sonymobile$generativeartwork$gl$Letter2$Type = iArr;
            try {
                iArr[Type.FONT_IMAGE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$generativeartwork$gl$Letter2$Type[Type.STOCK_IMAGE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private void generatePerInstanceTexture() {
        int[] iArr = {-1};
        GLES20.glGenTextures(1, iArr, 0);
        Utils.checkGLErr();
        this.mLettersTextureID = iArr[0];
        GLES20.glActiveTexture(33984);
        Utils.checkGLErr();
        GLES20.glBindTexture(3553, this.mLettersTextureID);
        Utils.checkGLErr();
        GLES20.glTexParameteri(3553, 10241, 9728);
        Utils.checkGLErr();
        GLES20.glTexParameteri(3553, Data.MAX_DATA_BYTES, 9728);
        Utils.checkGLErr();
        GLES20.glTexParameteri(3553, 10242, 33071);
        Utils.checkGLErr();
        GLES20.glTexParameteri(3553, 10243, 33071);
        Utils.checkGLErr();
        GLES20.glGenBuffers(1, iArr, 0);
        Utils.checkGLErr();
        this.mPerInstanceGLObjGenerated = true;
    }

    public void resetTexture() {
        this.mIsTextureAllocated = false;
    }

    public void setTextureBitmap(Bitmap bitmap) {
        this.mLettersBitmap = bitmap;
        this.mStockImageId = -1;
        setLetterType(Type.FONT_IMAGE);
    }

    public void setTextureStockImageId(int i) {
        this.mStockImageId = i;
        this.mLettersBitmap = null;
        setLetterType(Type.STOCK_IMAGE);
    }

    private void setLetterType(Type type) {
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
