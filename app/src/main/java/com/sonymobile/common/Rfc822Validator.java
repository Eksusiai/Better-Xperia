package com.sonymobile.common;

import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.widget.AutoCompleteTextView;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class Rfc822Validator implements AutoCompleteTextView.Validator {
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[^\\s@]+@([^\\s@\\.]+\\.)+[a-zA-z][a-zA-Z][a-zA-Z]*");
    private String mDomain;
    private boolean mRemoveInvalid = false;

    public Rfc822Validator(String str) {
        this.mDomain = str;
    }

    @Override // android.widget.AutoCompleteTextView.Validator
    public boolean isValid(CharSequence charSequence) {
        Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(charSequence);
        return rfc822TokenArr.length == 1 && EMAIL_ADDRESS_PATTERN.matcher(rfc822TokenArr[0].getAddress()).matches();
    }

    private String removeIllegalCharacters(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt > ' ' && cCharAt <= '~' && cCharAt != '(' && cCharAt != ')' && cCharAt != '<' && cCharAt != '>' && cCharAt != '@' && cCharAt != ',' && cCharAt != ';' && cCharAt != ':' && cCharAt != '\\' && cCharAt != '\"' && cCharAt != '[' && cCharAt != ']') {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    @Override // android.widget.AutoCompleteTextView.Validator
    public CharSequence fixText(CharSequence charSequence) {
        if (TextUtils.getTrimmedLength(charSequence) == 0) {
            return "";
        }
        Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(charSequence);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rfc822TokenArr.length; i++) {
            String address = rfc822TokenArr[i].getAddress();
            if (!this.mRemoveInvalid || isValid(address)) {
                int iIndexOf = address.indexOf(64);
                if (iIndexOf < 0) {
                    if (this.mDomain != null) {
                        rfc822TokenArr[i].setAddress(removeIllegalCharacters(address) + "@" + this.mDomain);
                    }
                } else {
                    String strRemoveIllegalCharacters = removeIllegalCharacters(address.substring(0, iIndexOf));
                    if (!TextUtils.isEmpty(strRemoveIllegalCharacters)) {
                        String strRemoveIllegalCharacters2 = removeIllegalCharacters(address.substring(iIndexOf + 1));
                        boolean z = strRemoveIllegalCharacters2.length() == 0;
                        if (!z || this.mDomain != null) {
                            Rfc822Token rfc822Token = rfc822TokenArr[i];
                            StringBuilder sbAppend = new StringBuilder().append(strRemoveIllegalCharacters).append("@");
                            if (z) {
                                strRemoveIllegalCharacters2 = this.mDomain;
                            }
                            rfc822Token.setAddress(sbAppend.append(strRemoveIllegalCharacters2).toString());
                        }
                    }
                }
                sb.append(rfc822TokenArr[i].toString());
                if (i + 1 < rfc822TokenArr.length) {
                    sb.append(", ");
                }
            }
        }
        return sb;
    }
}
