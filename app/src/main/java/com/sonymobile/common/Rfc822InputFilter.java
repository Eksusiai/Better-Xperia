package com.sonymobile.common;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;

/* JADX INFO: loaded from: classes2.dex */
public class Rfc822InputFilter implements InputFilter {
    @Override // android.text.InputFilter
    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i2 - i == 1 && charSequence.charAt(i) == ' ') {
            boolean z = false;
            while (i3 > 0) {
                i3--;
                char cCharAt = spanned.charAt(i3);
                if (cCharAt == ',') {
                    break;
                }
                if (cCharAt == '.') {
                    z = true;
                } else if (cCharAt == '@') {
                    if (!z) {
                        return null;
                    }
                    if (!(charSequence instanceof Spanned)) {
                        return ", ";
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(RecurrenceRuleParser.VALUE_SEPARATOR);
                    spannableStringBuilder.append(charSequence);
                    return spannableStringBuilder;
                }
            }
        }
        return null;
    }
}
