package com.android.ex.chips.recipientchip;

import android.text.TextUtils;
import com.android.ex.chips.RecipientEntry;

/* JADX INFO: loaded from: classes.dex */
class SimpleRecipientChip implements BaseRecipientChip {
    private final long mContactId;
    private final long mDataId;
    private final Long mDirectoryId;
    private final CharSequence mDisplay;
    private final RecipientEntry mEntry;
    private final String mLookupKey;
    private CharSequence mOriginalText;
    private boolean mSelected = false;
    private final CharSequence mValue;

    public SimpleRecipientChip(RecipientEntry recipientEntry) {
        this.mDisplay = recipientEntry.getDisplayName();
        this.mValue = recipientEntry.getDestination().trim();
        this.mContactId = recipientEntry.getContactId();
        this.mDirectoryId = recipientEntry.getDirectoryId();
        this.mLookupKey = recipientEntry.getLookupKey();
        this.mDataId = recipientEntry.getDataId();
        this.mEntry = recipientEntry;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public void setSelected(boolean z) {
        this.mSelected = z;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public boolean isSelected() {
        return this.mSelected;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getDisplay() {
        return this.mDisplay;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getValue() {
        return this.mValue;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public long getContactId() {
        return this.mContactId;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public Long getDirectoryId() {
        return this.mDirectoryId;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public String getLookupKey() {
        return this.mLookupKey;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public long getDataId() {
        return this.mDataId;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public RecipientEntry getEntry() {
        return this.mEntry;
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public void setOriginalText(String str) {
        if (TextUtils.isEmpty(str)) {
            this.mOriginalText = str;
        } else {
            this.mOriginalText = str.trim();
        }
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getOriginalText() {
        return !TextUtils.isEmpty(this.mOriginalText) ? this.mOriginalText : this.mEntry.getDestination();
    }

    public String toString() {
        return ((Object) this.mDisplay) + " <" + ((Object) this.mValue) + ">";
    }
}
