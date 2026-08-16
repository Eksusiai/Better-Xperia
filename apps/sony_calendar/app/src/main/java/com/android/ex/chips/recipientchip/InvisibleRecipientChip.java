package com.android.ex.chips.recipientchip;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import com.android.ex.chips.RecipientEntry;

/* JADX INFO: loaded from: classes.dex */
public class InvisibleRecipientChip extends ReplacementSpan implements DrawableRecipientChip {
    private final SimpleRecipientChip mDelegate;

    @Override // com.android.ex.chips.recipientchip.DrawableRecipientChip
    public void draw(Canvas canvas) {
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        return 0;
    }

    public InvisibleRecipientChip(RecipientEntry recipientEntry) {
        this.mDelegate = new SimpleRecipientChip(recipientEntry);
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public void setSelected(boolean z) {
        this.mDelegate.setSelected(z);
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public boolean isSelected() {
        return this.mDelegate.isSelected();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getDisplay() {
        return this.mDelegate.getDisplay();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getValue() {
        return this.mDelegate.getValue();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public long getContactId() {
        return this.mDelegate.getContactId();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public Long getDirectoryId() {
        return this.mDelegate.getDirectoryId();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public String getLookupKey() {
        return this.mDelegate.getLookupKey();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public long getDataId() {
        return this.mDelegate.getDataId();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public RecipientEntry getEntry() {
        return this.mDelegate.getEntry();
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public void setOriginalText(String str) {
        this.mDelegate.setOriginalText(str);
    }

    @Override // com.android.ex.chips.recipientchip.BaseRecipientChip
    public CharSequence getOriginalText() {
        return this.mDelegate.getOriginalText();
    }

    @Override // com.android.ex.chips.recipientchip.DrawableRecipientChip
    public Rect getBounds() {
        return new Rect(0, 0, 0, 0);
    }
}
