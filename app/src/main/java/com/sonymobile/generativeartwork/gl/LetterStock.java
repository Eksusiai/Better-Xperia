package com.sonymobile.generativeartwork.gl;

import android.content.Context;
import android.opengl.GLES20;
import androidx.work.Data;
import com.sonymobile.calendar.R;
import com.sonymobile.generativeartwork.utils.Utils;
import java.nio.FloatBuffer;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public final class LetterStock {
    private static final int COORDS_PER_VERTEX = 2;
    private static final String FRAGMENT_SHADER = "#version 100\nprecision highp float;\nvoid main() {\n    gl_FragColor = vec4(vec3(0.), 1.);\n}\n";
    private static final int NUM_ATTRIB_BUFFERS = 1;
    private static final String TAG = "com.sonymobile.generativeartwork.gl.LetterStock";
    private static final boolean USE_OLD_SOLUTION = false;
    private static final String VERTEX_SHADER = "#version 100\nattribute vec2 aCoords;\nvoid main() {\n    gl_Position = vec4(aCoords, 0.0, 1.0);\n}\n";
    public int[] sStockTextures;
    static final int[] STOCK_IMAGES = {R.drawable.stockimage01, R.drawable.stockimage02, R.drawable.stockimage03, R.drawable.stockimage04, R.drawable.stockimage05, R.drawable.stockimage06, R.drawable.stockimage07, R.drawable.stockimage08, R.drawable.stockimage09, R.drawable.stockimage10};
    private static final float MAX_IMG_SIDE = 1079.0f;
    private static float[][] sTriangles = {new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 830.0f, MAX_IMG_SIDE, 233.0f, 0.0f, 825.0f, 0.0f}, new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 865.0f, MAX_IMG_SIDE, 841.0f, 701.0f, 620.0f, 0.0f, 979.0f, 0.0f, MAX_IMG_SIDE, 686.0f}, new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 830.0f, MAX_IMG_SIDE, 716.0f, 0.0f, 945.0f, 0.0f}, new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 613.0f, MAX_IMG_SIDE, 551.0f, 0.0f, 898.0f, 0.0f, MAX_IMG_SIDE, 654.0f}, new float[]{370.0f, MAX_IMG_SIDE, 121.0f, MAX_IMG_SIDE, 198.0f, 0.0f, 603.0f, 0.0f}, new float[]{961.0f, MAX_IMG_SIDE, 528.0f, MAX_IMG_SIDE, 639.0f, 709.0f, 637.0f, 0.0f, 1069.0f, 0.0f}, new float[]{425.0f, MAX_IMG_SIDE, 177.0f, MAX_IMG_SIDE, 633.0f, 0.0f, 884.0f, 0.0f}, new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 830.0f, MAX_IMG_SIDE, 333.0f, 0.0f, 615.0f, 0.0f, MAX_IMG_SIDE, 294.0f}, new float[]{MAX_IMG_SIDE, MAX_IMG_SIDE, 830.0f, MAX_IMG_SIDE, 690.0f, 0.0f, 906.0f, 0.0f}, new float[]{737.0f, MAX_IMG_SIDE, 488.0f, MAX_IMG_SIDE, 732.0f, 0.0f, 947.0f, 0.0f}};

    static {
        for (int i = 0; i < sTriangles.length; i++) {
            int i2 = 0;
            while (true) {
                float[][] fArr = sTriangles;
                if (i2 < fArr[i].length) {
                    fArr[i][i2] = ((fArr[i][i2] / MAX_IMG_SIDE) * 2.0f) - 1.0f;
                    i2++;
                }
            }
        }
    }

    public static int getNumImages() {
        return STOCK_IMAGES.length;
    }

    private static class ShaderSettings {
        public int aCoords;

        private ShaderSettings() {
            this.aCoords = -1;
        }

        public boolean checkIDs() {
            return this.aCoords != -1;
        }

        public String toString() {
            return "aCoords: " + this.aCoords;
        }
    }

    public LetterStock() {
        int[] iArr = new int[STOCK_IMAGES.length];
        this.sStockTextures = iArr;
        Arrays.fill(iArr, -1);
    }

    public void init(Context context, int i, int i2) {
        int i3;
        ShaderSettings shaderSettings = new ShaderSettings();
        int iLoadShader = Utils.loadShader(35633, VERTEX_SHADER);
        int iLoadShader2 = Utils.loadShader(35632, FRAGMENT_SHADER);
        if (iLoadShader != 0 && iLoadShader2 != 0) {
            int iPrepareShaderProgram = Utils.prepareShaderProgram(iLoadShader, iLoadShader2);
            if (iPrepareShaderProgram == -1) {
                throw new RuntimeException("Failed to create shader program for: " + TAG);
            }
            shaderSettings.aCoords = GLES20.glGetAttribLocation(iPrepareShaderProgram, "aCoords");
            if (!shaderSettings.checkIDs()) {
                throw new RuntimeException("Couldn't found one of the Shader params (attribute,uniform) for: " + TAG + " " + shaderSettings);
            }
            GLES20.glUseProgram(iPrepareShaderProgram);
            int[] iArr = new int[1];
            int i4 = 0;
            GLES20.glGenBuffers(1, iArr, 0);
            int numImages = getNumImages();
            GLES20.glGenTextures(numImages, this.sStockTextures, 0);
            int i5 = 0;
            while (true) {
                i3 = 3553;
                if (i5 >= numImages) {
                    break;
                }
                GLES20.glBindTexture(3553, this.sStockTextures[i5]);
                Utils.checkGLErr();
                GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
                Utils.checkGLErr();
                GLES20.glTexParameteri(3553, 10241, 9729);
                Utils.checkGLErr();
                GLES20.glTexParameteri(3553, Data.MAX_DATA_BYTES, 9729);
                Utils.checkGLErr();
                GLES20.glTexParameteri(3553, 10242, 33071);
                Utils.checkGLErr();
                GLES20.glTexParameteri(3553, 10243, 33071);
                Utils.checkGLErr();
                i5++;
            }
            int[] iArr2 = new int[1];
            GLES20.glGenFramebuffers(1, iArr2, 0);
            Utils.checkGLErr();
            int i6 = iArr2[0];
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
            int i7 = 0;
            while (i7 < numImages) {
                GLES20.glBindFramebuffer(36160, i6);
                Utils.checkGLErr();
                GLES20.glFramebufferTexture2D(36160, 36064, i3, this.sStockTextures[i7], i4);
                Utils.checkGLErr();
                GLES20.glViewport(i4, i4, i, i2);
                Utils.checkGLErr();
                GLES20.glBindBuffer(34962, iArr[i4]);
                float[][] fArr = sTriangles;
                GLES20.glBufferData(34962, fArr[i7].length * 4, FloatBuffer.wrap(fArr[i7]), 35044);
                GLES20.glVertexAttribPointer(shaderSettings.aCoords, 2, 5126, false, 0, 0);
                GLES20.glEnableVertexAttribArray(shaderSettings.aCoords);
                GLES20.glClear(16384);
                GLES20.glDrawArrays(6, 0, sTriangles[i7].length / 2);
                i7++;
                i4 = 0;
                i3 = 3553;
            }
            GLES20.glUseProgram(i4);
            GLES20.glBindBuffer(34962, i4);
            GLES20.glBindFramebuffer(36160, i4);
            Utils.checkGLErr();
            iArr2[i4] = i6;
            GLES20.glDeleteFramebuffers(1, iArr2, i4);
            GLES20.glDeleteBuffers(1, iArr, i4);
            GLES20.glDetachShader(iPrepareShaderProgram, iLoadShader);
            GLES20.glDetachShader(iPrepareShaderProgram, iLoadShader2);
            GLES20.glDeleteProgram(iPrepareShaderProgram);
            GLES20.glDeleteShader(iLoadShader);
            GLES20.glDeleteShader(iLoadShader2);
            return;
        }
        throw new RuntimeException("Failed to create shader objects for: " + TAG);
    }

    public void release() {
        int[] iArr = this.sStockTextures;
        GLES20.glDeleteTextures(iArr.length, iArr, 0);
    }
}
