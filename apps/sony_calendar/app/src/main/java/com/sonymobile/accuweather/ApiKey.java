package com.sonymobile.accuweather;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import com.google.common.io.BaseEncoding;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: classes.dex */
public class ApiKey {
    private static final Object API_KEY_LOCK = new Object();
    public static String API_KEY_PART_A;
    public static String API_KEY_PART_B;
    public static byte[] API_KEY_PART_C;
    private static String sApiKey;

    public ApiKey(Context context) {
        synchronized (API_KEY_LOCK) {
            if (sApiKey == null) {
                sApiKey = getApiKey(context);
            }
        }
    }

    public String toString() {
        return sApiKey;
    }

    private static String getApiKey(Context context) {
        return "50c23ade03334399ba673352d7bc5cd4";
    }

    protected static String concatKey(String str, String str2, byte[] bArr, byte[] bArr2) {
        return new String(xor(BaseEncoding.base16().decode(str), bArr2)) + new String(xor(BaseEncoding.base16().decode(str2), bArr2)) + new String(xor(Base64.decode(bArr, 2), bArr2));
    }

    public static byte[] getSignatureSHA256(Context context) {
        Signature[] signingCertificateHistory;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 134217728);
                if (packageInfo.signingInfo.hasMultipleSigners()) {
                    signingCertificateHistory = packageInfo.signingInfo.getApkContentsSigners();
                } else {
                    signingCertificateHistory = packageInfo.signingInfo.getSigningCertificateHistory();
                }
            } else {
                signingCertificateHistory = packageManager.getPackageInfo(context.getPackageName(), 64).signatures;
            }
            if (signingCertificateHistory.length == 1) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
                messageDigest.update(signingCertificateHistory[0].toByteArray());
                return messageDigest.digest();
            }
            throw new SecurityException("App is not signed");
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
    }

    protected static byte[] xor(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr3[i] = (byte) (bArr[i] ^ bArr2[i % bArr2.length]);
        }
        return bArr3;
    }
}
