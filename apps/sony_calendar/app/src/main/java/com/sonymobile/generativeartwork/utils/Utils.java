package com.sonymobile.generativeartwork.utils;

import android.opengl.GLES20;
import android.util.Log;
import java.util.Random;

/* JADX INFO: loaded from: classes2.dex */
public class Utils {
    private static final String TAG = "com.sonymobile.generativeartwork.utils.Utils";
    private static Random mRndGen = new Random(0);

    public static int loadShader(int i, String str) {
        int[] iArr = new int[1];
        int iGlCreateShader = GLES20.glCreateShader(i);
        checkGLErr();
        GLES20.glShaderSource(iGlCreateShader, str);
        checkGLErr();
        GLES20.glCompileShader(iGlCreateShader);
        checkGLErr();
        GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
        checkGLErr();
        if (iArr[0] == 0) {
            String str2 = TAG;
            Log.e(str2, "Could not compile vertex shader:");
            Log.e(str2, GLES20.glGetShaderInfoLog(iGlCreateShader));
            Log.e(str2, "Chader src: \n" + str);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Shader compilation failed");
        }
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError == 0) {
            return iGlCreateShader;
        }
        Log.e(TAG, "Failed to load shader: " + iGlGetError);
        throw new RuntimeException("Shader loading failed");
    }

    public static int prepareShaderProgram(int i, int i2) {
        int[] iArr = new int[1];
        int iGlCreateProgram = GLES20.glCreateProgram();
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            Log.e(TAG, "Failed to create shader program: " + iGlGetError);
            throw new RuntimeException("Program creation has failed");
        }
        GLES20.glAttachShader(iGlCreateProgram, i);
        int iGlGetError2 = GLES20.glGetError();
        if (iGlGetError2 != 0) {
            Log.e(TAG, "Failed to attach vertex shader: " + iGlGetError2);
            throw new RuntimeException("Vertex attach has failed");
        }
        GLES20.glAttachShader(iGlCreateProgram, i2);
        int iGlGetError3 = GLES20.glGetError();
        if (iGlGetError3 != 0) {
            Log.e(TAG, "Failed to attach fragment shader: " + iGlGetError3);
            throw new RuntimeException("Fragment attach has failed");
        }
        GLES20.glLinkProgram(iGlCreateProgram);
        GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
        if (iArr[0] != 1) {
            String str = TAG;
            Log.e(str, "Could not link shader program: ");
            Log.e(str, GLES20.glGetProgramInfoLog(iGlCreateProgram));
            throw new RuntimeException("Program linking has failed");
        }
        int iGlGetError4 = GLES20.glGetError();
        if (iGlGetError4 != 0) {
            Log.e(TAG, "Failed to link program: " + iGlGetError4);
            throw new RuntimeException("Errors after program linking");
        }
        GLES20.glValidateProgram(iGlCreateProgram);
        GLES20.glGetProgramiv(iGlCreateProgram, 35715, iArr, 0);
        if (iArr[0] != 1) {
            String str2 = TAG;
            Log.e(str2, "Program validation has failed: ");
            Log.e(str2, GLES20.glGetProgramInfoLog(iGlCreateProgram));
            throw new RuntimeException("Program linking has failed");
        }
        String str3 = TAG;
        Log.d(str3, GLES20.glGetProgramInfoLog(iGlCreateProgram));
        int iGlGetError5 = GLES20.glGetError();
        if (iGlGetError5 == 0) {
            return iGlCreateProgram;
        }
        Log.e(str3, "Failed to validate program: " + iGlGetError5);
        throw new RuntimeException("Errors after program linking");
    }

    public static void checkGLErr() {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            Log.d(TAG, "OpenGL error: " + iGlGetError);
            Thread.dumpStack();
        }
    }

    public static int generateRand(char c, char c2, int i) {
        mRndGen.setSeed(((long) c) * ((long) c2));
        return mRndGen.nextInt(i);
    }

    public static int generateRand(long j, int i) {
        mRndGen.setSeed(j);
        return mRndGen.nextInt(i);
    }
}
