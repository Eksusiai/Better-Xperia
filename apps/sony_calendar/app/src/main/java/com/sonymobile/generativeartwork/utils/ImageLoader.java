package com.sonymobile.generativeartwork.utils;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import androidx.work.Data;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ImageLoader {
    private static final String TAG = "com.sonymobile.generativeartwork.utils.ImageLoader";

    public static int loadTextureFromAsset(String str, AssetManager assetManager, int i, Rect rect, boolean z) {
        try {
            InputStream inputStreamOpen = assetManager.open(str);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpen, null, options);
            rect.left = 0;
            rect.right = bitmapDecodeStream.getWidth();
            rect.top = 0;
            rect.bottom = bitmapDecodeStream.getHeight();
            int iLoadTextureFromBitmap = loadTextureFromBitmap(bitmapDecodeStream, i, z, false);
            bitmapDecodeStream.recycle();
            return iLoadTextureFromBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during loading texture from Asset");
        }
    }

    public static int loadTextureFromResource(int i, Resources resources, int i2, boolean z) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(resources, i, options);
            int iLoadTextureFromBitmap = loadTextureFromBitmap(bitmapDecodeResource, i2, z, false);
            bitmapDecodeResource.recycle();
            return iLoadTextureFromBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during loading texture from Resource");
        }
    }

    public static int loadTextureFromBitmap(Bitmap bitmap, int i, boolean z, boolean z2) {
        int[] iArr = {-1};
        if (z2) {
            try {
                Matrix matrix = new Matrix();
                matrix.preScale(1.0f, -1.0f);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error during loading texture from Resource");
            }
        }
        GLES20.glGenTextures(1, iArr, 0);
        Utils.checkGLErr();
        GLES20.glActiveTexture(i);
        Utils.checkGLErr();
        GLES20.glBindTexture(3553, iArr[0]);
        Utils.checkGLErr();
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        Utils.checkGLErr();
        if (z) {
            GLES20.glGenerateMipmap(3553);
            Utils.checkGLErr();
            GLES20.glTexParameteri(3553, 10241, 9987);
            Utils.checkGLErr();
            GLES20.glTexParameteri(3553, Data.MAX_DATA_BYTES, 9987);
            Utils.checkGLErr();
        } else {
            GLES20.glTexParameteri(3553, 10241, 9729);
            Utils.checkGLErr();
            GLES20.glTexParameteri(3553, Data.MAX_DATA_BYTES, 9729);
            Utils.checkGLErr();
        }
        GLES20.glTexParameteri(3553, 10242, 33071);
        Utils.checkGLErr();
        GLES20.glTexParameteri(3553, 10243, 33071);
        Utils.checkGLErr();
        bitmap.recycle();
        return iArr[0];
    }
}
