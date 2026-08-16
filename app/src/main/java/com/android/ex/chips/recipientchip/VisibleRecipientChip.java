package com.android.ex.chips.recipientchip;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.android.ex.chips.RecipientEntry;

/* JADX INFO: loaded from: classes.dex */
public class VisibleRecipientChip extends ReplacementDrawableSpan implements DrawableRecipientChip {
    private final SimpleRecipientChip mDelegate;

    public VisibleRecipientChip(Drawable drawable, RecipientEntry recipientEntry) {
        super(drawable);
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

    @Override // com.android.ex.chips.recipientchip.ReplacementDrawableSpan, com.android.ex.chips.recipientchip.DrawableRecipientChip
    public Rect getBounds() {
        return super.getBounds();
    }

    @Override // com.android.ex.chips.recipientchip.DrawableRecipientChip
    public void draw(Canvas canvas) {
        this.mDrawable.draw(canvas);
    }

    public String toString() {
        return this.mDelegate.toString();
    }
}
