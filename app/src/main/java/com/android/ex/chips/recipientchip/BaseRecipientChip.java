package com.android.ex.chips.recipientchip;

import com.android.ex.chips.RecipientEntry;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes.dex */
public interface BaseRecipientChip {
    long getContactId();

    long getDataId();

    Long getDirectoryId();

    CharSequence getDisplay();

    RecipientEntry getEntry();

    String getLookupKey();

    CharSequence getOriginalText();

    CharSequence getValue();

    boolean isSelected();

    void setOriginalText(String str);

    void setSelected(boolean z);
}
