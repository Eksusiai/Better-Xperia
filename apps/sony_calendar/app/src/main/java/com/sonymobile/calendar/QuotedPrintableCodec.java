package com.sonymobile.calendar;

import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;

/* JADX INFO: loaded from: classes2.dex */
public class QuotedPrintableCodec {
    private static final byte ESCAPE_CHAR = 61;
    private static final BitSet PRINTABLE_CHARS = new BitSet(256);
    private static final byte SPACE = 32;
    private static final byte TAB = 9;

    static {
        for (int i = 33; i <= 60; i++) {
            PRINTABLE_CHARS.set(i);
        }
        for (int i2 = 62; i2 <= 126; i2++) {
            PRINTABLE_CHARS.set(i2);
        }
        BitSet bitSet = PRINTABLE_CHARS;
        bitSet.set(9);
        bitSet.set(32);
    }

    public static String encode(String str) {
        if (str == null) {
            return null;
        }
        return newStringUsAscii(encode(str.getBytes(Charsets.UTF_8)));
    }

    public static byte[] encode(byte[] bArr) {
        return encodeQuotedPrintable(PRINTABLE_CHARS, bArr);
    }

    private static void encodeQuotedPrintable(int i, ByteArrayOutputStream byteArrayOutputStream) {
        byteArrayOutputStream.write(61);
        char upperCase = Character.toUpperCase(Character.forDigit((i >> 4) & 15, 16));
        char upperCase2 = Character.toUpperCase(Character.forDigit(i & 15, 16));
        byteArrayOutputStream.write(upperCase);
        byteArrayOutputStream.write(upperCase2);
    }

    public static byte[] encodeQuotedPrintable(BitSet bitSet, byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        if (bitSet == null) {
            bitSet = PRINTABLE_CHARS;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i];
            if (i2 < 0) {
                i2 += 256;
            }
            if (bitSet.get(i2)) {
                byteArrayOutputStream.write(i2);
            } else {
                encodeQuotedPrintable(i2, byteArrayOutputStream);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String newStringUsAscii(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        return new String(bArr, Charsets.US_ASCII);
    }

    public static String decode(String str) {
        if (str == null) {
            return null;
        }
        return new String(decodeQuotedPrintable(getBytesUsAscii(str)), Charsets.UTF_8);
    }

    public static byte[] getBytesUsAscii(String str) {
        if (str == null) {
            return null;
        }
        return str.getBytes(Charsets.US_ASCII);
    }

    public static byte[] decodeQuotedPrintable(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            if (b == 61) {
                int i2 = i + 1;
                int iDigit16 = digit16(bArr[i2]);
                i = i2 + 1;
                byteArrayOutputStream.write((char) ((iDigit16 << 4) + digit16(bArr[i])));
            } else {
                byteArrayOutputStream.write(b);
            }
            i++;
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static int digit16(byte b) {
        return Character.digit((char) b, 16);
    }

    public static boolean needQPEncode(String str) {
        if (str == null) {
            return false;
        }
        return !new String(str.getBytes(Charsets.US_ASCII), Charsets.US_ASCII).equals(str);
    }
}
