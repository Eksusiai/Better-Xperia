package com.sonymobile.generativeartwork.helper;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SymbolsUtils {
    private static String normalizeNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            int iDigit = Character.digit(cCharAt, 10);
            if (iDigit != -1) {
                sb.append(iDigit);
            } else if (sb.length() == 0 && cCharAt == '+') {
                sb.append(cCharAt);
            } else if ((cCharAt >= 'a' && cCharAt <= 'z') || (cCharAt >= 'A' && cCharAt <= 'Z')) {
                return normalizeNumber(PhoneNumberUtils.convertKeypadLettersToDigits(str));
            }
        }
        return sb.toString();
    }

    public static OutputSymbols getSymbolsFromPhoneNumber(String str) {
        OutputSymbols outputSymbols = new OutputSymbols();
        String strNormalizeNumber = normalizeNumber(str);
        if (TextUtils.isEmpty(strNormalizeNumber)) {
            return outputSymbols;
        }
        if (strNormalizeNumber.charAt(0) == '+') {
            strNormalizeNumber = strNormalizeNumber.substring(1);
        }
        strNormalizeNumber.getChars(0, 2 > strNormalizeNumber.length() ? strNormalizeNumber.length() : 2, outputSymbols.Symbols, 0);
        return outputSymbols;
    }

    public static OutputSymbols getSymbolsFromDisplayName(String str) {
        OutputSymbols outputSymbols = new OutputSymbols();
        if (TextUtils.isEmpty(str)) {
            return outputSymbols;
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(str)) {
            return getSymbolsFromPhoneNumber(str);
        }
        String[] strArrSplit = str.replaceAll("\\s+", " ").split(" ");
        if (strArrSplit.length < 2) {
            str.getChars(0, 2 > str.length() ? str.length() : 2, outputSymbols.Symbols, 0);
            return outputSymbols;
        }
        int length = 2 > strArrSplit.length ? strArrSplit.length : 2;
        for (int i = 0; i < length; i++) {
            if (!TextUtils.isEmpty(strArrSplit[i])) {
                outputSymbols.Symbols[i] = strArrSplit[i].charAt(0);
            } else {
                outputSymbols.Symbols[i] = ' ';
            }
        }
        return outputSymbols;
    }

    public static OutputSymbols getSymbolsFromEmail(String str) {
        OutputSymbols outputSymbols = new OutputSymbols();
        if (TextUtils.isEmpty(str)) {
            return outputSymbols;
        }
        int i = 0;
        for (int i2 = 0; i2 < str.length() && i < 2; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt != '@' && cCharAt != '.') {
                outputSymbols.Symbols[i] = cCharAt;
                i++;
            }
        }
        return outputSymbols;
    }

    public static OutputSymbols getSymbolsFromFields(String str, String str2, String str3) {
        OutputSymbols outputSymbols = new OutputSymbols();
        if (!TextUtils.isEmpty(str)) {
            return getSymbolsFromDisplayName(str);
        }
        if (TextUtils.isEmpty(str2)) {
            return !TextUtils.isEmpty(str3) ? getSymbolsFromEmail(str3) : outputSymbols;
        }
        return getSymbolsFromPhoneNumber(str2);
    }
}
