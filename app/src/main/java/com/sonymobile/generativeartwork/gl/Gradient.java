package com.sonymobile.generativeartwork.gl;

import android.opengl.GLES20;
import com.sonymobile.generativeartwork.GenerativeArtWork;
import com.sonymobile.generativeartwork.utils.Utils;
import java.lang.reflect.Array;
import java.nio.FloatBuffer;

/* JADX INFO: loaded from: classes2.dex */
public class Gradient {
    private static final int COORDS_PER_VERTEX = 2;
    private static final String FRAGMENT_SHADER = "#version 100\nprecision highp float;\nuniform vec4 uColor1;\nuniform vec4 uColor2;\nuniform vec4 uColor3;\nuniform vec4 uColorDists;\nfloat rand(vec2 co){  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);}varying float gradientDist;\nvarying vec2 vUV;\nvoid main() {\n    vec4 resClr = uColor1;\n    if (gradientDist >= uColorDists[0] && gradientDist < uColorDists[1]) {\n       float t = (gradientDist - uColorDists[0]) / (uColorDists[1] - uColorDists[0]);\n       resClr = mix(uColor1, uColor2, t);\n    } else if (gradientDist >= uColorDists[1]) {\n       float t = (gradientDist - uColorDists[1]) / (uColorDists[2] - uColorDists[1]);\n       resClr = mix(uColor2, uColor3, t);\n    }\n       resClr.rgb += rand(vUV) * 0.007;\n    gl_FragColor = resClr;\n}\n";
    private static final int NUM_ATTRIB_BUFFERS = 1;
    private static final int NUM_VERTICES;
    private static final String TAG = "com.sonymobile.generativeartwork.gl.Gradient";
    private static final String VERTEX_SHADER = "#version 100\nattribute vec2 aCoords;\nuniform vec2 uGradientDirection;\nvarying float gradientDist;\nvarying vec2 vUV;\nvoid main() {\n    vec2 tmp = aCoords.xy * 0.5 + 0.5;\n    vUV = tmp;\n    tmp.y -= 1.0;\n    float gradientLength = length(uGradientDirection);\n    gradientDist = dot(tmp, uGradientDirection) / gradientLength;\n    gl_Position = vec4(aCoords, 0.0, 1.0);\n}\n";
    private static final float[] mBackgroundCoords;
    private static FloatBuffer mVertexBuffer;
    private ShaderSettings mShdParams = new ShaderSettings();
    private int[] mBuffers = new int[1];
    private int mVertexShader = -1;
    private int mFragmentShader = -1;
    private int mProgram = -1;
    private final float[][] mMainColor = (float[][]) Array.newInstance((Class<?>) float.class, 3, 4);
    private float[] mColorDists = {0.0f, 0.25f, 0.75f, 1.0f};
    private float[] mGradientDirection = {1.0f, 1.0f};

    static {
        float[] fArr = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        mBackgroundCoords = fArr;
        NUM_VERTICES = fArr.length / 2;
    }

    private static class ShaderSettings {
        public int aCoords;
        public int uColor1;
        public int uColor2;
        public int uColor3;
        public int uColorDists;
        public int uGradientDirection;

        private ShaderSettings() {
            this.aCoords = -1;
            this.uColorDists = -1;
            this.uColor1 = -1;
            this.uColor2 = -1;
            this.uColor3 = -1;
            this.uGradientDirection = -1;
        }

        public boolean checkIDs() {
            return (this.aCoords == -1 || this.uColor1 == -1 || this.uColorDists == -1 || this.uColor2 == -1 || this.uColor3 == -1 || this.uGradientDirection == -1) ? false : true;
        }

        public String toString() {
            return "aCoords: " + this.aCoords + " uColor1: " + this.uColor1 + " uColor1Dist: " + this.uColorDists + " uColor2: " + this.uColor2 + " uColor3: " + this.uColor3 + " uGradientDirection: " + this.uGradientDirection;
        }
    }

    public void init(GenerativeArtWork generativeArtWork) {
        if (mVertexBuffer == null) {
            FloatBuffer floatBufferWrap = FloatBuffer.wrap(mBackgroundCoords);
            mVertexBuffer = floatBufferWrap;
            floatBufferWrap.position(0);
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
            this.mShdParams.uColor1 = GLES20.glGetUniformLocation(this.mProgram, "uColor1");
            this.mShdParams.uColor2 = GLES20.glGetUniformLocation(this.mProgram, "uColor2");
            this.mShdParams.uColor3 = GLES20.glGetUniformLocation(this.mProgram, "uColor3");
            this.mShdParams.uColorDists = GLES20.glGetUniformLocation(this.mProgram, "uColorDists");
            this.mShdParams.uGradientDirection = GLES20.glGetUniformLocation(this.mProgram, "uGradientDirection");
            if (!this.mShdParams.checkIDs()) {
                throw new RuntimeException("Couldn't found one of the Shader params (attribute,uniform) for: " + TAG + " " + this.mShdParams);
            }
            GLES20.glGenBuffers(1, this.mBuffers, 0);
            GLES20.glBindBuffer(34962, this.mBuffers[0]);
            GLES20.glBufferData(34962, NUM_VERTICES * 8, mVertexBuffer, 35044);
            GLES20.glVertexAttribPointer(this.mShdParams.aCoords, 2, 5126, false, 0, 0);
            GLES20.glEnableVertexAttribArray(this.mShdParams.aCoords);
            GLES20.glBindBuffer(34962, 0);
            Utils.checkGLErr();
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
        GLES20.glDeleteBuffers(1, this.mBuffers, 0);
        mVertexBuffer.clear();
    }

    public void draw(Object obj) {
        GLES20.glUseProgram(this.mProgram);
        GLES20.glBindBuffer(34962, this.mBuffers[0]);
        GLES20.glVertexAttribPointer(this.mShdParams.aCoords, 2, 5126, false, 0, 0);
        GLES20.glEnableVertexAttribArray(this.mShdParams.aCoords);
        GLES20.glUniform4fv(this.mShdParams.uColor1, 1, this.mMainColor[0], 0);
        GLES20.glUniform4fv(this.mShdParams.uColor2, 1, this.mMainColor[1], 0);
        GLES20.glUniform4fv(this.mShdParams.uColor3, 1, this.mMainColor[2], 0);
        GLES20.glUniform4fv(this.mShdParams.uColorDists, 1, this.mColorDists, 0);
        GLES20.glUniform2fv(this.mShdParams.uGradientDirection, 1, this.mGradientDirection, 0);
        GLES20.glDrawArrays(5, 0, NUM_VERTICES);
    }

    public void setGradientColor(float[][] fArr) {
        int length = fArr.length;
        float[][] fArr2 = this.mMainColor;
        if (length == fArr2.length) {
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        }
    }

    public void setGradientRanges(float[] fArr) {
        float[] fArr2 = this.mColorDists;
        fArr2[0] = fArr[0];
        fArr2[1] = fArr[1];
        fArr2[2] = fArr[2];
        fArr2[3] = 0.0f;
    }

    public void setGradientAngle(float f) {
        float radians = (float) Math.toRadians(f);
        if (f <= 45.0f) {
            this.mGradientDirection[0] = 1.0f / ((float) Math.tan(radians));
            this.mGradientDirection[1] = -1.0f;
        } else {
            float[] fArr = this.mGradientDirection;
            fArr[0] = 1.0f;
            fArr[1] = -((float) Math.tan(radians));
        }
    }
}
