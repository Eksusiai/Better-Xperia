package com.sonyericsson.calendar.util;

import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: classes.dex */
public class SimpleIcsWriter {
    private static final int CHAR_MAX_BYTES_IN_UTF8 = 4;
    private static final int MAX_LINE_LENGTH = 75;
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private final ByteArrayOutputStream mOut = new ByteArrayOutputStream();

    public static boolean isFirstUtf8Byte(byte b) {
        return (b & 192) != 128;
    }

    void writeLine(String str) {
        int i = 0;
        for (byte b : toUtf8(str)) {
            if (i > 71 && isFirstUtf8Byte(b)) {
                this.mOut.write(13);
                this.mOut.write(10);
                this.mOut.write(9);
                i = 1;
            }
            this.mOut.write(b);
            i++;
        }
        this.mOut.write(13);
        this.mOut.write(10);
    }

    public void writeTag(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        if ("CALSCALE".equals(str) || CalendarConstants.METHOD.equals(str) || CalendarConstants.PRODID.equals(str) || CalendarConstants.VERSION.equals(str) || CalendarConstants.CATEGORIES.equals(str) || "CLASS".equals(str) || "COMMENT".equals(str) || CalendarConstants.DESCRIPTION.equals(str) || CalendarConstants.LOCATION.equals(str) || "RESOURCES".equals(str) || CalendarConstants.STATUS.equals(str) || CalendarConstants.SUMMARY.equals(str) || "TRANSP".equals(str) || CalendarConstants.TZID.equals(str) || "TZNAME".equals(str) || "CONTACT".equals(str) || "RELATED-TO".equals(str) || CalendarConstants.UID.equals(str) || "ACTION".equals(str) || "REQUEST-STATUS".equals(str) || "X-LIC-LOCATION".equals(str)) {
            str2 = escapeTextValue(str2);
        }
        writeLine(str + CalendarConstants.COLON + str2);
    }

    public String toString() {
        return fromUtf8(getBytes());
    }

    public byte[] getBytes() {
        try {
            this.mOut.flush();
        } catch (IOException unused) {
        }
        return this.mOut.toByteArray();
    }

    public static String quoteParamValue(String str) {
        if (str == null) {
            return null;
        }
        return "\"" + str.replace("\"", "'") + "\"";
    }

    static String escapeTextValue(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '\n') {
                sb.append("\\n");
            } else if (cCharAt != '\r') {
                if (cCharAt == ',' || cCharAt == ';' || cCharAt == '\\') {
                    sb.append('\\');
                    sb.append(cCharAt);
                } else {
                    sb.append(cCharAt);
                }
            }
        }
        return sb.toString();
    }

    private static byte[] encode(Charset charset, String str) {
        if (str == null) {
            return null;
        }
        ByteBuffer byteBufferEncode = charset.encode(CharBuffer.wrap(str));
        byte[] bArr = new byte[byteBufferEncode.limit()];
        byteBufferEncode.get(bArr);
        return bArr;
    }

    private static String decode(Charset charset, byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        CharBuffer charBufferDecode = charset.decode(ByteBuffer.wrap(bArr));
        return new String(charBufferDecode.array(), 0, charBufferDecode.length());
    }

    public static byte[] toUtf8(String str) {
        return encode(UTF_8, str);
    }

    public static String fromUtf8(byte[] bArr) {
        return decode(UTF_8, bArr);
    }
}
